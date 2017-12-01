package org.tud.qmark.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

/**
 * A genre describes a typical category of applications (e.g. mail clients or
 * Web browsers).
 * 
 * @author Claas Wilke
 */
@Entity
public class Genre implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -7873163072365444756L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "genreID")
	private Long genreID;

	/** The name of the {@link Genre}. */
	private String name;

	/** A description of the {@link Genre}. */
	private String description;

	/** The {@link UseCaseModel} of this {@link Genre}. */
	@OneToOne
	@JoinColumn(name = "useCaseModelID", unique = true, nullable = false, updatable = false)
	private UseCaseModel useCaseModel;

	/**
	 * Returns the description of the {@link Genre}.
	 * 
	 * @return The description of the {@link Genre}.
	 */
	public String getDescription() {
		return description;
	}

	public Long getGenreID() {
		return genreID;
	}

	/**
	 * Returns the name of the {@link Genre}.
	 * 
	 * @return The name of the {@link Genre}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the {@link UseCaseModel} of this {@link Genre}.
	 * 
	 * @return The {@link UseCaseModel} of this {@link Genre}.
	 */
	public UseCaseModel getUseCaseModel() {
		return useCaseModel;
	}

	/**
	 * Sets the description of the {@link Genre}.
	 * 
	 * @param description
	 *            The description of the {@link Genre}.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public void setGenreID(Long genreID) {
		this.genreID = genreID;
	}

	/**
	 * Sets the name of the {@link Genre}.
	 * 
	 * @param The
	 *            name of the {@link Genre}.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the {@link UseCaseModel} of this {@link Genre}.
	 * 
	 * @param useCaseModel
	 *            The {@link UseCaseModel} of this {@link Genre}.
	 */
	public void setUseCaseModel(UseCaseModel useCaseModel) {
		this.useCaseModel = useCaseModel;
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
		Genre other = (Genre) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (genreID == null) {
			if (other.genreID != null)
				return false;
		} else if (!genreID.equals(other.genreID))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((genreID == null) ? 0 : genreID.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
}
