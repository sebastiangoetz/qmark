package org.tud.qmark.impl;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Named;

import org.tud.qmark.interfaces.IClientManager;
import org.tud.qmark.interfaces.QMarkSessionBean;
import org.tud.qmark.scheduler.InternalProfilingJob;
import org.tud.qmark.scheduler.JobSchedulerService;

@Named("clientManager")
@Stateless
@Remote(IClientManager.class)
public class EJBClientManager extends QMarkSessionBean implements IClientManager 
{
    @EJB
    JobSchedulerService jobSchedulerService;
    
    public String getInfo()
    {
        InternalProfilingJob int1 = new InternalProfilingJob(1l, "Test1", 1, 500);
        InternalProfilingJob int2 = new InternalProfilingJob(1l, "Test2", 1, 300);
        InternalProfilingJob int3 = new InternalProfilingJob(1l, "Test3", 1, 1);
        InternalProfilingJob int4 = new InternalProfilingJob(1l, "Test4", 1, 600);
        jobSchedulerService.addJob(int1);
        jobSchedulerService.addJob(int2);
        jobSchedulerService.addJob(int3);
        jobSchedulerService.addJob(int4);
        
        jobSchedulerService.tryStart();
        
        return "Test Queue";
    }
    
    public String getInfo2()
    {
        try
        {
            Thread.sleep(4000);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        try
        {
            //jobSchedulerService.stop();
            InternalProfilingJob int1 = new InternalProfilingJob(1l, "Test5", 1, 500);
            InternalProfilingJob int2 = new InternalProfilingJob(1l, "Test6", 1, 300);
            InternalProfilingJob int3 = new InternalProfilingJob(1l, "Test7", 1, 1);
            InternalProfilingJob int4 = new InternalProfilingJob(1l, "Test8", 1, 600);
            jobSchedulerService.addJob(int1);
            jobSchedulerService.addJob(int2);
            jobSchedulerService.addJob(int3);
            jobSchedulerService.addJob(int4);
            jobSchedulerService.tryStart();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return "Test Queue2";
    }
    
}
