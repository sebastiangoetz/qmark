package org.tud.qmark.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * A {@link PowerRate} represents the power rate of one {@link Transition} in an
 * {@link EnergyModel}.
 * 
 * @author Claas Wilke
 */
@Entity
public class PowerRate implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -8858473303295065692L;

	/** The ID of the {@link PowerRate}. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "powerRateID")
	private Long powerRateID;

	/** The {@link Transition} of the {@link PowerRate}. */
	@OneToOne
	@JoinColumn(name = "transitionID", unique = false, nullable = false, updatable = false)
	private Transition transition;

	/** The value of the {@link PowerRate}. */
	@Column(name = "value", unique = false, nullable = true)
	private Double value;

	/**
	 * Returns the ID of the {@link PowerRate}.
	 * 
	 * @return The ID of the {@link PowerRate}.
	 */
	public Long getPowerRateID() {
		return powerRateID;
	}

	/**
	 * Returns the {@link Transition} of the {@link PowerRate}.
	 * 
	 * @return The {@link Transition} of the {@link PowerRate}.
	 */
	public Transition getTransition() {
		return transition;
	}

	/**
	 * Returns the value of the {@link PowerRate}.
	 * 
	 * @return The value of the {@link PowerRate}.
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * Sets the ID of the {@link PowerRate}.
	 * 
	 * @param id
	 *            The ID of the {@link PowerRate}.
	 */
	public void setPowerRateID(Long id) {
		this.powerRateID = id;
	}

	/**
	 * Sets the {@link Transition} of the {@link PowerRate}.
	 * 
	 * @param transition
	 *            The {@link Transition} of the {@link PowerRate}.
	 */
	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	/**
	 * Sets the value of the {@link PowerRate}.
	 * 
	 * @param value
	 *            The value of the {@link PowerRate}.
	 */
	public void setValue(Double value) {
		this.value = value;
	}
}