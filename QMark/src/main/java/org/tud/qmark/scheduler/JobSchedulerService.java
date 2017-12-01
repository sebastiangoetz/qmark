package org.tud.qmark.scheduler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

@Singleton
public class JobSchedulerService
{
    private static final int N = 1;
    private ExecutorService scheduler;
    private ExecutorService executorThread;
    private final JobQueue queue = new JobQueue();

    @PostConstruct
    public void init()
    {
        //something TODO?
    }

    public boolean addJob(InternalProfilingJob job)
    {
        try
        {
            queue.put(job);
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeJob(InternalProfilingJob job)
    {
        try
        {
            queue.remove(job);
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    //TODO should maybe Synchronized
    private void start()
    {
        if(scheduler != null || executorThread != null)
        {
            return;
        }

        scheduler = Executors.newSingleThreadExecutor();

        scheduler.submit(new Runnable()
        {
            public void run()
            {
                try
                {
                    boolean realBreak = false;
                    while(!realBreak)
                    {
                        System.out.println(realBreak);
                        queue.enqueNew();
                        int actualbuffersize = 0;
                        InternalProfilingJob job;
                        while(actualbuffersize == 0 && ((job = queue.get()) != null))
                        {
                            executorThread = Executors.newFixedThreadPool(N);
                            execute(job);
                            executorThread.shutdown();
                            executorThread.awaitTermination(3, TimeUnit.DAYS);
                            actualbuffersize = queue.getBufferSize();
                        }
                        if(actualbuffersize == 0)
                        {
                            realBreak = true;
                        }
                    }
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void tryStart()
    {
        queue.enqueNew();
        start();
    }

    //TODO should maybe Synchronized
    public boolean stop() throws InterruptedException
    {
        if(scheduler == null || executorThread == null)
        {
            throw new RuntimeException("JobProcessor is already stopped.");
        }

        boolean stopped = false;
        scheduler.shutdownNow();
        stopped = scheduler.awaitTermination(30, TimeUnit.SECONDS);

        executorThread.shutdown();
        stopped &= executorThread.awaitTermination(300, TimeUnit.SECONDS);

        if(stopped)
        {
            scheduler = executorThread = null;
        }

        return stopped;
    }

    private void execute(final InternalProfilingJob job)
    {
        executorThread.submit(new Runnable()
        {
            public void run()
            {
                job.execute();
            }
        });
    }
    
    /*
     * Returns Jobs acutal scheduled
     */
    public List<InternalProfilingJob> getScheduledJobs()
    {
        return queue.getAllJobs();
    }
    
    /*
     * Returns Jobs, which are in the Buffer but not scheduled yet.
     * They will be scheduled, if the current task ist finished.
     */
    public List<InternalProfilingJob> getBufferJobs()
    {
        return queue.getBufferJobs();
    }

}
