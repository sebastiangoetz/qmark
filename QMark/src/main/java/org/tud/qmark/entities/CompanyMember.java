/*
 * Created on 30.10.2012
 */

package org.tud.qmark.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * A {@link CompanyMember} represents relations between {@link User}s and
 * {@link Vendor}s. {@link User}s can own {@link Vendor}s to invite other
 * {@link User}s into this group or can be members of {@link Vendor}s only,
 * identified by the {@link Type} of the {@link CompanyMember} relation.
 * 
 * @author Claas Wilke
 */
@Entity
@Table(name = "CompanyMember")
public class CompanyMember implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = 3854742437864016772L;

	/** The ID of this {@link CompanyMember}. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "companyMemberID")
	private Long companyMemberID;

	/** The {@link Type} of this {@link CompanyMember}. */
	@OneToOne
	@JoinColumn(name = "typeID", unique = false, nullable = false, updatable = true)
	private Type type;

	/** The {@link UserMetaData} of this {@link CompanyMember}. */
	@ManyToOne
	@JoinColumn(name = "userMetaDataID", unique = false, nullable = false, updatable = false)
	private UserMetaData userMetaData;

	/** The {@link Vendor} of this {@link CompanyMember}. */
	@OneToOne
	@JoinColumn(name = "vendorID", unique = false, nullable = false, updatable = false)
	private Vendor vendor;

	/**
	 * Returns the ID of this {@link CompanyMember}.
	 * 
	 * @return The ID of this {@link CompanyMember}.
	 */
	public Long getCompanyMemberID() {
		return companyMemberID;
	}

	/**
	 * Returns the {@link Type} of this {@link CompanyMember}.
	 * 
	 * @return The {@link Type} of this {@link CompanyMember}.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the {@link UserMetaData} of this {@link CompanyMember}.
	 * 
	 * @return The {@link UserMetaData} of this {@link CompanyMember}.
	 */
	public UserMetaData getUserMetaData() {
		return userMetaData;
	}

	/**
	 * Returns the {@link Vendor} of this {@link CompanyMember}.
	 * 
	 * @return The {@link Vendor} of this {@link CompanyMember}.
	 */
	public Vendor getVendor() {
		return vendor;
	}

	/**
	 * Sets the ID of this {@link CompanyMember}.
	 * 
	 * @param companyMemberID
	 *            The ID of this {@link CompanyMember}.
	 */
	public void setCompanyMemberID(Long companyMemberID) {
		this.companyMemberID = companyMemberID;
	}

	/**
	 * Sets the {@link Type} of this {@link CompanyMember}.
	 * 
	 * @param type
	 *            The {@link Type} of this {@link CompanyMember}.
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Sets the {@link UserMetaData} of this {@link CompanyMember}.
	 * 
	 * @param user
	 *            The {@link UserMetaData} of this {@link CompanyMember}.
	 */
	public void setUserMetaData(UserMetaData userMetaData) {
		this.userMetaData = userMetaData;
	}

	/**
	 * Sets the {@link Vendor} of this {@link CompanyMember}.
	 * 
	 * @param app
	 *            The {@link Vendor} of this {@link CompanyMember}.
	 */
	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}
}
