package de.schlegel11.eventdispatcher;

import com.google.common.collect.Sets;

import java.util.EventListener;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by schlegel11 on 20.10.14.
 */
final class EventDispatcherList {

    public static final EventDispatcherList PSEUDO_EMPTY_DISPATCHER_LIST = new EventDispatcherList();
    private final Set<EventListener> listeners = Sets.newHashSet();

    public boolean addListener(EventListener listener) {
        return listeners.add(listener);
    }

    public boolean removeListener(EventListener listener) {
        return listeners.remove(listener);
    }

    public int getListenerCount() {
        return listeners.size();
    }

    public void fireEvent(Consumer<EventListener> consumer) {
        listeners.stream().forEach(consumer);
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
}
