package fr.minuskube.inv;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.opener.ChestInventoryOpener;
import fr.minuskube.inv.opener.InventoryOpener;
import fr.minuskube.inv.opener.SpecialInventoryOpener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class InventoryManager {
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final Map<Player, SmartInventory> inventories;
    private final Map<Player, InventoryContents> contents;
    private final List<InventoryOpener> defaultOpeners;
    private final List<InventoryOpener> openers;

    public InventoryManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pluginManager = Bukkit.getPluginManager();
        this.inventories = new HashMap();
        this.contents = new HashMap();
        this.defaultOpeners = Arrays.asList(new ChestInventoryOpener(), new SpecialInventoryOpener());
        this.openers = new ArrayList();
    }

    public void init() {
        this.pluginManager.registerEvents(new InvListener(), this.plugin);
        (new InvTask()).runTaskTimer(this.plugin, 0, 0);
    }

    public Optional<InventoryOpener> findOpener(InventoryType type) {
        Optional<InventoryOpener> opInv = this.openers.stream().filter((opener) -> {
            return opener.supports(type);
        }).findAny();
        if (!opInv.isPresent()) {
            opInv = this.defaultOpeners.stream().filter((opener) -> {
                return opener.supports(type);
            }).findAny();
        }

        return opInv;
    }

    public void registerOpeners(InventoryOpener... openers) {
        this.openers.addAll(Arrays.asList(openers));
    }

    public List<Player> getOpenedPlayers(SmartInventory inv) {
        List<Player> list = new ArrayList();
        this.inventories.forEach((player, playerInv) -> {
            if (inv.equals(playerInv)) {
                list.add(player);
            }

        });
        return list;
    }

    public Optional<SmartInventory> getInventory(Player p) {
        return Optional.ofNullable(this.inventories.get(p));
    }

    protected void setInventory(Player p, SmartInventory inv) {
        if (inv == null) {
            this.inventories.remove(p);
        } else {
            this.inventories.put(p, inv);
        }

    }

    public Optional<InventoryContents> getContents(Player p) {
        return Optional.ofNullable(this.contents.get(p));
    }

    protected void setContents(Player p, InventoryContents contents) {
        if (contents == null) {
            this.contents.remove(p);
        } else {
            this.contents.put(p, contents);
        }

    }

    class InvTask extends BukkitRunnable {
        InvTask() {
        }

        public void run() {
            inventories.forEach((player, inv) -> {
                inv.getProvider().update(player, InventoryManager.this.contents.get(player));
            });
        }
    }

    class InvListener implements Listener {
        InvListener() {
        }

        @EventHandler(
                priority = EventPriority.LOW
        )
        public void onInventoryClick(InventoryClickEvent e) {
            Player p = (Player) e.getWhoClicked();
            if (InventoryManager.this.inventories.containsKey(p)) {
                if (e.getAction() != InventoryAction.COLLECT_TO_CURSOR && e.getAction() != InventoryAction.NOTHING) {
                    if (e.getClickedInventory() == p.getOpenInventory().getTopInventory()) {
                        e.setCancelled(true);
                        int row = e.getSlot() / 9;
                        int column = e.getSlot() % 9;
                        if (row < 0 || column < 0) {
                            return;
                        }

                        SmartInventory inv = InventoryManager.this.inventories.get(p);
                        if (row >= inv.getRows() || column >= inv.getColumns()) {
                            return;
                        }

                        inv.getListeners().stream().filter((listener) -> {
                            return listener.getType() == InventoryClickEvent.class;
                        }).forEach((listener) -> {
                            listener.accept(e);
                        });
                        InventoryManager.this.contents.get(p).get(row, column).ifPresent((item) -> {
                            item.run(e);
                        });
                        p.updateInventory();
                    }

                } else {
                    e.setCancelled(true);
                }
            }
        }

        @EventHandler(
                priority = EventPriority.LOW
        )
        public void onInventoryDrag(InventoryDragEvent e) {
            Player p = (Player) e.getWhoClicked();
            if (InventoryManager.this.inventories.containsKey(p)) {
                SmartInventory inv = InventoryManager.this.inventories.get(p);

                for (int slot : e.getRawSlots()) {
                    if (slot < p.getOpenInventory().getTopInventory().getSize()) {
                        e.setCancelled(true);
                        break;
                    }
                }

                inv.getListeners().stream().filter((listener) -> {
                    return listener.getType() == InventoryDragEvent.class;
                }).forEach((listener) -> {
                    listener.accept(e);
                });
            }
        }

        @EventHandler(
                priority = EventPriority.LOW
        )
        public void onInventoryOpen(InventoryOpenEvent e) {
            Player p = (Player) e.getPlayer();
            if (InventoryManager.this.inventories.containsKey(p)) {
                SmartInventory inv = InventoryManager.this.inventories.get(p);
                inv.getListeners().stream().filter((listener) -> {
                    return listener.getType() == InventoryOpenEvent.class;
                }).forEach((listener) -> {
                    listener.accept(e);
                });
            }
        }

        @EventHandler(
                priority = EventPriority.LOW
        )
        public void onInventoryClose(InventoryCloseEvent e) {
            Player p = (Player) e.getPlayer();
            if (InventoryManager.this.inventories.containsKey(p)) {
                SmartInventory inv = InventoryManager.this.inventories.get(p);
                inv.getListeners().stream().filter((listener) -> {
                    return listener.getType() == InventoryCloseEvent.class;
                }).forEach((listener) -> {
                    listener.accept(e);
                });
                if (inv.isCloseable()) {
                    e.getInventory().clear();
                    InventoryManager.this.inventories.remove(p);
                    InventoryManager.this.contents.remove(p);
                } else {
                    Bukkit.getScheduler().runTask(InventoryManager.this.plugin, () -> {
                        p.openInventory(e.getInventory());
                    });
                }

            }
        }

        @EventHandler(
                priority = EventPriority.LOW
        )
        public void onPlayerQuit(PlayerQuitEvent e) {
            Player p = e.getPlayer();
            if (InventoryManager.this.inventories.containsKey(p)) {
                SmartInventory inv = InventoryManager.this.inventories.get(p);
                inv.getListeners().stream().filter((listener) -> {
                    return listener.getType() == PlayerQuitEvent.class;
                }).forEach((listener) -> {
                    listener.accept(e);
                });
                InventoryManager.this.inventories.remove(p);
                InventoryManager.this.contents.remove(p);
            }
        }

        @EventHandler(
                priority = EventPriority.LOW
        )
        public void onPluginDisable(PluginDisableEvent e) {
            inventories.forEach((player, inv) -> {
                inv.getListeners().stream().filter((listener) -> {
                    return listener.getType() == PluginDisableEvent.class;
                }).forEach((listener) -> {
                    listener.accept(e);
                });
                inv.close(player);
            });
            InventoryManager.this.inventories.clear();
            InventoryManager.this.contents.clear();
        }
    }
}
