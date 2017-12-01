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
import javax.persistence.Table;

/**
 * Represents the average results for {@link TestCase}s of a {@link TestRun},
 * separated by their name.
 * 
 * @author Claas Wilke
 */
@Entity
@Table(name = "AvgTestCase")
public class AvgTestCase implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = 6787817127741266375L;

	/** The average duration of this {@link AvgTestCase}. */
	@Column(name = "avgDuration")
	private Long avgDuration;

	/**
	 * The average power rate profiled for this {@link AvgTestCase}.
	 */
	@Column(name = "avgPowerRate", nullable = true)
	private Long avgPowerRate;

	/** The ID of this {@link AvgTestCase}. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "avgTestCaseID")
	private Long avgTestCaseID;

	/**
	 * The number of {@link TestCase}s belonging to this {@link AvgTestCase}
	 * those execution failed.
	 */
	@Column(name = "failedCases")
	private Integer failedCases;

	/** The name of the {@link TestCase}s belonging to this {@link AvgTestCase}. */
	@Column(name = "name")
	private String name;

	/** The ID of the {@link TestRun}, this {@link AvgTestCase} belongs to. */
	@Column(name = "testRunID")
	private Long testRunID;

	/**
	 * The number of {@link TestCase}s belonging to this {@link AvgTestCase}
	 * that finished successfully.
	 */
	@Column(name = "okCases")
	private Integer okCases;

	/**
	 * Returns the average duration of this {@link AvgTestCase}.
	 * 
	 * @return The average duration of this {@link AvgTestCase}.
	 */
	public Long getAvgDuration() {
		return avgDuration;
	}

	/**
	 * Returns the average power rate profiled for this {@link AvgTestCase}.
	 * 
	 * @return The average power rate profiled for this {@link AvgTestCase}.
	 */
	public Long getAvgPowerRate() {
		return avgPowerRate;
	}

	/**
	 * Returns the ID of this {@link AvgTestCase}.
	 * 
	 * @return The ID of this {@link AvgTestCase}.
	 */
	public Long getAvgTestCaseID() {
		return avgTestCaseID;
	}

	/**
	 * Returns the number of {@link TestCase}s belonging to this
	 * {@link AvgTestCase} those execution failed.
	 * 
	 * @return The number of {@link TestCase}s belonging to this
	 *         {@link AvgTestCase} those execution failed.
	 */
	public Integer getFailedCases() {
		return failedCases;
	}

	/**
	 * Returns the name of the {@link TestCase}s belonging to this
	 * {@link AvgTestCase}.
	 * 
	 * @return The name of the {@link TestCase}s belonging to this
	 *         {@link AvgTestCase}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the number of {@link TestCase}s belonging to this
	 * {@link AvgTestCase} that finished successfully.
	 * 
	 * @return The number of {@link TestCase}s belonging to this
	 *         {@link AvgTestCase} that finished successfully.
	 */
	public Integer getOkCases() {
		return okCases;
	}

	/**
	 * Returns the ID of the {@link TestRun}, this {@link AvgTestCase} belongs
	 * to.
	 * 
	 * @return The ID of the {@link TestRun}, this {@link AvgTestCase} belongs
	 *         to.
	 */
	public Long getTestRunID() {
		return testRunID;
	}

	/**
	 * Sets the average duration of this {@link AvgTestCase}.
	 * 
	 * @param avgDuration
	 *            The average duration of this {@link AvgTestCase}.
	 */
	public void setAvgDuration(Long avgDuration) {
		this.avgDuration = avgDuration;
	}

	/**
	 * Sets the average power rate profiled for this {@link AvgTestCase}.
	 * 
	 * @param avgPowerRate
	 *            The average power rate profiled for this {@link AvgTestCase}.
	 */
	public void setAvgPowerRate(Long avgPowerRate) {
		this.avgPowerRate = avgPowerRate;
	}

	/**
	 * Sets the ID of this {@link AvgTestCase}.
	 * 
	 * @param avgTestCaseID
	 *            The ID of this {@link AvgTestCase}.
	 */
	public void setAvgTestCaseID(Long avgTestCaseID) {
		this.avgTestCaseID = avgTestCaseID;
	}

	/**
	 * Sets the number of {@link TestCase}s belonging to this
	 * {@link AvgTestCase} those execution failed.
	 * 
	 * @param failedCases
	 *            The number of {@link TestCase}s belonging to this
	 *            {@link AvgTestCase} those execution failed.
	 */
	public void setFailedCases(Integer failedCases) {
		this.failedCases = failedCases;
	}

	/**
	 * Sets the name of the {@link TestCase}s belonging to this
	 * {@link AvgTestCase}.
	 * 
	 * @param name
	 *            The name of the {@link TestCase}s belonging to this
	 *            {@link AvgTestCase}.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the number of {@link TestCase}s belonging to this
	 * {@link AvgTestCase} that finished successfully.
	 * 
	 * @param okCases
	 *            The number of {@link TestCase}s belonging to this
	 *            {@link AvgTestCase} that finished successfully.
	 */
	public void setOkCases(Integer okCases) {
		this.okCases = okCases;
	}

	/**
	 * Sets the ID of the {@link TestRun}, this {@link AvgTestCase} belongs to.
	 * 
	 * @param testRunID
	 *            The ID of the {@link TestRun}, this {@link AvgTestCase}
	 *            belongs to.
	 */
	public void setTestRunID(Long testRunID) {
		this.testRunID = testRunID;
	}
}
