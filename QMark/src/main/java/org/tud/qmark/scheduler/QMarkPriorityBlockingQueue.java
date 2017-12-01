package org.tud.qmark.scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class QMarkPriorityBlockingQueue<E> extends PriorityBlockingQueue<E>
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private ArrayList<E> buffer = new ArrayList<E>();

    public QMarkPriorityBlockingQueue(int size, Comparator<E> wcomp)
    {
        super(size, wcomp);
    }

    public ArrayList<E> getBuffer()
    {
        return buffer;
    }

    public void resetBuffer()
    {
        buffer = new ArrayList<E>();
    }

    public void addAllToBuffer(List<E> elements)
    {
        resetBuffer();
        buffer.addAll(elements);
    }

}
