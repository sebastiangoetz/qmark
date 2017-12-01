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
 * A {@link Vendor} is the developer and deployer of one or multiple {@link App}
 * s.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 */
@Entity(name = "Vendor")
@Table(name = "Vendor")
public class Vendor implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = 1942442850330565929L;

	/** The description of this {@link Vendor}. */
	@Column(name = "description")
	private String description;

	/** The name of the {@link Vendor}. */
	@Column(name = "name")
	private String name;

	/**
	 * The {@link PaymentPlan} this {@link Vendor} uses to profile its
	 * {@link App}s.
	 */
	@OneToOne
	@JoinColumn(name = "paymentPlanID", unique = false, nullable = false, updatable = false)
	private PaymentPlan paymentPlan;

	/** The ID of the {@link Vendor} in the DB. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vendorID")
	private Long vendorID;

	/**
	 * Returns the description of this {@link Vendor}.
	 * 
	 * @return The description of this {@link Vendor}.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the name of the {@link Vendor}.
	 * 
	 * @return The name of the {@link Vendor}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Rrturns the {@link PaymentPlan} this {@link Vendor} uses to profile its
	 * {@link App}s.
	 * 
	 * @return The {@link PaymentPlan} this {@link Vendor} uses to profile its
	 *         {@link App}s.
	 */
	public PaymentPlan getPaymentPlan() {
		return paymentPlan;
	}

	/**
	 * Returns the ID of the {@link Vendor} in the DB.
	 * 
	 * @return The ID of the {@link Vendor} in the DB.
	 */
	public Long getVendorID() {
		return vendorID;
	}

	/**
	 * Sets the description of this {@link Vendor}.
	 * 
	 * @param description
	 *            The description of this {@link Vendor}.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the name of the {@link Vendor}.
	 * 
	 * @param name
	 *            The name of the {@link Vendor}.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the {@link PaymentPlan} this {@link Vendor} uses to profile its
	 * {@link App}s.
	 * 
	 * @param paymentPlan
	 *            The {@link PaymentPlan} this {@link Vendor} uses to profile
	 *            its {@link App}s.
	 */
	public void setPaymentPlan(PaymentPlan paymentPlan) {
		this.paymentPlan = paymentPlan;
	}

	/**
	 * Sets the ID of the {@link Vendor} in the DB.
	 * 
	 * @param vendorID
	 *            The ID of the {@link Vendor} in the DB.
	 */
	public void setVendorID(Long vendorID) {
		this.vendorID = vendorID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((paymentPlan == null) ? 0 : paymentPlan.hashCode());
		result = prime * result
				+ ((vendorID == null) ? 0 : vendorID.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vendor other = (Vendor) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (paymentPlan == null) {
			if (other.paymentPlan != null)
				return false;
		} else if (!paymentPlan.equals(other.paymentPlan))
			return false;
		if (vendorID == null) {
			if (other.vendorID != null)
				return false;
		} else if (!vendorID.equals(other.vendorID))
			return false;
		return true;
	}
}
