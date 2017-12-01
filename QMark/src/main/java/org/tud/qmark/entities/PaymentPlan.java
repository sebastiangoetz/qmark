/*
 * Created on 24.10.2012
 */

package org.tud.qmark.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Represents a payment plan used to pay the profiling of {@link App}s.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 */
@Entity
@Table(name = "PaymentPlan")
public class PaymentPlan implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -879037904858845831L;

	/** The ID of the {@link PaymentPlan} in the DB. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "paymentPlanID")
	private Long paymentPlanID;

	/** The Type of this {@link PaymentPlan}. */
	@OneToOne()
	@JoinColumn(name = "typeID", unique = false, nullable = false, updatable = false)
	private Type type;

	/**
	 * Returns the ID of the {@link PaymentPlan} in the DB.
	 * 
	 * @return The ID of the {@link PaymentPlan} in the DB.
	 */
	public Long getPaymentPlanID() {
		return paymentPlanID;
	}

	/**
	 * Returns the Type of this {@link PaymentPlan}.
	 * 
	 * @return The Type of this {@link PaymentPlan}.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Sets the ID of the {@link PaymentPlan} in the DB.
	 * 
	 * @param paymentPlanID
	 *            The ID of the {@link PaymentPlan} in the DB.
	 */
	public void setPaymentPlanID(Long paymentPlanID) {
		this.paymentPlanID = paymentPlanID;
	}

	/**
	 * Sets the Type of this {@link PaymentPlan}.
	 * 
	 * @param type
	 *            The Type of this {@link PaymentPlan}.
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 * Overwritten to avoid validation errors from JSF.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PaymentPlan other = (PaymentPlan) obj;
		if (paymentPlanID == null) {
			if (other.paymentPlanID != null)
				return false;
		} else if (!paymentPlanID.equals(other.paymentPlanID))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 * 
	 * Overwritten to avoid validation errors from JSF.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((paymentPlanID == null) ? 0 : paymentPlanID.hashCode());
		return result;
	}
}