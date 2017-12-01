package org.tud.qmark.jouleunit.service.db;

import org.qualitune.jouleunit.android.testrun.TestRun;

/**
 * Contains all information of one test run required from the DB.
 * 
 * @author Claas Wilke
 */
public class DbTestRun {

	/** The ID of the Apk file of the Application under test in the DB. */
	private int autApkID;

	/** The package ID of the Application under test. */
	private String autPackageID;

	/** Indicates whether or not hardware profiling shall be enabled. */
	private boolean hardwareProfilingEnabled;

	/** The time in seconds to profile the DUT's idle power consumption. */
	private int idleProfilingTime;

	/** The number of test runs to be performed. */
	private int noOfRuns;

	/** The ID of the Apk file of the test suite in the DB. */
	private int testApkID;

	/** The package ID of the test suite. */
	private String testPackageID;

	/** The ID of this {@link DbTestRun} in the DB. */
	private int testRunID;

	/** The script of this {@link DbTestRun} in the DB. */
	private String testScript;

	/**
	 * Creates a new {@link DbTestRun}.
	 * 
	 * @param testRunID
	 *            The ID of this {@link DbTestRun} in the DB.
	 * @param hardwareProfilingEnable
	 *            Indicates whether or not hardware profiling shall be enabled.
	 * @param idleProfilingTime
	 *            The time in seconds to profile the DUT's idle power
	 *            consumption.
	 * @param autApkID
	 *            The ID of the Apk file of the Application under test in the
	 *            DB.
	 * @param autPackageID
	 *            The package ID of the Application under test.
	 * @param testApkID
	 *            The ID of the Apk file of the test suite in the DB.
	 * @param testPackageID
	 *            The package ID of the test suite.
	 * @param noOfRuns
	 *            The number of test runs to be performed.
	 * @param testScript
	 *            The script of this {@link DbTestRun} in the DB.
	 */
	public DbTestRun(int testRunID, boolean hardwareProfilingEnable,
			int idleProfilingTime, int autApkID, String autPackageID,
			int testApkID, String testPackageID, int noOfRuns, String testScript) {
		this.testRunID = testRunID;
		this.hardwareProfilingEnabled = hardwareProfilingEnable;
		this.idleProfilingTime = idleProfilingTime;
		this.autApkID = autApkID;
		this.autPackageID = autPackageID;
		this.testApkID = testApkID;
		this.testPackageID = testPackageID;
		this.noOfRuns = noOfRuns;
		this.testScript = testScript;
	}

	/**
	 * Returns the ID of the Apk file of the Application under test in the DB.
	 * 
	 * @return The ID of the Apk file of the Application under test in the DB.
	 */
	public int getAutApkID() {
		return autApkID;
	}

	/**
	 * Returns the package ID of the Application under test.
	 * 
	 * @return The package ID of the Application under test.
	 */
	public String getAutPackageID() {
		return autPackageID;
	}

	/**
	 * Returns the time in seconds to profile the DUT's idle power consumption.
	 * 
	 * @return The time in seconds to profile the DUT's idle power consumption.
	 */
	public int getIdleProfilingTime() {
		return idleProfilingTime;
	}

	/**
	 * Returns the number of test runs to be performed.
	 * 
	 * @return The number of test runs to be performed.
	 */
	public int getNumberOfTestRuns() {
		return noOfRuns;
	}

	/**
	 * Returns the ID of the Apk file of the test suite in the DB.
	 * 
	 * @return The ID of the Apk file of the test suite in the DB.
	 */
	public int getTestApkID() {
		return testApkID;
	}

	/**
	 * Returns the package ID of the test suite.
	 * 
	 * @return The package ID of the test suite.
	 */
	public String getTestPackageID() {
		return testPackageID;
	}

	/**
	 * Returns the ID of this {@link TestRun} in the DB.
	 * 
	 * @return The ID of this {@link TestRun} in the DB.
	 */
	public int getTestRunID() {
		return testRunID;
	}

	/**
	 * Returns the script of this {@link DbTestRun} in the DB.
	 * 
	 * @return The script of this {@link DbTestRun} in the DB.
	 */
	public String getTestScript() {
		return testScript;
	}

	/**
	 * Indicates whether or not hardware profiling shall be enabled.
	 * 
	 * @return If <code>true</code> hardware profiling is enabled.
	 */
	public boolean isHardwareProfilingEnabled() {
		return hardwareProfilingEnabled;
	}
}
