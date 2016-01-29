package de.schlegel11.eventdispatcher;

import com.google.common.base.Preconditions;
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
 * @version 1.1
 */
public final class EventDispatcher {

    public static final String ARGUMENT_IS_NULL = "Argument is null.";
    public static final String ARGUMENT_MAX_CALLS = "Argument is less or equal 0 (except -1).";
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
     *                 If param is {@code null} a {@link java.lang.NullPointerException} is thrown.
     * @param listener Represents a specific {@link java.util.EventListener} instance.
     *                 If param is {@code null} a {@link java.lang.NullPointerException} is thrown.
     * @return True if the current {@link de.schlegel11.eventdispatcher.EventDispatcher} did not already contain the specific {@code listener} instance.
     */
    public boolean addListener(final Class<? extends EventListener> clazz, final EventListener listener) {
        return addListener(clazz, listener, EventListenerWrapper.INFINITE_CALLS);
    }

    /**
     * Adds a new {@link java.util.EventListener} class instance with a maximum number of {@link java.util.EventListener} calls.
     * <br>
     * In case of infinite calls please use {@link #addListener(Class, java.util.EventListener)}
     * <p>
     * The {@link java.util.EventListener} class instance is assigned to a specific {@link java.util.EventListener} class type.
     * <br>
     * A specific {@link java.util.EventListener} class type assigns many {@link java.util.EventListener} class instances.
     *
     * @param clazz    Represents a specific unique {@link java.util.EventListener} class type.
     *                 If param is {@code null} a {@link java.lang.NullPointerException} is thrown.
     * @param listener Represents a specific {@link java.util.EventListener} instance.
     *                 If param is {@code null} a {@link java.lang.NullPointerException} is thrown.
     * @param maxCalls Represents a maximum number of calls for the specific {@link java.util.EventListener} instance.
     *                 The value -1 represents infinite calls.
     *                 If param is less or equal 0 (except -1) an {@link java.lang.IllegalArgumentException} is thrown.
     * @return True if the current {@link de.schlegel11.eventdispatcher.EventDispatcher} did not already contain the specific {@code listener} instance.
     */
    public boolean addListener(final Class<? extends EventListener> clazz, final EventListener listener, final int maxCalls) {
        Objects.requireNonNull(clazz, ARGUMENT_IS_NULL);
        Objects.requireNonNull(listener, ARGUMENT_IS_NULL);
        Preconditions.checkArgument(maxCalls > 0 || maxCalls == EventListenerWrapper.INFINITE_CALLS, ARGUMENT_MAX_CALLS);
        return dispatchers.computeIfAbsent(clazz, v -> new EventDispatcherList()).addListener(listener, maxCalls);
    }

    /**
     * Removes a {@link java.util.EventListener} class instance from a specific {@link java.util.EventListener} class type.
     * <br>
     * If the {@link java.util.EventListener} class type has no more {@link java.util.EventListener} class instances it will also be removed.
     *
     * @param clazz    Represents a specific {@link java.util.EventListener} class type.
     * @param listener Represents a specific {@link java.util.EventListener} instance.
     * @return True if the current {@link de.schlegel11.eventdispatcher.EventDispatcher} contains the specific {@code clazz} type and {@code listener} instance.
     */
    public boolean removeListener(final Class<? extends EventListener> clazz, final EventListener listener) {
        EventDispatcherList edl = dispatchers.getOrDefault(clazz, EventDispatcherList.PSEUDO_EMPTY_DISPATCHER_LIST);
        boolean result = edl.removeListener(listener);
        if (edl.isEmpty()) {
            removeListenerType(clazz);
        }
        return result;
    }

    /**
     * Removes a specific {@link java.util.EventListener} class type and all associated {@link java.util.EventListener} instances.
     *
     * @param clazz Represents a specific {@link java.util.EventListener} class type.
     * @return True if the current {@link de.schlegel11.eventdispatcher.EventDispatcher} contains the specific {@code clazz} type and removed it.
     */
    public boolean removeListenerType(final Class<? extends EventListener> clazz) {
        return Objects.nonNull(dispatchers.remove(clazz));
    }

    /**
     * Removes all {@link java.util.EventListener} class type and listener instances.
     * The current {@link de.schlegel11.eventdispatcher.EventDispatcher} will be empty after this call returns.
     */
    public void clear() {
        dispatchers.clear();
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
    public int getListenerCount(final Class<? extends EventListener> clazz) {
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
     * Returns the number of maximum calls for the specific {@link java.util.EventListener} class instance.
     *
     * @param clazz    Represents a specific {@link java.util.EventListener} class type.
     * @param listener Represents a specific {@link java.util.EventListener} instance.
     * @return The number of maximum calls for the specific {@link java.util.EventListener} class instance.
     * <br>
     * If specific {@link java.util.EventListener} instance did not exist returns 0.
     * <br>
     * If specific {@link java.util.EventListener} instance have infinite calls returns -1.
     */
    public int getListenerMaxCalls(final Class<? extends EventListener> clazz, final EventListener listener) {
        return dispatchers.getOrDefault(clazz, EventDispatcherList.PSEUDO_EMPTY_DISPATCHER_LIST).getListenerMaxCalls(listener);
    }

    /**
     * Returns the number of current calls for the specific {@link java.util.EventListener} class instance.
     *
     * @param clazz    Represents a specific {@link java.util.EventListener} class type.
     * @param listener Represents a specific {@link java.util.EventListener} instance.
     * @return The number of current calls for the specific {@link java.util.EventListener} class instance.
     * <br>
     * If specific {@link java.util.EventListener} instance did not exist returns 0.
     * <br>
     * If specific {@link java.util.EventListener} instance have infinite calls returns 0.
     */
    public int getListenerCurrentCalls(final Class<? extends EventListener> clazz, final EventListener listener) {
        return dispatchers.getOrDefault(clazz, EventDispatcherList.PSEUDO_EMPTY_DISPATCHER_LIST).getListenerCurrentCalls(listener);
    }

    /**
     * Handel the {@link java.util.function.Consumer} lambda expression for all {@link java.util.EventListener} class instances of a
     * specific {@link java.util.EventListener} class type.
     * <br>
     * If the "maxCalls" ({@link #addListener(Class, java.util.EventListener, int)}) value of the {@link java.util.EventListener} class instance is reached the instance will be removed.
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
     * l.anotherSpecificEventListenerMethod(...);
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
    public <T extends EventListener> boolean fireEvent(final Class<T> clazz, final Consumer<T> consumer) {
        EventDispatcherList edl = dispatchers.get(clazz);
        if (Objects.nonNull(edl) && Objects.nonNull(consumer)) {
            //noinspection unchecked
            edl.fireEvent((Consumer<EventListener>) consumer);
            if (edl.isEmpty()) {
                removeListenerType(clazz);
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dispatchers);
    }

    @Override
    public boolean equals(final Object obj) {
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
