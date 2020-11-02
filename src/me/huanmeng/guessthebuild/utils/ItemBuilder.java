package me.huanmeng.guessthebuild.utils;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ItemBuilder {
    private final ItemStack itemStack;
    private boolean firsted = false;

    private List<String> lores;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        lores = new ArrayList<>();
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.lores = new ArrayList<>();
    }

    public ItemBuilder(Material material, int amount, byte durability) {
        this.itemStack = new ItemStack(material, amount, durability);
        this.lores = new ArrayList<>();
    }

    public ItemBuilder clone() {
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder setDurability(short durability) {
        this.itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.spigot().setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
        this.itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        this.itemStack.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        SkullMeta im = (SkullMeta) this.itemStack.getItemMeta();
        im.setOwner(owner);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setEnchantMeta(Map<Enchantment, Integer> enchantments) {
        EnchantmentStorageMeta im = (EnchantmentStorageMeta) this.itemStack.getItemMeta();
        Iterator var3 = enchantments.entrySet().iterator();

        while (var3.hasNext()) {
            Entry<Enchantment, Integer> m = (Entry) var3.next();
            im.addStoredEnchant(m.getKey(), m.getValue(), true);
        }

        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        ItemMeta im = this.itemStack.getItemMeta();
        im.addEnchant(enchantment, level, true);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setInfinityDurability() {
        this.itemStack.setDurability((short) 32767);
        return this;
    }

    public ItemBuilder addLore(String... lores) {
        for (String lore : lores) {
            this.lores.add(lore);
        }
        return this;
    }

//    public ItemBuilder setDyeColor(DyeColor color) {
//        this.itemStack.setDurability(color.getData());
//        return this;
//    }

    public ItemBuilder addGlow() {
        ItemMeta im = this.itemStack.getItemMeta();
        im.addEnchant(Enchantment.DURABILITY, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addPotion(PotionEffect potionEffect) {
        PotionMeta im = (PotionMeta) this.itemStack.getItemMeta();
        im.addCustomEffect(potionEffect, true);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemStack build() {
        ItemMeta im = this.itemStack.getItemMeta();
        List<String> a = new ArrayList<>();
        for (String s : this.lores) {
            a.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        im.setLore(a);
        this.itemStack.setItemMeta(im);
        return this.itemStack;
    }

    public List<String> getLores() {
        return lores;
    }

    public ItemBuilder FirstLine(String... s) {
        if (firsted) return this;
        List<String> old = new ArrayList<>();
        old.addAll(this.lores);
        this.lores.clear();
        for (String s1 : s) {
            this.lores.add(s1);
            firsted = true;
        }
        this.lores.addAll(old);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        this.lores.addAll(lore);
        return this;
    }

    public ItemBuilder setLore(String... info) {
        return addLore(info);
    }

    public ItemBuilder setLore(List<String> info) {
        return addLore(info);
    }

    public ItemBuilder clearLore() {
        this.lores.clear();
        return this;
    }
    public ItemBuilder setDyeColor(DyeColor dyeColor){
        itemStack.setDurability(dyeColor.getDyeData());
        return this;
    }
    public ItemBuilder addPattern(Pattern pattern){
        BannerMeta bannerMeta= (BannerMeta) itemStack.getItemMeta();
        bannerMeta.addPattern(pattern);
        itemStack.setItemMeta(bannerMeta);
        return this;
    }

    public Material getType() {
        return itemStack.getType();
    }
}
