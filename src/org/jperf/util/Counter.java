package org.jperf.util;

/**
 * Thread-safe counter.
 *
 * @author Andy Grove
 */
public class Counter {

    /**
     * Current value.
     */
    protected long counter;

    /**
     * Creates a counter with an initial value of zero.
     */
    public Counter() {
    }

    /**
     * Creates a counter with the specified initial value.
     *
     * @param initialValue Initial value for the counter
     */
    public Counter(long initialValue) {
        this.counter = initialValue;
    }

    /**
     * Retrieves the current counter value and increments the counter.
     *
     * @return current value
     */
    public synchronized long next() {
        return counter++;
    }

    /**
     * Retrieves the current counter value and resets the counter to zero.
     *
     * @return current value
     */
    public synchronized long reset() {
        long ret = counter;
        counter = 0;
        return ret;
    }

}
