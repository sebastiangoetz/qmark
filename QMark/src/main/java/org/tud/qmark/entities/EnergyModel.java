package org.tud.qmark.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * A {@link EnergyModel} represents the power consumption for the use cases of a
 * {@link Genre} for on {@link App}.
 * 
 * @author Claas Wilke
 */
@Entity
public class EnergyModel implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -4844859205693362962L;

	/** The {@link Device} of this {@link EnergyModel}. */
	@OneToOne
	@JoinColumn(name = "deviceID", unique = false, nullable = false, updatable = false)
	private Device device;

	/** The ID of the {@link EnergyModel}. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "energyModelID")
	private Long energyModelID;

	/** The {@link PowerRate}s of this {@link EnergyModel}. */
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "EnergyModelToPowerRate", joinColumns = @JoinColumn(name = "energyModelID"), inverseJoinColumns = @JoinColumn(name = "powerRateID"))
	private Set<PowerRate> powerRates;

	/** The {@link Date} on which this {@link EnergyModel} has been profiled. */
	@Column(name = "profilingDate")
	private Date profilingDate;

	/** The {@link UseCaseModel} of this {@link EnergyModel}. */
	@OneToOne
	@JoinColumn(name = "useCaseModelID", unique = false, nullable = false, updatable = false)
	private UseCaseModel useCaseModel;

	/**
	 * Returns the {@link Device} of this {@link EnergyModel}.
	 * 
	 * @return The {@link Device} of this {@link EnergyModel}.
	 */
	public Device getDevice() {
		return device;
	}

	/**
	 * Returns the ID of the {@link EnergyModel}.
	 * 
	 * @return The ID of the {@link EnergyModel}.
	 */
	public Long getEnergyModelID() {
		return energyModelID;
	}

	/**
	 * Returns the {@link PowerRate}s of this {@link EnergyModel}.
	 * 
	 * @return The {@link PowerRate}s of this {@link EnergyModel}.
	 */
	public Set<PowerRate> getPowerRates() {
		return powerRates;
	}

	/**
	 * Returns the {@link Date} on which this {@link EnergyModel} has been
	 * profiled.
	 * 
	 * @return The {@link Date} on which this {@link EnergyModel} has been
	 *         profiled.
	 */
	public Date getProfilingDate() {
		return profilingDate;
	}

	/**
	 * Returns the {@link UseCaseModel} of this {@link EnergyModel}.
	 * 
	 * @return The {@link UseCaseModel} of this {@link EnergyModel}.
	 */
	public UseCaseModel getUseCaseModel() {
		return useCaseModel;
	}

	/**
	 * Sets the {@link Device} of this {@link EnergyModel}.
	 * 
	 * @param device
	 *            The {@link Device} of this {@link EnergyModel}.
	 */
	public void setDevice(Device device) {
		this.device = device;
	}

	/**
	 * Sets the ID of the {@link EnergyModel}.
	 * 
	 * @param id
	 *            The ID of the {@link EnergyModel}.
	 */
	public void setEnergyModelID(Long id) {
		this.energyModelID = id;
	}

	/**
	 * Sets the {@link PowerRate}s of this {@link EnergyModel}.
	 * 
	 * @param powerRates
	 *            The {@link PowerRate}s of this {@link EnergyModel}.
	 */
	public void setPowerRates(Set<PowerRate> powerRates) {
		this.powerRates = powerRates;
	}

	/**
	 * Sets the {@link Date} on which this {@link EnergyModel} has been
	 * profiled.
	 * 
	 * @param profilingDate
	 *            The {@link Date} on which this {@link EnergyModel} has been
	 *            profiled.
	 */
	public void setProfilingDate(Date profilingDate) {
		this.profilingDate = profilingDate;
	}

	/**
	 * Sets the {@link UseCaseModel} of this {@link EnergyModel}.
	 * 
	 * @param useCaseModel
	 *            The {@link UseCaseModel} of this {@link EnergyModel}.
	 */
	public void setUseCaseModel(UseCaseModel useCaseModel) {
		this.useCaseModel = useCaseModel;
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
		EnergyModel other = (EnergyModel) obj;
		if (energyModelID == null) {
			if (other.energyModelID != null)
				return false;
		} else if (!energyModelID.equals(other.energyModelID))
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
				+ ((energyModelID == null) ? 0 : energyModelID.hashCode());
		return result;
	}
}