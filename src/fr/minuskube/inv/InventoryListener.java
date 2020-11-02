package fr.minuskube.inv;

import org.bukkit.event.Event;

import java.util.function.Consumer;

public class InventoryListener<T> {
    private final Class<T> type;
    private final Consumer<Event> consumer;

    public InventoryListener(Class<T> type, Consumer<Event> consumer) {
        this.type = type;
        this.consumer = consumer;
    }

    public void accept(Event t) {
        this.consumer.accept(t);
    }

    public Class<T> getType() {
        return this.type;
    }
}
