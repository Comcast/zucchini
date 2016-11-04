package com.comcast.zucchini;

import java.util.Iterator;

public class RunfastIterator<T> implements Iterator<T> {
    Iterator<T> iter;

    public RunfastIterator(Iterator<T> iter) {
        this.iter = iter;
    }

    @Override
    public synchronized boolean hasNext() {
        return this.iter.hasNext();
    }

    @Override
    public synchronized T next() {
        return this.iter.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported for Runfast");
    }
}
