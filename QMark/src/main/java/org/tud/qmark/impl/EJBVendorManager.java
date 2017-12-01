package org.tud.qmark.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.tud.qmark.entities.CompanyMember;
import org.tud.qmark.entities.Vendor;
import org.tud.qmark.interfaces.IVendorManager;
import org.tud.qmark.interfaces.QMarkSessionBean;
import org.tud.qmark.util.VendorConverter;

@Named("vendorManager")
@Remote(IVendorManager.class)
@RequestScoped
public class EJBVendorManager extends QMarkSessionBean implements IVendorManager
{

    /** The {@link EntityManager} used. */
    @Inject
    private EntityManager vendorDatabase;

    /**
     * {@link VendorConverter} used to convert between {@link Vendor}s and their
     * IDs.
     */
    private VendorConverter myVendorConverter = new VendorConverter();

    /** The current {@link UserTransaction}. */
    @Inject
    private UserTransaction utx;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IVendorManager#addCompanyMember(org.tud.qmark
     * .entities.CompanyMember)
     */
    public String addCompanyMember(CompanyMember companyMember) throws Exception
    {
        try
        {
            try
            {
                utx.begin();
                vendorDatabase.persist(companyMember);
                return "companyMemberAdded";
            }
            finally
            {
                utx.commit();
                resetCache();
            }
        }
        catch(Exception e)
        {
            utx.rollback();
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IVendorManager#addVendor(org.tud.qmark.entities
     * .Vendor)
     */
    public String addVendor(Vendor vendor) throws Exception
    {
        try
        {
            try
            {
                utx.begin();
                vendorDatabase.persist(vendor);
                return "vendorAdded";
            }
            finally
            {
                utx.commit();
                resetCache();
            }
        }
        catch(Exception e)
        {
            utx.rollback();
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IVendorManager#getCompanyMembers(org.tud.qmark
     * .entities.Vendor)
     */
    @SuppressWarnings("unchecked")
    public List<CompanyMember> getCompanyMembers(Vendor vendor) throws Exception
    {

        if(null == vendor) return Collections.emptyList();

        if(!cache.existsObjectsforMethodandID("getCompanyMembers", vendor.getVendorID().toString()))
        {
            try
            {
                try
                {
                    utx.begin();
                    List<CompanyMember> results = vendorDatabase
                            .createQuery(
                                    "select m from Vendor v, CompanyMember m where v.vendorID=:id and v = m.vendor")
                            .setParameter("id", vendor.getVendorID()).getResultList();
                    cache.putObjectsforMethodandID("getCompanyMembers", vendor.getVendorID().toString(),
                            results);
                    return results;
                }
                finally
                {
                    utx.commit();
                }
            }
            catch(Exception e)
            {
                utx.rollback();
                throw e;
            }
        }
        else
        {
            return (List<CompanyMember>)cache.getObjectsforMethodandID("getCompanyMembers", vendor
                    .getVendorID().toString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tud.qmark.interfaces.IVendorManager#getVendor(java.lang.Long)
     */
    public Vendor getVendor(String name) throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getVendor", name))
        {
            try
            {
                try
                {
                    utx.begin();
                    @SuppressWarnings("unchecked")
                    List<Vendor> results = vendorDatabase
                            .createQuery("select v from Vendor v where v.name=:name")
                            .setParameter("name", name).getResultList();
                    if(results.isEmpty())
                    {
                        cache.putObjectsforMethodandID("getVendor", name, null);
                        return null;
                    }
                    else if(results.size() > 1)
                    {
                        throw new IllegalStateException(
                                "Cannot have more than one Vendor with the same name!");
                    }
                    else
                    {
                        Vendor v = results.get(0);
                        cache.putObjectsforMethodandID("getVendor", name, v);
                        return v;
                    }
                }
                finally
                {
                    utx.commit();
                }
            }
            catch(Exception e)
            {
                utx.rollback();
                throw e;
            }
        }
        else
        {
            return (Vendor)cache.getObjectsforMethodandID("getVendor", name);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qmark.VendorManager#getVendors()
     */
    @SuppressWarnings("unchecked")
    @Named
    public List<Vendor> getVendors() throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getVendors", "1"))
        {
            try
            {
                try
                {
                    utx.begin();
                    List<Vendor> v = vendorDatabase.createQuery("select v from Vendor v").getResultList();
                    cache.putObjectsforMethodandID("getVendors", "1", v);
                    return v;
                }
                finally
                {
                    utx.commit();
                }
            }
            catch(Exception e)
            {
                utx.rollback();
                throw e;
            }
        }
        else
        {
            return (List<Vendor>)cache.getObjectsforMethodandID("getVendors", "1");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qmark.VendorManager#getVendorsById()
     */
    @SuppressWarnings("unchecked")
    @Named
    public Map<String, Vendor> getVendorsByName() throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getVendorsByName", "1"))
        {
            try
            {
                try
                {
                    utx.begin();
                    List<Vendor> Vendors = vendorDatabase.createQuery("select v from Vendor v")
                            .getResultList();
                    Map<String, Vendor> resultMap = new HashMap<String, Vendor>(Vendors.size());

                    for(Vendor Vendor : Vendors)
                        resultMap.put(Vendor.getName(), Vendor);
                    // end for.
                    cache.putObjectsforMethodandID("getVendorsByName", "1", resultMap);

                    return resultMap;
                }
                finally
                {
                    utx.commit();
                }
            }
            catch(Exception e)
            {
                utx.rollback();
                throw e;
            }
        }
        else
        {
            return (Map<String, Vendor>)cache.getObjectsforMethodandID("getVendorsByName", "1");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qmark.VendorManager#getVendorsFromId()
     */
    public VendorConverter getVendorConverter() throws Exception
    {
        return myVendorConverter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IVendorManager#updateCompanyMember(org.tud.qmark
     * .entities.CompanyMember)
     */
    public String updateCompanyMember(CompanyMember companyMember) throws Exception
    {
        try
        {
            try
            {
                utx.begin();
                vendorDatabase.merge(companyMember);
                return "companyMemberUpdated";
            }
            finally
            {
                utx.commit();
                resetCache();
            }
        }
        catch(Exception e)
        {
            utx.rollback();
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IVendorManager#updateVendor(org.tud.qmark.entities
     * .Vendor)
     */
    public void updateVendor(Vendor vendor) throws Exception
    {
        try
        {
            try
            {
                utx.begin();
                vendorDatabase.merge(vendor);
            }
            finally
            {
                utx.commit();
                resetCache();
            }
        }
        catch(Exception e)
        {
            utx.rollback();
            throw e;
        }
    }
    
    protected void resetCache()
    {
        cache.removeObjectforMethod("getCompanyMembers");
        cache.removeObjectforMethod("getVendor");
        cache.removeObjectforMethod("getVendors");
        cache.removeObjectforMethod("getVendorsByName");
    }
}
