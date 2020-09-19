package org.music.connection;

import java.util.Vector;

public class BlockingQueue {

    private Vector v = new Vector();

    public BlockingQueue() {
    }

    /**
     * put an object into the queue
     */
    public synchronized void put(Object o) {
        v.addElement(o);
        notify();
    }

    /**
     * @@return an object from the queue.
     * blocks until there is one.
     */
    public synchronized Object get()
            throws InterruptedException {
        while (true) {
            if (v.size() > 0) {
                Object o = v.elementAt(0);
                v.removeElementAt(0);
                return o;
            } else {
                wait();
            }
        }
    }

}
