/*
 * Created on 24.10.2012
 */

package org.tud.qmark.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Further meta data for {@link User} entities.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 */
@Entity
@Table(name = "UserMetaData")
public class UserMetaData implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = 8880262618797928740L;

	/**
	 * The {@link Vendor}s this {@link UserMetaData} belongs to identified by
	 * {@link CompanyMember} relations.
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "userMetaDataID")
	private List<CompanyMember> companies;

	/** The gender of this {@link UserMetaData}. */
	@OneToOne
	@JoinColumn(name = "genderTypeID", unique = false, nullable = true, updatable = true)
	private Type gender;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userMetaDataID")
	private Long userMetaDataID;
	@Column(name = "title")
	private String title;
	@Column(name = "firstname")
	private String firstname;
	@Column(name = "surname")
	private String surname;

	/**
	 * Returns the {@link CompanyMember}s this {@link UserMetaData} belongs to.
	 * 
	 * @return The {@link CompanyMember}s this {@link UserMetaData} belongs to.
	 */
	public List<CompanyMember> getCompanies() {
		return companies;
	}

	/**
	 * Returns the gender of this {@link UserMetaData}.
	 * 
	 * @return The gender of this {@link UserMetaData}.
	 */
	public Type getGender() {
		return gender;
	}

	/**
	 * Sets the {@link CompanyMember}s this {@link UserMetaData} belongs to.
	 * 
	 * @param companies
	 *            The {@link CompanyMember}s this {@link UserMetaData} belongs
	 *            to.
	 */
	public void setCompanies(List<CompanyMember> companies) {
		this.companies = companies;
	}

	/**
	 * Sets the gender of this {@link UserMetaData}.
	 * 
	 * @param gender
	 *            The gender of this {@link UserMetaData}.
	 */
	public void setGender(Type gender) {
		this.gender = gender;
	}

	@OneToOne(optional = false)
	public long getUserMetaDataID() {
		return userMetaDataID;
	}

	public void setUserMetaDataID(Long userMetaDataID) {
		this.userMetaDataID = userMetaDataID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
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
		UserMetaData other = (UserMetaData) obj;
		if (userMetaDataID == null) {
			if (other.userMetaDataID != null)
				return false;
		} else if (!userMetaDataID.equals(other.userMetaDataID))
			return false;
		return true;
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
				+ ((userMetaDataID == null) ? 0 : userMetaDataID.hashCode());
		return result;
	}
}
