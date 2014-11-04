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
        ex.message == "Argument is null."

        when:
        eventDispatcher.addListener(EventListener, null)
        then:
        ex = thrown()
        ex.message == "Argument is null."
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
        def emptyEventDispatcher = EventDispatcher.newInstance()
        eventDispatcher.addListener(EventListener, listener)
        eventDispatcher.addListener(EventListener, anotherListener)
        eventDispatcher.addListener(Object, anotherListener)

        expect:
        eventDispatcher.getListenerTypeCount() == 2
        eventDispatcher.removeListenerType(EventListener)
        eventDispatcher.getListenerTypeCount() == 1
        eventDispatcher.removeListenerType(Object)
        eventDispatcher.getListenerTypeCount() == 0
        emptyEventDispatcher.getListenerTypeCount() == 0
    }

    def "FireEventPositive"() {
        given:
        def testListener = Mock(TestListener)
        def eventObject = new EventObject("TestSource")
        def anotherEventObject = new EventObject("TestSource2")
        eventDispatcher.addListener(TestListener, testListener)

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
    }

    def "FireEventNegative"() {
        given:
        def testListener = Mock(TestListener)
        def eventObject = new EventObject("TestSource")
        def emptyEventDispatcher = EventDispatcher.newInstance()
        eventDispatcher.addListener(TestListener, testListener)

        expect:
        eventDispatcher.fireEvent(null, { l -> l.testMethod(eventObject) }) == false
        eventDispatcher.fireEvent(TestListener, null) == false
        eventDispatcher.fireEvent(Object, { l -> l.testMethod(eventObject) }) == false
        emptyEventDispatcher.fireEvent(Object, { l -> l.testMethod(eventObject) }) == false
    }
}
