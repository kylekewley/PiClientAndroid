package com.kylekewley.piclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by kylekewley on 7/21/14.
 */
public class OrderedUniqueArrayList<E> extends ArrayList<E> {

    /**
     * Comparator to use for objects in the list.
     */
    private Comparator<E> comparator;

    private OrderedUniqueArrayList() {

    }
    public OrderedUniqueArrayList(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    public Comparator<E> getComparator() {
        return comparator;
    }


    @Override
    public boolean add(E e) {
        int loc = Collections.binarySearch(this, e, comparator);
        if (loc >= 0) {
            return false;
        }else {
            int insertionPoint = -(loc+1);
            super.add(insertionPoint, e);
        }

        return true;
    }
}
