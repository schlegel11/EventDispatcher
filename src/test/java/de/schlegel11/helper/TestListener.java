package de.schlegel11.helper;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Created by schlegel11 on 01.11.14.
 */
public interface TestListener extends EventListener {
    public void testMethod(EventObject eventObject);
    public void anotherTestMethod(EventObject eventObject);
}
