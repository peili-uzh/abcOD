package org.music.segmentation.baseline;

import java.util.ArrayList;

/**
 * A list of elements. It supports: (1) get element by index; (2) push element to the head of list; (3) pop the first element in the list
 *
 * @author peili
 */
public class SequenceList {

    @SuppressWarnings({"rawtypes"})
    private ArrayList list;

    @SuppressWarnings("rawtypes")
    public SequenceList() {
        this.list = new ArrayList();
    }

    /**
     * Get an element in SequenceList by index.
     *
     * @param index
     * @return an element in the sequence list.
     */
    public Object getElement(int index) {

        if (list.size() > index)
            return list.get(index);
        else
            return null;

    }

    /**
     * Push an element to the first position of the sequence list.
     *
     * @param object
     */
    @SuppressWarnings("unchecked")
    public void pushElement(Object object) {
        if (object != null)
            list.add(0, object);
    }

    /**
     * Remove the first element of the sequence list, and return it as value of the function call.
     *
     * @return
     */
    public Object popElement() {
        if (list.isEmpty())
            return null;
        else {
            Object object = list.get(0);
            list.remove(0);
            return object;
        }

    }

    /**
     * Add object to the end of the SequenceList.
     *
     * @param object
     */
    @SuppressWarnings("unchecked")
    public void appendElement(Object object) {
        if (object != null)
            list.add(list.size(), object);
    }

    /**
     * Return the size of the list.
     */
    public int size() {
        return list.size();
    }

    public void remove(int index) {
        if (!list.isEmpty() && list.size() > index)
            list.remove(index);
    }
}
