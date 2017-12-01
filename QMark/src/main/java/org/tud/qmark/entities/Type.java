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
 * The represents the type of a {@link PaymentPlan}.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 */
@Entity(name = "Type")
@Table(name = "Type")
public class Type implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -7845371991745116982L;

	/** The description of this {@link Type}. */
	@Column(name = "description")
	private String description;

	/** The name of this {@link Type}. */
	@Column(name = "name")
	private String name;

	/** The super {@link Type} of this {@link Type}. */
	@OneToOne(optional = true)
	@JoinColumn(name = "typeTypeID", unique = false, nullable = true, updatable = false)
	private Type typeType;

	/** The ID of this {@link Type} in the DB. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "typeID")
	private Long typeID;

	/**
	 * Returns the description of this {@link Type}.
	 * 
	 * @return The description of this {@link Type}.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the name of this {@link Type}.
	 * 
	 * @return The name of this {@link Type}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the ID of this {@link Type} in the DB.
	 * 
	 * @return The ID of this {@link Type} in the DB.
	 */
	public Long getTypeID() {
		return typeID;
	}

	/**
	 * Returns the super {@link Type} of this {@link Type}.
	 * 
	 * @return The super {@link Type} of this {@link Type}.
	 */
	public Type getTypeType() {
		return typeType;
	}

	/**
	 * Checks whether this {@link Type} or one of its super {@link Type}s has
	 * the given name.
	 * 
	 * @param name
	 *            The name to be checked.
	 * @return <code>true</code> if this {@link Type} or one of its super
	 *         {@link Type}s has the given name.
	 */
	public boolean isOfType(String name) {

		if (getName().equals(name))
			return true;
		else if (null != getTypeType())
			return getTypeType().isOfType(name);
		else
			return false;
	}

	/**
	 * Sets the description of this {@link Type}.
	 * 
	 * @param description
	 *            The description of this {@link Type}.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the name of this {@link Type}.
	 * 
	 * @param name
	 *            The name of this {@link Type}.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the ID of this {@link Type} in the DB.
	 * 
	 * @param typeID
	 *            The ID of this {@link Type} in the DB.
	 */
	public void setTypeID(Long typeID) {
		this.typeID = typeID;
	}

	/**
	 * Sets the super {@link Type} of this {@link Type}.
	 * 
	 * @param typeType
	 *            The super {@link Type} of this {@link Type}.
	 */
	public void setTypeType(Type typeType) {
		this.typeType = typeType;
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
		Type other = (Type) obj;
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
		if (typeID == null) {
			if (other.typeID != null)
				return false;
		} else if (!typeID.equals(other.typeID))
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
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((typeID == null) ? 0 : typeID.hashCode());
		return result;
	}
}