package org.tud.qmark.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * A {@link Transition} represents one transition in a {@link UseCaseModel}.
 * 
 * @author Claas Wilke
 */
@Entity(name = "Transition")
public class Transition implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -6825503294842711357L;

	/** The description of the {@link Transition}. */
	private String description;

	/** The ID of the {@link Transition}. */
	@Id
	@GeneratedValue
	@Column(name = "transitionID")
	private Long transitionID;

	/** The name of the {@link Transition}. */
	private String name;

	/**
	 * Returns the description of the {@link Transition}.
	 * 
	 * @return The description of the {@link Transition}.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the name of the {@link Transition}.
	 * 
	 * @return The name of the {@link Transition}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the ID of the {@link Transition}.
	 * 
	 * @return The ID of the {@link Transition}.
	 */
	public Long getTransitionID() {
		return transitionID;
	}

	/**
	 * Sets the description of the {@link Transition}.
	 * 
	 * @param description
	 *            The description of the {@link Transition}.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the name of the {@link Transition}.
	 * 
	 * @param name
	 *            The name of the {@link Transition}.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the ID of the {@link Transition}.
	 * 
	 * @param id
	 *            The ID of the {@link Transition}.
	 */
	public void setTransitionID(Long id) {
		this.transitionID = id;
	}
}