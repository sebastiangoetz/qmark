package org.tud.qmark.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import org.tud.qmark.entities.PaymentPlan;
import org.tud.qmark.interfaces.IPaymentPlanManager;

/**
 * {@link Converter} implementation to convert between {@link PaymentPlan}s and
 * their IDs.
 * 
 * @author Claas Wilke
 */
@Named("paymentPlanConverter")
public class PaymentPlanConverter implements Converter {

	/** The {@link IPaymentPlanManager} used to resolve {@link PaymentPlan}s. */
	@Inject
	private IPaymentPlanManager paymentPlanManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		try {
			Long id = Long.parseLong(arg2.split(" - ")[0].trim());
			PaymentPlan result = paymentPlanManager.getPaymentPlan(id);
			return result;
		}

		catch (Exception e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.Object)
	 */
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		if (arg2 instanceof PaymentPlan) {
			PaymentPlan pp = (PaymentPlan) arg2;
			return pp.getPaymentPlanID() + " - " + pp.getType().getName();
		} else
			return "";
	}
}
