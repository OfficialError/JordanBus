import me.errordev.event.bus.Bus;
import me.errordev.event.bus.Listener;
import me.errordev.event.handling.Target;

public enum Main {

    INSTANCE;

    public static void main(String[] args) {
        final Bus bus = new Bus();
        bus.registerEvent(new TestEvent());
        bus.register(INSTANCE);
        final long last = System.nanoTime();
        final int iterations = 1000000;
        for (int i = 0; i < iterations; i++)
            bus.call(TestEvent.class);
        System.out.println("Time: " + (System.nanoTime() - last) / 10000000.0 + "ms");
    }

    @Target
    public final Listener<TestEvent> eventListener = e -> {
    };


}