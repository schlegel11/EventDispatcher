package de.schlegel11.eventdispatcher;

import com.google.common.collect.Sets;

import java.util.EventListener;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by schlegel11 on 20.10.14.
 */
final class EventDispatcherList {

    public static final EventDispatcherList PSEUDO_EMPTY_DISPATCHER_LIST = new EventDispatcherList();
    private final Set<EventListenerWrapper> listeners = Sets.newHashSet();

    public boolean addListener(EventListener listener, int numberOfCalls) {
        return listeners.add(new EventListenerWrapper(listener, numberOfCalls));
    }

    public boolean removeListener(EventListener listener) {
        return listeners.removeIf(elw -> elw.getListener() == listener);
    }

    public int getListenerCount() {
        return listeners.size();
    }

    public int getListenerMaxCalls(EventListener listener) {
        EventListenerWrapper elw = getElw(listener);
        return Objects.isNull(elw) ? 0 : elw.getMaxCalls();
    }

    public int getListenerCurrentCalls(EventListener listener) {
        EventListenerWrapper elw = getElw(listener);
        return Objects.isNull(elw) ? 0 : elw.getCurrentCalls();
    }

    public boolean isEmpty() {
        return listeners.isEmpty();
    }

    public void fireEvent(Consumer<EventListener> consumer) {
        for (EventListenerWrapper elw : listeners) {
            consumer.accept(elw.getListener());
            elw.addCurrentCalls(elw.getMaxCalls() == EventListenerWrapper.INFINITE_CALLS ? 0 : 1);
        }
        listeners.removeIf(elw -> elw.getMaxCalls() == elw.getCurrentCalls());
    }

    @Override
    public int hashCode() {
        return Objects.hash(listeners);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final EventDispatcherList other = (EventDispatcherList) obj;
        return Objects.equals(this.listeners, other.listeners);
    }

    private EventListenerWrapper getElw(EventListener listener) {
        Stream<EventListenerWrapper> stream = listeners.stream().filter(elw -> elw.getListener() == listener);
        Optional<EventListenerWrapper> optional = stream.findFirst();
        return optional.isPresent() ? optional.get() : null;
    }
}
