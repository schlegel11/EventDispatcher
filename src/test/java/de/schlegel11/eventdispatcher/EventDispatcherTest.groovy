package de.schlegel11.eventdispatcher

import de.schlegel11.helper.TestListener
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by schlegel11 on 29.10.14.
 */
class EventDispatcherTest extends Specification {
    EventDispatcher eventDispatcher
    @Shared
            listener = Mock(EventListener)

    def "setup"() {
        eventDispatcher = EventDispatcher.newInstance()
    }

    def "AddListenerPositive"() {
        expect:
        eventDispatcher.addListener(EventListener, listener) == true
    }

    def "AddListenerNegative"() {
        given:
        NullPointerException ex
        eventDispatcher.addListener(EventListener, listener)

        expect:
        eventDispatcher.addListener(EventListener, listener) == false

        when:
        eventDispatcher.addListener(null, listener)
        then:
        ex = thrown()
        ex.message == EventDispatcher.ARGUMENT_IS_NULL

        when:
        eventDispatcher.addListener(EventListener, null)
        then:
        ex = thrown()
        ex.message == EventDispatcher.ARGUMENT_IS_NULL
    }

    def "AddListener(maxCalls)Positive"() {
        given:
        def testListener = Mock(TestListener)

        expect:
        eventDispatcher.addListener(EventListener, listener, 5) == true
        eventDispatcher.addListener(TestListener, testListener, -1) == true
    }

    def "AddListener(maxCalls)Negative"() {
        given:
        NullPointerException ex
        IllegalArgumentException iae
        eventDispatcher.addListener(EventListener, listener, 5)

        expect:
        eventDispatcher.addListener(EventListener, listener, 5) == false
        eventDispatcher.addListener(EventListener, listener, 6) == false

        when:
        eventDispatcher.addListener(null, listener, 5)
        then:
        ex = thrown()
        ex.message == EventDispatcher.ARGUMENT_IS_NULL

        when:
        eventDispatcher.addListener(EventListener, null, 5)
        then:
        ex = thrown()
        ex.message == EventDispatcher.ARGUMENT_IS_NULL

        when:
        eventDispatcher.addListener(EventListener, listener, 0)
        then:
        iae = thrown()
        iae.message == EventDispatcher.ARGUMENT_MAX_CALLS

        when:
        eventDispatcher.addListener(EventListener, listener, -2)
        then:
        iae = thrown()
        iae.message == EventDispatcher.ARGUMENT_MAX_CALLS
    }

    def "RemoveListenerPositive"() {
        given:
        eventDispatcher.addListener(EventListener, listener)

        expect:
        eventDispatcher.removeListener(EventListener, listener) == true
    }

    def "RemoveListenerNegative"() {
        given:
        def emptyEventDispatcher = EventDispatcher.newInstance()
        eventDispatcher.addListener(EventListener, listener)

        expect:
        eventDispatcher.removeListener(paramClazz, paramListener) == result
        emptyEventDispatcher.removeListener(EventListener, listener) == false
        where:
        paramClazz    | paramListener       || result
        Object        | listener            || false
        EventListener | Mock(EventListener) || false
        null          | listener            || false
        EventListener | null                || false
    }

    def "RemoveListenerTypePositive"() {
        given:
        eventDispatcher.addListener(EventListener, listener)

        expect:
        eventDispatcher.removeListenerType(EventListener) == true
    }

    def "RemoveListenerTypeNegative"() {
        given:
        def emptyEventDispatcher = EventDispatcher.newInstance()
        eventDispatcher.addListener(EventListener, listener)

        expect:
        eventDispatcher.removeListenerType(paramClazz) == result
        emptyEventDispatcher.removeListenerType(EventListener) == false
        where:
        paramClazz || result
        Object     || false
        null       || false
    }

    def "GetListenerCount"() {
        given:
        def anotherListener = Mock(EventListener)
        def emptyEventDispatcher = EventDispatcher.newInstance()
        def bigEventDispatcher = EventDispatcher.newInstance()
        eventDispatcher.addListener(EventListener, listener)
        eventDispatcher.addListener(EventListener, listener)
        eventDispatcher.addListener(Object, anotherListener)
        for (int i = 0; i < 300; i++) {
            bigEventDispatcher.addListener(EventListener, Mock(EventListener))
        }

        expect:
        eventDispatcher.getListenerCount() == 2
        eventDispatcher.removeListener(EventListener, listener)
        eventDispatcher.getListenerCount() == 1
        eventDispatcher.removeListener(Object, anotherListener)
        eventDispatcher.getListenerCount() == 0
        eventDispatcher.addListener(Object, anotherListener)
        eventDispatcher.getListenerCount() == 1
        emptyEventDispatcher.getListenerCount() == 0
        bigEventDispatcher.getListenerCount() == 300
    }

    def "GetListenerCount(clazz)"() {
        given:
        def anotherListener = Mock(EventListener)
        def emptyEventDispatcher = EventDispatcher.newInstance()
        def bigEventDispatcher = EventDispatcher.newInstance()
        eventDispatcher.addListener(EventListener, listener)
        eventDispatcher.addListener(EventListener, anotherListener)
        eventDispatcher.addListener(Object, anotherListener)
        for (int i = 0; i < 300; i++) {
            bigEventDispatcher.addListener(EventListener, Mock(EventListener))
        }

        expect:
        eventDispatcher.getListenerCount(EventListener) == 2
        eventDispatcher.removeListener(EventListener, listener)
        eventDispatcher.getListenerCount(EventListener) == 1
        eventDispatcher.removeListener(EventListener, anotherListener)
        eventDispatcher.getListenerCount(EventListener) == 0
        eventDispatcher.getListenerCount(Object) == 1
        eventDispatcher.removeListener(Object, anotherListener)
        eventDispatcher.getListenerCount(Object) == 0
        eventDispatcher.getListenerCount(String) == 0
        eventDispatcher.getListenerCount(null) == 0
        emptyEventDispatcher.getListenerCount(Object) == 0
        emptyEventDispatcher.getListenerCount(null) == 0
        bigEventDispatcher.getListenerCount(EventListener) == 300
    }

    def "GetListenerTypeCount"() {
        given:
        def anotherListener = Mock(EventListener)
        def testListener = Mock(TestListener)
        def emptyEventDispatcher = EventDispatcher.newInstance()
        eventDispatcher.addListener(EventListener, listener)
        eventDispatcher.addListener(EventListener, anotherListener)
        eventDispatcher.addListener(Object, anotherListener)
        eventDispatcher.addListener(TestListener, testListener)

        expect:
        eventDispatcher.getListenerTypeCount() == 3
        eventDispatcher.removeListenerType(EventListener)
        eventDispatcher.getListenerTypeCount() == 2
        eventDispatcher.removeListenerType(Object)
        eventDispatcher.getListenerTypeCount() == 1
        eventDispatcher.removeListener(TestListener, testListener)
        eventDispatcher.getListenerTypeCount() == 0
        emptyEventDispatcher.getListenerTypeCount() == 0
    }

    def "GetListenerMaxCalls"() {
        given:
        def anotherListener = Mock(EventListener)
        def emptyEventDispatcher = EventDispatcher.newInstance()
        eventDispatcher.addListener(EventListener, listener, 15)
        eventDispatcher.addListener(EventListener, anotherListener, 20)
        eventDispatcher.addListener(Object, anotherListener, 5)
        eventDispatcher.addListener(Object, listener)

        expect:
        eventDispatcher.getListenerMaxCalls(EventListener, listener) == 15
        eventDispatcher.getListenerMaxCalls(EventListener, anotherListener) == 20
        eventDispatcher.getListenerMaxCalls(Object, anotherListener) == 5
        eventDispatcher.getListenerMaxCalls(String, anotherListener) == 0
        eventDispatcher.getListenerMaxCalls(Object, listener) == -1
        emptyEventDispatcher.getListenerMaxCalls(EventListener, listener) == 0
    }

    def "GetListenerCurrentCalls"() {
        given:
        def anotherListener = Mock(EventListener)
        def emptyEventDispatcher = EventDispatcher.newInstance()
        eventDispatcher.addListener(EventListener, listener, 15)
        eventDispatcher.addListener(EventListener, anotherListener, 20)
        eventDispatcher.addListener(Object, anotherListener, 5)
        eventDispatcher.addListener(Object, listener)
        eventDispatcher.fireEvent(EventListener, { l -> l.toString() })
        eventDispatcher.fireEvent(EventListener, { l -> l.toString() })
        eventDispatcher.fireEvent(EventListener, { l -> l.toString() })
        eventDispatcher.fireEvent(EventListener, { l -> l.toString() })
        eventDispatcher.fireEvent(Object, { l -> l.toString() })
        eventDispatcher.fireEvent(Object, { l -> l.toString() })
        eventDispatcher.fireEvent(Object, { l -> l.toString() })
        eventDispatcher.fireEvent(Object, { l -> l.toString() })

        expect:
        eventDispatcher.getListenerCurrentCalls(EventListener, listener) == 4
        eventDispatcher.getListenerCurrentCalls(EventListener, anotherListener) == 4
        eventDispatcher.getListenerCurrentCalls(Object, anotherListener) == 4
        eventDispatcher.fireEvent(Object, { l -> l.toString() })
        eventDispatcher.getListenerCurrentCalls(Object, anotherListener) == 0
        eventDispatcher.getListenerCurrentCalls(Object, listener) == 0
        emptyEventDispatcher.getListenerCurrentCalls(Object, anotherListener) == 0
    }

    def "Clear"() {
        given:
        def anotherListener = Mock(EventListener)
        eventDispatcher.addListener(EventListener, listener)
        eventDispatcher.addListener(EventListener, anotherListener)
        eventDispatcher.addListener(Object, anotherListener)

        expect:
        eventDispatcher.clear()
        eventDispatcher.getListenerCount() == 0
        eventDispatcher.getListenerTypeCount() == 0
    }

    def "FireEventPositive"() {
        given:
        def testListener = Mock(TestListener)
        def listenerMaxCalls = Mock(EventListener)
        def eventObject = new EventObject("TestSource")
        def anotherEventObject = new EventObject("TestSource2")
        eventDispatcher.addListener(TestListener, testListener)
        eventDispatcher.addListener(EventListener, listenerMaxCalls, 1)

        when:
        eventDispatcher.fireEvent(TestListener, { l -> l.testMethod(eventObject) })
        and:
        eventDispatcher.fireEvent(TestListener, { l ->
            l.testMethod(eventObject)
            l.anotherTestMethod(anotherEventObject)
        })
        then:
        2 * testListener.testMethod({ e -> e.getSource() == "TestSource" })
        1 * testListener.anotherTestMethod({ e -> e.getSource() == "TestSource2" })

        expect:
        eventDispatcher.fireEvent(TestListener, { l -> l.testMethod(eventObject) }) == true
        eventDispatcher.fireEvent(TestListener, {}) == true

        when:
        eventDispatcher.fireEvent(EventListener, { l -> l.toString() })
        eventDispatcher.fireEvent(EventListener, { l -> l.toString() })
        then:
        1 * listenerMaxCalls.toString()

        expect:
        eventDispatcher.getListenerCount() == 1
        eventDispatcher.getListenerCount(EventListener) == 0
        eventDispatcher.getListenerTypeCount() == 1
    }

    def "FireEventNegative"() {
        given:
        def testListener = Mock(TestListener)
        def listenerMaxCalls = Mock(EventListener)
        def eventObject = new EventObject("TestSource")
        def emptyEventDispatcher = EventDispatcher.newInstance()
        eventDispatcher.addListener(TestListener, testListener)
        eventDispatcher.addListener(EventListener, listenerMaxCalls, 1)
        eventDispatcher.fireEvent(EventListener, { l -> l.toString() })

        expect:
        eventDispatcher.fireEvent(null, { l -> l.testMethod(eventObject) }) == false
        eventDispatcher.fireEvent(TestListener, null) == false
        eventDispatcher.fireEvent(Object, { l -> l.testMethod(eventObject) }) == false
        emptyEventDispatcher.fireEvent(Object, { l -> l.testMethod(eventObject) }) == false
        eventDispatcher.fireEvent(EventListener, { l -> l.toString() }) == false
    }
}
