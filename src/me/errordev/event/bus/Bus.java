package me.errordev.event.bus;

import me.errordev.event.handling.Target;
import me.errordev.util.Triple;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Hashtable;

public class Bus {

    private final Hashtable<Class<? extends Event>, Event> events = new Hashtable<>();
    private final ArrayList<Triple<Object, Listener<Event>, Class<?>>> listeners = new ArrayList<>();

    public <U extends Event> void registerEvent(final U event) {
        events.put(event.getClass(), event);
    }

    public final <U extends Event> void register(final Object object) {
        for (final Field field : object.getClass().getDeclaredFields()) {
            final boolean accessible = field.isAccessible();
            field.setAccessible(true);
            try {
                if (!field.isAnnotationPresent(Target.class))
                    continue;
                if (field.get(object) instanceof Listener) {
                    final Triple<Object, Listener<Event>, Class<?>> triple = new Triple<>(object, (Listener<Event>) field.get(object), (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
                    switch (field.getDeclaredAnnotation(Target.class).value()) {
                        case HIGH:
                            listeners.add(0, triple);
                            break;
                        case LOW:
                            listeners.add(triple);
                            break;
                        case MEDIUM:
                            listeners.add(Math.max(0, Math.min(listeners.size() - 1, listeners.size() / 2)), triple);
                            break;
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
            field.setAccessible(accessible);
        }
    }

    public void unregister(final Object object) {
        listeners.removeIf(tuple -> tuple.getFirst().equals(object));
    }

    public <U extends Event> U getEvent(final Class<? extends U> event) {
        return event.cast(events.getOrDefault(event, null));
    }

    public <U extends Event> U call(final Class<? extends U> event) {
        final U e = getEvent(event);
        for (Triple<Object, Listener<Event>, Class<?>> listener : listeners) {
            if (!listener.getThird().equals(event))
                continue;
            listener.getSecond().onEvent(e);
        }
        return e;
    }
}