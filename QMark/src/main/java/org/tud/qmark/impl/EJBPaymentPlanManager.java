package org.tud.qmark.impl;

import java.util.List;

import javax.ejb.Remote;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.tud.qmark.entities.PaymentPlan;
import org.tud.qmark.interfaces.IPaymentPlanManager;
import org.tud.qmark.interfaces.QMarkSessionBean;

/**
 * EJB manager implementation of {@link IPaymentPlanManager}.
 * 
 * @author Claas Wilke
 */
@Named("paymentPlanManager")
@Remote(IPaymentPlanManager.class)
@RequestScoped
public class EJBPaymentPlanManager extends QMarkSessionBean implements IPaymentPlanManager
{

    /** The {@link EntityManager} used. */
    @Inject
    private EntityManager paymentPlanDatabase;

    /** The current {@link UserTransaction}. */
    @Inject
    private UserTransaction utx;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IPaymentPlanManager#getPaymentPlan(java.lang
     * .Long)
     */
    public PaymentPlan getPaymentPlan(Long id) throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getPaymentPlan", id.toString()))
        {
            try
            {
                try
                {
                    utx.begin();
                    @SuppressWarnings("unchecked")
                    List<PaymentPlan> results = paymentPlanDatabase
                            .createQuery("select p from PaymentPlan p where p.paymentPlanID=:id")
                            .setParameter("id", id).getResultList();
                    if(results.isEmpty())
                    {
                        cache.putObjectsforMethodandID("getPaymentPlan", id.toString(), null);
                        return null;
                    }
                    else if(results.size() > 1)
                    {
                        throw new IllegalStateException(
                                "Cannot have more than one PaymentPlan with the same ID!");
                    }
                    else
                    {
                        cache.putObjectsforMethodandID("getPaymentPlan", id.toString(), results.get(0));
                        return results.get(0);
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
            return (PaymentPlan) ((List<PaymentPlan>) cache.getObjectsforMethodandID("getPaymentPlan", id.toString())).get(0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tud.qmark.interfaces.IPaymentPlanManager#getPaymentPlans()
     */
    @SuppressWarnings("unchecked")
    @Named
    public List<PaymentPlan> getPaymentPlans() throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getPaymentPlans", "1"))
        {
            try
            {
                try
                {
                    utx.begin();
                    List<PaymentPlan> l = paymentPlanDatabase.createQuery("select p from PaymentPlan p")
                            .getResultList();
                    cache.putObjectsforMethodandID("getPaymentPlan", "1", l);
                    return l;
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
            return (List<PaymentPlan>)cache.getObjectsforMethodandID("getPaymentPlans", "1");
        }
    }

    protected void resetCache()
    {
        cache.removeObjectforMethod("getPaymentPlans");
        cache.removeObjectforMethod("getPaymentPlan");
    }
}
