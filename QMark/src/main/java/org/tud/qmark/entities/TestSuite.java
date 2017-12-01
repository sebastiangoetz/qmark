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
 * Represents a {@link TestSuite} that can be executed for a specific
 * {@link App} {@link Version}. Such an execution will result in a
 * {@link TestRun}.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 */
@Entity(name = "TestSuite")
@Table(name = "TestSuite")
public class TestSuite implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = 6836508400595577475L;

	/** The APK file ({@link Binary}) of this {@link TestSuite}, if any. */
	@OneToOne
	@JoinColumn(name = "apkBinaryID", nullable = true, unique = false)
	private Binary apk;

	/** The name of this {@link TestSuite}. */
	@Column(name = "name", nullable = false)
	private String name;

	/** The ID of this {@link TestSuite} in the DB. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "testSuiteID")
	private Long testSuiteID;

	/** All {@link TestRun}s belonging to this {@link TestSuite}. */
	@OneToMany()
	@JoinColumn(name = "testSuiteID")
	private Set<TestRun> testRuns;

	/** The test script of this {@link TestSuite}, if any. */
	@Column(name = "testScript", nullable = true)
	@Lob
	private String testScript;

	/**
	 * The package ID of the test suite APK belonging to this {@link TestSuite}
	 * (if any).
	 */
	@Column(name = "packageID", nullable = true)
	private String packageID;

	/** The {@link App} {@link Version} this {@link TestSuite} belongs to. */
	@ManyToOne
	@JoinColumn(name = "versionID", unique = false, updatable = false, nullable = false)
	private Version version;

	/**
	 * Returns the APK file ({@link Binary}) of this {@link TestSuite}, if any.
	 * 
	 * @return The APK file ({@link Binary}) of this {@link TestSuite}, if any.
	 */
	public Binary getApk() {
		return apk;
	}

	/**
	 * Returns the name of this {@link TestSuite}.
	 * 
	 * @return The name of this {@link TestSuite}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns all {@link TestRun}s belonging to this {@link TestSuite}.
	 * 
	 * @return All {@link TestRun}s belonging to this {@link TestSuite}.
	 */
	public Set<TestRun> getTestRuns() {
		return testRuns;
	}

	/**
	 * Returns the test script of this {@link TestSuite}, if any.
	 * 
	 * @return The test script of this {@link TestSuite}, if any.
	 */
	public String getTestScript() {
		return testScript;
	}

	/**
	 * Returns the ID of this {@link TestSuite} in the DB.
	 * 
	 * @return The ID of this {@link TestSuite} in the DB.
	 */
	public Long getTestSuiteID() {
		return testSuiteID;
	}

	/**
	 * Returns the {@link App} {@link Version} this {@link TestSuite} belongs
	 * to.
	 * 
	 * @return The {@link App} {@link Version} this {@link TestSuite} belongs
	 *         to.
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Sets the APK file ({@link Binary}) of this {@link TestSuite}, if any.
	 * 
	 * @param apk
	 *            The APK file ({@link Binary}) of this {@link TestSuite}, if
	 *            any.
	 */
	public void setApk(Binary apk) {
		this.apk = apk;
	}

	/**
	 * Sets the name of this {@link TestSuite}.
	 * 
	 * @param name
	 *            The name of this {@link TestSuite}.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets all {@link TestRun}s belonging to this {@link TestSuite}.
	 * 
	 * @param testRuns
	 *            All {@link TestRun}s belonging to this {@link TestSuite}.
	 */
	public void setTestRuns(Set<TestRun> testRuns) {
		this.testRuns = testRuns;
	}

	/**
	 * Sets the test script of this {@link TestSuite}, if any.
	 * 
	 * @param testScript
	 *            The test script of this {@link TestSuite}, if any.
	 */
	public void setTestScript(String testScript) {
		this.testScript = testScript;
	}

	/**
	 * Sets the ID of this {@link TestSuite} in the DB.
	 * 
	 * @param testSuiteID
	 *            The ID of this {@link TestSuite} in the DB.
	 */
	public void setTestSuiteID(Long testSuiteID) {
		this.testSuiteID = testSuiteID;
	}

	/**
	 * Sets the {@link App} {@link Version} this {@link TestSuite} belongs to.
	 * 
	 * @param version
	 *            The {@link App} {@link Version} this {@link TestSuite} belongs
	 *            to.
	 */
	public void setVersion(Version version) {
		this.version = version;
	}

	/**
	 * Returns the package ID of the test suite APK belonging to this
	 * {@link TestSuite} (if any).
	 * 
	 * @return The package ID of the test suite APK belonging to this
	 *         {@link TestSuite} (if any).
	 */
	public String getPackageID() {
		return packageID;
	}

	/**
	 * Sets the package ID of the test suite APK belonging to this
	 * {@link TestSuite} (if any).
	 * 
	 * @param packageID
	 *            The package ID of the test suite APK belonging to this
	 *            {@link TestSuite} (if any).
	 */
	public void setPackageID(String packageID) {
		this.packageID = packageID;
	}
}
