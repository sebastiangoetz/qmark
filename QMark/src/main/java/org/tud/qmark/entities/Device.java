/*
 * Created on 24.10.2012
 */

package org.tud.qmark.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Represents an Android device used for energy profiling.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 */
@Entity
@Table(name = "Device")
public class Device implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -3977858119427790555L;

	/** The {@link Device}'s battery capacity in <code>mWh</code>. */
	@Column(name = "batteryCapacity")
	private Long batteryCapacity;

	/**
	 * Describes the configuration of the {@link Device} (e.g., which Android
	 * version is installed).
	 */
	@Column(name = "configuration")
	private String configuration;

	/** The description of the {@link Device}. */
	@Column(name = "description")
	private String description;

	/** The ID of the {@link Device}. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "deviceID")
	private Long deviceID;

	/**
	 * The {@link Device}'s {@link EnergyModel} indicating its idle energy
	 * consumption.
	 */
	@OneToOne
	@JoinColumn(name = "energyModelID", unique = true, nullable = false, updatable = false)
	private EnergyModel energyModel;

	/** The name of the {@link Device}. */
	@Column(name = "name")
	private String name;

	/** The {@link TestRun}s executed on this {@link Device}. */
	@OneToMany()
	@JoinColumn(name = "deviceID")
	private Set<TestRun> testRuns;

	/**
	 * Returns the {@link Device}'s battery capacity in <code>mWh</code>.
	 * 
	 * @return The {@link Device}'s battery capacity in <code>mWh</code>.
	 */
	public long getBatteryCapacity() {
		return batteryCapacity;
	}

	/**
	 * Returns the configuration of the {@link Device} (e.g., which Android
	 * version is installed).
	 * 
	 * @return The configuration of the {@link Device} (e.g., which Android
	 *         version is installed).
	 */
	public String getConfiguration() {
		return configuration;
	}

	/**
	 * Returns the description of the {@link Device}.
	 * 
	 * @return The description of the {@link Device}.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the ID of the {@link Device}.
	 * 
	 * @return The ID of the {@link Device}.
	 */
	public Long getDeviceID() {
		return deviceID;
	}

	/**
	 * Returns the {@link Device}'s {@link EnergyModel} indicating its idle
	 * energy consumption.
	 * 
	 * @return The {@link Device}'s {@link EnergyModel} indicating its idle
	 *         energy consumption.
	 */
	public EnergyModel getEnergyModel() {
		return energyModel;
	}

	/**
	 * Returns the name of the {@link Device}.
	 * 
	 * @return The name of the {@link Device}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the {@link TestRun}s executed on this {@link Device}.
	 * 
	 * @return The {@link TestRun}s executed on this {@link Device}.
	 */
	public Set<TestRun> getTestRuns() {
		return testRuns;
	}

	/**
	 * Sets the {@link Device}'s battery capacity in <code>mWh</code>.
	 * 
	 * @param batteryCapacity
	 *            The {@link Device}'s battery capacity in <code>mWh</code>.
	 */
	public void setBatteryCapacity(long batteryCapacity) {
		this.batteryCapacity = batteryCapacity;
	}

	/**
	 * Sets the configuration of the {@link Device} (e.g., which Android version
	 * is installed).
	 * 
	 * @param configuration
	 *            The configuration of the {@link Device} (e.g., which Android
	 *            version is installed).
	 */
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	/**
	 * Sets the description of the {@link Device}.
	 * 
	 * @param description
	 *            The description of the {@link Device}.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the ID of the {@link Device}.
	 * 
	 * @param deviceID
	 *            The ID of the {@link Device}.
	 */
	public void setDeviceID(Long deviceID) {
		this.deviceID = deviceID;
	}

	/**
	 * Sets the {@link Device}'s {@link EnergyModel} indicating its idle energy
	 * consumption.
	 * 
	 * @param energyModel
	 *            The {@link Device}'s {@link EnergyModel} indicating its idle
	 *            energy consumption.
	 */
	public void setEnergyModel(EnergyModel energyModel) {
		this.energyModel = energyModel;
	}

	/**
	 * Sets the name of the {@link Device}.
	 * 
	 * @param name
	 *            The name of the {@link Device}.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the {@link TestRun}s executed on this {@link Device}.
	 * 
	 * @param testRuns
	 *            The {@link TestRun}s executed on this {@link Device}.
	 */
	public void setTestRuns(Set<TestRun> testRuns) {
		this.testRuns = testRuns;
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
		Device other = (Device) obj;
		if (deviceID == null) {
			if (other.deviceID != null)
				return false;
		} else if (!deviceID.equals(other.deviceID))
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
				+ ((deviceID == null) ? 0 : deviceID.hashCode());
		return result;
	}
}
