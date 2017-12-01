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
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

/**
 * A {@link UseCaseModel} represents the use cases of a {@link Genre}.
 * 
 * @author Claas Wilke
 */
@Entity(name = "UseCaseModel")
public class UseCaseModel implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = 8497503426453440044L;

	/** The description of the use cases belonging to this {@link UseCaseModel}. */
	@Lob
	@Column(name = "description", nullable = false, updatable = true)
	private String description;

	/** The {@link Transition}s of this {@link UseCaseModel}. */
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "UseCaseModelToTransition", joinColumns = @JoinColumn(name = "useCaseModelID"), inverseJoinColumns = @JoinColumn(name = "transitionID"))
	private List<Transition> transitions;

	/** The ID {@link UseCaseModel}. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "useCaseModelID")
	private Long useCaseModelID;

	/**
	 * Returns the description of the use cases belonging to this
	 * {@link UseCaseModel}.
	 * 
	 * @return The description of the use cases belonging to this
	 *         {@link UseCaseModel}.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the {@link Transition}s of this {@link UseCaseModel}.
	 * 
	 * @return The {@link Transition}s of this {@link UseCaseModel} as a
	 *         {@link List}.
	 */
	public List<Transition> getTransitions() {
		return transitions;
	}

	/**
	 * Returns the ID {@link UseCaseModel}.
	 * 
	 * @return The ID {@link UseCaseModel}.
	 */
	public Long getUseCaseModelID() {
		return useCaseModelID;
	}

	/**
	 * Sets the description of the use cases belonging to this
	 * {@link UseCaseModel}.
	 * 
	 * @param description
	 *            The description of the use cases belonging to this
	 *            {@link UseCaseModel}.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the {@link Transition}s of this {@link UseCaseModel}.
	 * 
	 * @param transitions
	 *            The {@link Transition}s of this {@link UseCaseModel}.
	 */
	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	/**
	 * Sets the ID {@link UseCaseModel}.
	 * 
	 * @param id
	 *            The ID {@link UseCaseModel}.
	 */
	public void setUseCaseModelID(Long id) {
		this.useCaseModelID = id;
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
		UseCaseModel other = (UseCaseModel) obj;
		if (useCaseModelID == null) {
			if (other.useCaseModelID != null)
				return false;
		} else if (!useCaseModelID.equals(other.useCaseModelID))
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
				+ ((useCaseModelID == null) ? 0 : useCaseModelID.hashCode());
		return result;
	}

}