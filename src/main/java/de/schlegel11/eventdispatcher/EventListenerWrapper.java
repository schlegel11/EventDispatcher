package de.schlegel11.eventdispatcher;

import java.util.EventListener;
import java.util.Objects;

/**
 * Created by schlegel11 on 05.11.14.
 */
final class EventListenerWrapper {

    public final static int INFINITE_CALLS = -1;
    private final EventListener listener;
    private final int maxCalls;
    private int currentCalls;

    public EventListenerWrapper(EventListener listener, int maxCalls) {
        this.listener = listener;
        this.maxCalls = maxCalls;
    }

    public EventListener getListener() {
        return listener;
    }

    public int getMaxCalls() {
        return maxCalls;
    }

    public int getCurrentCalls() {
        return currentCalls;
    }

    public void addCurrentCalls(int currentCalls) {
        this.currentCalls += currentCalls;
    }

    @Override
    public int hashCode() {
        return Objects.hash(listener);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final EventListenerWrapper other = (EventListenerWrapper) obj;
        return Objects.equals(this.listener, other.listener);
    }
}
