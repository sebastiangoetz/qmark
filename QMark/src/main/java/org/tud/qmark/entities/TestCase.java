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
 * Represents the result of one execution of one specific {@link TestCase}.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 */
@Entity(name = "TestCase")
@Table(name = "TestCase")
public class TestCase implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = 6787817127741266375L;

	/**
	 * The Tag of this {@link TestCase} which allows to add further metadata
	 * besides the name of this {@link TestCase}.
	 */
	@Column(name = "tag")
	private String tag;

	/**
	 * The average power rate profiled for this {@link TestCase}. Can be
	 * <code>null</code>, if it has not been computed from the profiling data
	 * yet.
	 */
	@Column(name = "avgPowerRate", nullable = true)
	private Long avgPowerRate;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "testCaseID")
	private Long testCaseID;
	@Column(name = "testRunID")
	private Long testRunID;
	@Column(name = "name")
	private String name;
	@Column(name = "start")
	private Long start;
	@Column(name = "stop")
	private Long stop;
	@Column(name = "result")
	private Integer result;

	/**
	 * Returns the average power rate profiled for this {@link TestCase}. Can be
	 * <code>null</code>, if it has not been computed from the profiling data
	 * yet.
	 * 
	 * @return The average power rate profiled for this {@link TestCase}. Can be
	 *         <code>null</code>, if it has not been computed from the profiling
	 *         data yet.
	 */
	public Long getAvgPowerRate() {
		return avgPowerRate;
	}

	/**
	 * Returns the Tag of this {@link TestCase} which allows to add further
	 * metadata besides the name of this {@link TestCase}.
	 * 
	 * @return The Tag of this {@link TestCase} which allows to add further
	 *         metadata besides the name of this {@link TestCase}.
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Sets the average power rate profiled for this {@link TestCase}. Can be
	 * <code>null</code>, if it has not been computed from the profiling data
	 * yet.
	 * 
	 * @param avgPowerRate
	 *            The average power rate profiled for this {@link TestCase}. Can
	 *            be <code>null</code>, if it has not been computed from the
	 *            profiling data yet.
	 */
	public void setAvgPowerRate(Long avgPowerRate) {
		this.avgPowerRate = avgPowerRate;
	}

	/**
	 * Sets the Tag of this {@link TestCase} which allows to add further
	 * metadata besides the name of this {@link TestCase}.
	 * 
	 * @param tag
	 *            The Tag of this {@link TestCase} which allows to add further
	 *            metadata besides the name of this {@link TestCase}.
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	public Long getTestCaseID() {
		return testCaseID;
	}

	public void setTestCaseID(Long testCaseID) {
		this.testCaseID = testCaseID;
	}

	public Long getTestRunID() {
		return testRunID;
	}

	public void setTestRunID(Long testRunID) {
		this.testRunID = testRunID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public Long getStop() {
		return stop;
	}

	public void setStop(Long stop) {
		this.stop = stop;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

}
