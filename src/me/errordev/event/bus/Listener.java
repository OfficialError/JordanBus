package me.errordev.event.bus;

public interface Listener<T extends Event> {

    void onEvent(T event);

}