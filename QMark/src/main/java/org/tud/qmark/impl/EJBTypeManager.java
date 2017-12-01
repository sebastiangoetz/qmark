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

import org.tud.qmark.entities.Type;
import org.tud.qmark.interfaces.ITypeManager;
import org.tud.qmark.interfaces.QMarkSessionBean;

/**
 * EJB manager implementation for {@link ITypeManager}.
 * 
 * @author Claas Wilke
 */
@Named("typeManager")
@Stateful
@RequestScoped
public class EJBTypeManager extends QMarkSessionBean implements ITypeManager
{

    /** The {@link EntityManager} used. */
    @Inject
    private EntityManager typeDatabase;

    /*
     * (non-Javadoc)
     * 
     * @see org.tud.qmark.interfaces.ITypeManager#getType(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Type getType(String name) throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getType", name))
        {
            List<Type> results = typeDatabase.createQuery("select t from Type t where t.name=:name")
                    .setParameter("name", name).getResultList();
            if(results.isEmpty())
            {
                cache.putObjectsforMethodandID("getType", name, null);
                return null;
            }
            else if(results.size() > 1)
            {
                throw new IllegalStateException("Cannot have more than one Type with the same username!");
            }
            else
            {
                cache.putObjectsforMethodandID("getType", name, results.get(0));
                return results.get(0);
            }
        }
        else
        {
            return (Type)cache.getObjectsforMethodandID("getType", name);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.ITypeManager#getSubTypes(org.tud.qmark.entities
     * .Type)
     */
    @SuppressWarnings("unchecked")
    public List<Type> getSubTypes(Type type) throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getSubTypes", type.getTypeID().toString()))
        {
            List<Type> results = typeDatabase.createQuery("select t from Type t where t.typeType=:type")
                    .setParameter("type", type).getResultList();
            cache.putObjectsforMethodandID("getSubTypes", type.getTypeID().toString(), results);
            return results;
        }
        else
        {
            return (List<Type>)cache.getObjectsforMethodandID("getSubTypes", type.getTypeID().toString());
        }
    }

    protected void resetCache()
    {
        cache.removeObjectforMethod("getSubTypes");
        cache.removeObjectforMethod("getType");
    }
}
