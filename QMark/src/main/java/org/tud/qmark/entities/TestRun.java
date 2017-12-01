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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Represents a run of a {@link TestSuite}.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 */
@Entity(name = "TestRun")
@Table(name = "TestRun")
public class TestRun implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = 7741204216134847578L;

	/** The console documenting this {@link TestRun} if already executed. */
	@Column(name = "console")
	@Lob
	private String console;

	/** The {@link Device} this {@link TestRun} was or has to be executed on. */
	@ManyToOne()
	@JoinColumn(name = "deviceID", unique = false, updatable = false, nullable = false)
	private Device device;

	/**
	 * The number of seconds the {@link Device}'s idle consumption will be
	 * profiled in front of each iteration of the {@link TestSuite}.
	 */
	@Column(name = "idleTime")
	private Long idleTime;

	/** Indicates whether hardware profiling is enabled or not. */
	@Column(name = "hwProfiling")
	private Boolean isHwProfilingEnabled;

	/** The number of times the associated {@link TestSuite} shall be executed. */
	@Column(name = "numberOfRuns")
	private Long numberOfRuns;

	/** The {@link TestCase}s belonging to this {@link TestRun}. */
	@OneToMany()
	@JoinColumn(name = "testSuiteID")
	private Set<TestCase> testCases;

	/** The ID of this {@link TestRun} in the DB. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "testRunID")
	private Long testRunID;

	/**
	 * The test script of this {@link TestRun}, if any (is copied from
	 * {@link TestSuite} on creation, as the {@link TestSuite}'s test script is
	 * likely to be modified over time).
	 */
	@Column(name = "testScript", nullable = true)
	@Lob
	private String testScript;

	/** The {@link TestSuite} this {@link TestRun} belongs to. */
	@ManyToOne()
	@JoinColumn(name = "testSuiteID", unique = false, updatable = false, nullable = false)
	private TestSuite testSuite;

	/**
	 * The {@link Type} of this {@link TestRun} (e.g., scheduled, finished,
	 * failed).
	 */
	@OneToOne
	@JoinColumn(name = "typeID", unique = false, nullable = false, updatable = true)
	private Type type;

	/**
	 * Returns the console documenting this {@link TestRun} if already executed.
	 * 
	 * @return The console documenting this {@link TestRun} if already executed.
	 */
	public String getConsole() {
		return console;
	}

	/**
	 * Returns the {@link Device} this {@link TestRun} was or has to be executed
	 * on.
	 * 
	 * @return The {@link Device} this {@link TestRun} was or has to be executed
	 *         on.
	 */
	public Device getDevice() {
		return device;
	}

	/**
	 * Returns whether hardware profiling is enabled or not.
	 * 
	 * @return Whether hardware profiling is enabled or not.
	 */
	public Boolean getIsHwProfilingEnabled() {
		return isHwProfilingEnabled;
	}

	/**
	 * Returns the number of seconds the {@link Device}'s idle consumption will
	 * be profiled in front of each iteration of the {@link TestSuite}.
	 * 
	 * @return The number of seconds the {@link Device}'s idle consumption will
	 *         be profiled in front of each iteration of the {@link TestSuite}.
	 */
	public Long getIdleTime() {
		return idleTime;
	}

	/**
	 * Returns the number of times the associated {@link TestSuite} shall be
	 * executed.
	 * 
	 * @return The number of times the associated {@link TestSuite} shall be
	 *         executed.
	 */
	public Long getNumberOfRuns() {
		return numberOfRuns;
	}

	/**
	 * Returns the {@link TestCase}s belonging to this {@link TestRun}.
	 * 
	 * @return The {@link TestCase}s belonging to this {@link TestRun}.
	 */
	public Set<TestCase> getTestCases() {
		return testCases;
	}

	/**
	 * Returns the ID of this {@link TestRun} in the DB.
	 * 
	 * @return The ID of this {@link TestRun} in the DB.
	 */
	public Long getTestRunID() {
		return testRunID;
	}

	/**
	 * Returns the test script of this {@link TestRun}, if any (is copied from
	 * {@link TestSuite} on creation, as the {@link TestSuite}'s test script is
	 * likely to be modified over time).
	 * 
	 * @return The test script of this {@link TestRun}, if any (is copied from
	 *         {@link TestSuite} on creation, as the {@link TestSuite}'s test
	 *         script is likely to be modified over time).
	 */
	public String getTestScript() {
		return testScript;
	}

	/**
	 * Returns the {@link TestSuite} this {@link TestRun} belongs to.
	 * 
	 * @return The {@link TestSuite} this {@link TestRun} belongs to.
	 */
	public TestSuite getTestSuite() {
		return testSuite;
	}

	/**
	 * Returns the {@link Type} of this {@link TestRun} (e.g., scheduled,
	 * finished, failed).
	 * 
	 * @return The {@link Type} of this {@link TestRun} (e.g., scheduled,
	 *         finished, failed).
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Sets the console documenting this {@link TestRun} if already executed.
	 * 
	 * @param console
	 *            The console documenting this {@link TestRun} if already
	 *            executed.
	 */
	public void setConsole(String console) {
		this.console = console;
	}

	/**
	 * Sets the {@link Device} this {@link TestRun} was or has to be executed
	 * on.
	 * 
	 * @param device
	 *            The {@link Device} this {@link TestRun} was or has to be
	 *            executed on.
	 */
	public void setDevice(Device device) {
		this.device = device;
	}

	/**
	 * Sets whether hardware profiling is enabled or not.
	 * 
	 * @param isHwProfilingEnabled
	 *            Whether hardware profiling is enabled or not.
	 */
	public void setIsHwProfilingEnabled(Boolean isHwProfilingEnabled) {
		this.isHwProfilingEnabled = isHwProfilingEnabled;
	}

	/**
	 * Sets the number of seconds the {@link Device}'s idle consumption will be
	 * profiled in front of each iteration of the {@link TestSuite}.
	 * 
	 * @param idleTime
	 *            The number of seconds the {@link Device}'s idle consumption
	 *            will be profiled in front of each iteration of the
	 *            {@link TestSuite}.
	 */
	public void setIdleTime(Long idleTime) {
		this.idleTime = idleTime;
	}

	/**
	 * Sets the number of times the associated {@link TestSuite} shall be
	 * executed.
	 * 
	 * @param numberOfRuns
	 *            The number of times the associated {@link TestSuite} shall be
	 *            executed.
	 */
	public void setNumberOfRuns(Long numberOfRuns) {
		this.numberOfRuns = numberOfRuns;
	}

	/**
	 * Sets the {@link TestCase}s belonging to this {@link TestRun}.
	 * 
	 * @param testCases
	 *            The {@link TestCase}s belonging to this {@link TestRun}.
	 */
	public void setTestCases(Set<TestCase> testCases) {
		this.testCases = testCases;
	}

	/**
	 * Sets the ID of this {@link TestRun} in the DB.
	 * 
	 * @param testRunID
	 *            The ID of this {@link TestRun} in the DB.
	 */
	public void setTestRunID(Long testRunID) {
		this.testRunID = testRunID;
	}

	/**
	 * Sets the test script of this {@link TestRun}, if any (is copied from
	 * {@link TestSuite} on creation, as the {@link TestSuite}'s test script is
	 * likely to be modified over time).
	 * 
	 * @param testScript
	 *            The test script of this {@link TestRun}, if any (is copied
	 *            from {@link TestSuite} on creation, as the {@link TestSuite}'s
	 *            test script is likely to be modified over time).
	 */
	public void setTestScript(String testScript) {
		this.testScript = testScript;
	}

	/**
	 * The {@link TestSuite} this {@link TestRun} belongs to.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite} this {@link TestRun} belongs to.
	 */
	public void setTestSuite(TestSuite testSuite) {
		this.testSuite = testSuite;
	}

	/**
	 * Sets the {@link Type} of this {@link TestRun} (e.g., scheduled, finished,
	 * failed).
	 * 
	 * @param type
	 *            The {@link Type} of this {@link TestRun} (e.g., scheduled,
	 *            finished, failed).
	 */
	public void setType(Type type) {
		this.type = type;
	}
}