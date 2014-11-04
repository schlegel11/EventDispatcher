package de.schlegel11.eventdispatcher;

import com.google.common.collect.Maps;

import java.util.EventListener;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Class that provides an easy to use event dispatcher.
 * <p>
 *
 * @author Marcel Schlegel (schlegel11) on 20.10.14
 * @version 1.0
 */
public final class EventDispatcher {

    private static final String IS_NULL = "Argument is null.";
    private final Map<Class<? extends EventListener>, EventDispatcherList> dispatchers = Maps.newHashMap();

    private EventDispatcher() {
    }

    /**
     * Factory method that creates a new {@link de.schlegel11.eventdispatcher.EventDispatcher} instance.
     *
     * @return A new {@link de.schlegel11.eventdispatcher.EventDispatcher} instance.
     */
    public static EventDispatcher newInstance() {
        return new EventDispatcher();
    }

    /**
     * Adds a new {@link java.util.EventListener} class instance.
     * <p>
     * The {@link java.util.EventListener} class instance is assigned to a specific {@link java.util.EventListener} class type.
     * <br>
     * A specific {@link java.util.EventListener} class type assigns many {@link java.util.EventListener} class instances.
     *
     * @param clazz    Represents a specific unique {@link java.util.EventListener} class type.
     *                 If param is {@code null} an {@link java.lang.NullPointerException} is thrown.
     * @param listener Represents a specific {@link java.util.EventListener} instance.
     *                 If param is {@code null} an {@link java.lang.NullPointerException} is thrown.
     * @return True if the current {@link de.schlegel11.eventdispatcher.EventDispatcher} did not already contain the specific {@code listener} instance.
     */
    public boolean addListener(Class<? extends EventListener> clazz, EventListener listener) {
        Objects.requireNonNull(clazz, IS_NULL);
        Objects.requireNonNull(listener, IS_NULL);
        return dispatchers.computeIfAbsent(clazz, v -> new EventDispatcherList()).addListener(listener);
    }

    /**
     * Removes a {@link java.util.EventListener} class instance from a specific {@link java.util.EventListener} class type.
     *
     * @param clazz    Represents a specific {@link java.util.EventListener} class type.
     * @param listener Represents a specific {@link java.util.EventListener} instance.
     * @return True if the current {@link de.schlegel11.eventdispatcher.EventDispatcher} contains the specific {@code clazz} type and {@code listener} instance.
     */
    public boolean removeListener(Class<? extends EventListener> clazz, EventListener listener) {
        return dispatchers.getOrDefault(clazz, EventDispatcherList.PSEUDO_EMPTY_DISPATCHER_LIST).removeListener(listener);
    }

    /**
     * Removes a specific {@link java.util.EventListener} class type and all associated {@link java.util.EventListener} instances.
     *
     * @param clazz Represents a specific {@link java.util.EventListener} class type.
     * @return True if the current {@link de.schlegel11.eventdispatcher.EventDispatcher} contains the specific {@code clazz} type and removed it.
     */
    public boolean removeListenerType(Class<? extends EventListener> clazz) {
        return Objects.nonNull(dispatchers.remove(clazz));
    }

    /**
     * Returns the number of all {@link java.util.EventListener} class instances.
     *
     * @return Number of all {@link java.util.EventListener} class instances.
     */
    public int getListenerCount() {
        return dispatchers.keySet().stream().mapToInt(this::getListenerCount).sum();
    }

    /**
     * Returns the number of all {@link java.util.EventListener} class instances associated with a specific {@link java.util.EventListener} class type.
     *
     * @param clazz Represents a specific {@link java.util.EventListener} class type.
     * @return Number of all {@link java.util.EventListener} class instances associated with a specific {@link java.util.EventListener} class type.
     * <br>
     * If specific {@code clazz} type did not exist returns 0.
     */
    public int getListenerCount(Class<? extends EventListener> clazz) {
        return dispatchers.getOrDefault(clazz, EventDispatcherList.PSEUDO_EMPTY_DISPATCHER_LIST).getListenerCount();
    }

    /**
     * Returns the number of all {@link java.util.EventListener} class types.
     *
     * @return Number of all {@link java.util.EventListener} class types.
     */
    public int getListenerTypeCount() {
        return dispatchers.size();
    }

    /**
     * Handel the {@link java.util.function.Consumer} lambda expression for all {@link java.util.EventListener} class instances of a
     * specific {@link java.util.EventListener} class type.
     * <br>
     * The {@link java.util.function.Consumer} input argument is automatically mapped to the {@code clazz} type parameter.
     * <p>
     * Event methods can be handled like this:
     * <blockquote><pre>
     * fireEvent(SpecificEventListener.class, l {@literal ->} l.specificEventListenerMethod(...));
     * <br>
     * or for multiple method calls
     * <br>
     * fireEvent(SpecificEventListener.class, l {@literal ->} {
     * l.specificEventListenerMethod(...);
     * l.anotherSpecificEventListenerMethod(..);
     * });
     * </pre></blockquote>
     *
     * @param clazz    Represents a specific {@link java.util.EventListener} class type.
     *                 Return False if param is null or current {@link de.schlegel11.eventdispatcher.EventDispatcher} did not contain type.
     * @param consumer Represents an operation normally for specific {@link java.util.EventListener} instances.
     *                 Return False if param is null.
     * @param <T>      A specific EventListener type that extends from {@link java.util.EventListener} interface.
     * @return True if {@link java.util.function.Consumer} operation is successfully handled.
     */
    public <T extends EventListener> boolean fireEvent(Class<T> clazz, Consumer<T> consumer) {
        EventDispatcherList edl = dispatchers.get(clazz);
        if (Objects.nonNull(edl) && Objects.nonNull(consumer)) {
            //noinspection unchecked
            edl.fireEvent((Consumer<EventListener>) consumer);
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dispatchers);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final EventDispatcher other = (EventDispatcher) obj;
        return Objects.equals(this.dispatchers, other.dispatchers);
    }
}
