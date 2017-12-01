package org.tud.qmark.interfaces;

import java.util.List;

import org.tud.qmark.entities.PaymentPlan;

/**
 * Manages {@link PaymentPlan} entities.
 * 
 * @author Claas Wilke
 */
public interface IPaymentPlanManager {

	/**
	 * Tries to retrieve a {@link PaymentPlan} for the given ID.
	 * 
	 * @param id
	 *            The id of the {@link PaymentPlan}.
	 * @return The resolved {@link PaymentPlan} or <code>null</code>.
	 * @throws Exception
	 */
	public PaymentPlan getPaymentPlan(Long id) throws Exception;

	/**
	 * Returns all existing {@link PaymentPlan}s.
	 * 
	 * @return All existing {@link PaymentPlan}s.
	 * @throws Exception
	 */
	public List<PaymentPlan> getPaymentPlans() throws Exception;
}
