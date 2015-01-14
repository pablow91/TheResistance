package eu.stosdev.theresistance.utils;

import com.squareup.otto.Bus;

import javax.inject.Inject;

public class TypedBus<T> {

    private final Bus bus = new Bus();

    @Inject
    public TypedBus() {
    }

    public void post(T event) {
        bus.post(event);
    }

    public void register(Object object) {
        bus.register(object);
    }

    public void unregister(Object object) {
        bus.unregister(object);
    }
}
