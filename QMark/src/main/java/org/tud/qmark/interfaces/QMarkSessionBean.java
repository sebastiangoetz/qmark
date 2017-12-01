package org.tud.qmark.interfaces;

import javax.ejb.EJB;


public class QMarkSessionBean
{
    @EJB
    protected ICacheManager cache;
    
    protected void resetCache()
    {
        //to be implemented in subclasses
    }
}
