package org.tud.qmark.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class JobQueue
{
    private class JobWrapper
    {
        private final long idx;
        private final InternalProfilingJob job;

        public JobWrapper(long idx, InternalProfilingJob job)
        {
            this.idx = idx;
            this.job = job;
        }

        public String toString()
        {
            return job.getID();
        }
    }

    //20 Job can be queued
    private static final int SIZE = 1000;

    private final AtomicLong counter;
    private final QMarkPriorityBlockingQueue<JobWrapper> queue;

    public JobQueue()
    {
        counter = new AtomicLong(0);
        Comparator<JobWrapper> wcomp = new Comparator<JobWrapper>()
        {
            public int compare(JobWrapper o1, JobWrapper o2)
            {
                int prio1 = o1.job.getPriority();
                int prio2 = o2.job.getPriority();
                long index1 = o1.idx;
                long index2 = o2.idx;

                //first compare by priority  
                //then compare by index  
                return prio2 < prio1 ? 1 : (prio2 > prio1 ? -1 : (index1 > index2 ? 1 : (index1 < index2 ? -1
                        : 0)));
            }
        };
        queue = new QMarkPriorityBlockingQueue<JobWrapper>(SIZE, wcomp);
    }

    public void put(InternalProfilingJob job) throws InterruptedException
    {
        List<JobWrapper> l = new ArrayList<JobWrapper>();
        l = queue.getBuffer();
        System.out.println("Add Job: " + job.getID());
        l.add(new JobWrapper(counter.incrementAndGet(), job));
        Collections.sort(l, new Comparator<JobWrapper>()
        {
            public int compare(JobWrapper o1, JobWrapper o2)
            {
                int prio1 = o1.job.getPriority();
                int prio2 = o2.job.getPriority();
                long index1 = o1.idx;
                long index2 = o2.idx;

                //first compare by priority  
                //then compare by index  
                return prio2 < prio1 ? 1 : (prio2 > prio1 ? -1 : (index1 > index2 ? 1 : (index1 < index2 ? -1
                        : 0)));
            }
        });
        queue.addAllToBuffer(l);
    }

    @SuppressWarnings("rawtypes")
    public void remove(InternalProfilingJob job) throws InterruptedException
    {
        Iterator it = queue.iterator();
        while(it.hasNext())
        {
            JobWrapper jobObject = (JobWrapper)it.next();
            if(jobObject.job.getID().equals(job.getID()))
            {
                it.remove();
            }
        }
    }

    public InternalProfilingJob get() throws InterruptedException
    {
        return queue.take().job;
    }

    @SuppressWarnings("rawtypes")
    public List<InternalProfilingJob> getAllJobs()
    {
        List<InternalProfilingJob> ret = new ArrayList<InternalProfilingJob>();
        Iterator it = queue.iterator();
        while(it.hasNext())
        {
            JobWrapper jobObject = (JobWrapper)it.next();
            ret.add(jobObject.job);
        }
        return ret;
    }

    @SuppressWarnings("rawtypes")
    public List<InternalProfilingJob> getBufferJobs()
    {
        List<InternalProfilingJob> ret = new ArrayList<InternalProfilingJob>();
        Iterator it = queue.getBuffer().iterator();
        while(it.hasNext())
        {
            JobWrapper jobObject = (JobWrapper)it.next();
            ret.add(jobObject.job);
        }
        return ret;
    }

    public int getBufferSize()
    {
        return queue.getBuffer().size();
    }

    public void enqueNew()
    {
        List<JobWrapper> l = new ArrayList<JobWrapper>();
        queue.drainTo(l);
        l = new ArrayList<JobQueue.JobWrapper>(l);
        l.addAll(queue.getBuffer());
        queue.resetBuffer();
        Collections.sort(l, new Comparator<JobWrapper>()
        {
            public int compare(JobWrapper o1, JobWrapper o2)
            {
                int prio1 = o1.job.getPriority();
                int prio2 = o2.job.getPriority();
                long index1 = o1.idx;
                long index2 = o2.idx;
                return prio2 < prio1 ? 1 : (prio2 > prio1 ? -1 : (index1 > index2 ? 1 : (index1 < index2 ? -1
                        : 0)));
            }
        });
        queue.addAll(l);
    }
}