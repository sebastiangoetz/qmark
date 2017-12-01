/*
 * Created on 24.10.2012
 */

package org.tud.qmark.impl;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.tud.qmark.entities.Device;
import org.tud.qmark.interfaces.IDeviceManager;
import org.tud.qmark.interfaces.QMarkSessionBean;

/**
 * EJB manager implementation for {@link IDeviceManager}.
 * 
 * @author Claas Wilke
 */
@Named("deviceManager")
@Stateful
@RequestScoped
public class EJBDeviceManager extends QMarkSessionBean implements IDeviceManager
{

    /** The {@link EntityManager} used. */
    @Inject
    private EntityManager deviceDatabase;

    /*
     * (non-Javadoc)
     * 
     * @see org.tud.qmark.interfaces.IDeviceManager#getDevice(java.lang.String)
     */
    public Device getDevice(String name) throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getDevice", name))
        {
            @SuppressWarnings("unchecked")
            List<Device> results = deviceDatabase.createQuery("select d from Device d where d.name=:name")
                    .setParameter("name", name).getResultList();
            if(results.isEmpty())
            {
                cache.putObjectsforMethodandID("getDevice", name, null);
                return null;
            }
            else if(results.size() > 1)
            {
                throw new IllegalStateException("Cannot have more than one device with the same name!");
            }
            else
            {
                cache.putObjectsforMethodandID("getDevice", name, results.get(0));
                return results.get(0);
            }
        }
        else
        {
            return (Device)cache.getObjectsforMethodandID("getDevice", name);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tud.qmark.interfaces.IDeviceManager#getDevices()
     */
    @SuppressWarnings("unchecked")
    public List<Device> getDevices() throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getDevices", "1"))
        {
            List<Device> results = deviceDatabase.createQuery("select d from Device d").getResultList();
            cache.putObjectsforMethodandID("getDevices", "1", results);
            return results;
        }
        else
        {
            return (List<Device>)cache.getObjectsforMethodandID("getDevices", "1");
        }
    }
    
    protected void resetCache()
    {
        cache.removeObjectforMethod("getDevices");
        cache.removeObjectforMethod("getDevice");
    }
}
