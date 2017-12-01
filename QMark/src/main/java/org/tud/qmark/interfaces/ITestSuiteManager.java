package org.tud.qmark.interfaces;

import java.util.List;
import java.util.Map;

import org.tud.qmark.entities.AvgTestCase;
import org.tud.qmark.entities.Binary;
import org.tud.qmark.entities.PowerRate;
import org.tud.qmark.entities.TestCase;
import org.tud.qmark.entities.TestRun;
import org.tud.qmark.entities.TestSuite;
import org.tud.qmark.entities.User;

/**
 * Manages {@link TestSuite} entities.
 * 
 * @author Claas Wilke
 */
public interface ITestSuiteManager {

	/**
	 * Adds a new {@link TestRun}.
	 * 
	 * @param testRun
	 *            The {@link TestRun} to be addded.
	 * @return A message indicating the result of the methods invocation.
	 * @throws Exception
	 */
	public String addTestRun(TestRun testRun) throws Exception;

	/**
	 * Adds a new {@link TestSuite}.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite} to be addded.
	 * @return A message indicating the result of the methods invocation.
	 * @throws Exception
	 */
	public String addTestSuite(TestSuite testSuite) throws Exception;

	/**
	 * Removes the {@link Binary} from a given {@link TestSuite} and deletes the
	 * {@link Binary} from the DB.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite}.
	 */
	public void deleteApkFromTestSuite(TestSuite testSuite);

	/**
	 * Removes a given {@link TestRun} from a its {@link TestSuite} and deletes
	 * the {@link TestRun} and the related {@link TestRun} data (e.g.,
	 * {@link PowerRate}s) from the DB.
	 * 
	 * @param testRun
	 *            The {@link TestRun}.
	 */
	public void deleteTestRunFromTestSuite(TestRun testRun);

	/**
	 * Returns the {@link AvgTestCase}s of a {@link TestRun}.
	 * 
	 * @param testRun
	 *            The {@link TestRun}.
	 * @return The {@link AvgTestCase}.
	 */
	public List<AvgTestCase> getAvgTestCasesOfTestRun(TestRun testRun);

	/**
	 * Tries to retrieve the number of {@link TestRun}s in a given
	 * {@link TestSuite}.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite}.
	 * @return The number as an int.
	 * @throws Exception
	 */
	public int getNumberOfTestRuns(TestSuite testSuite) throws Exception;

	/**
	 * Tries to compute the power rate for a given {@link TestCase}.
	 * 
	 * @param testCase
	 *            The {@link TestCase}
	 * @return The computed power rate in milli Watt.
	 * @throws Exception
	 */
	public long getPowerRate(TestCase testCase) throws Exception;

	/**
	 * Returns all {@link TestCase}s that belong to a given {@link TestRun}.
	 * 
	 * @param testRun
	 *            The {@link TestRun}.
	 * @return All {@link TestCases}s that belonging to the given
	 *         {@link TestRun}.
	 * @throws Exception
	 */
	public List<TestCase> getTestCases(TestRun testRun) throws Exception;

	/**
	 * Returns the {@link TestRun} that belong to a given ID.
	 * 
	 * @param testRunID
	 *            The ID of the {@link TestRun}.
	 * @return The corresponding {@link TestRun} or <code>null</code>.
	 * @throws Exception
	 */
	public TestRun getTestRun(long testRunID) throws Exception;

	/**
	 * Returns all {@link TestRun}s that belong to a given {@link TestSuite}.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite}.
	 * @return All {@link TestRun}s that belonging to the given
	 *         {@link TestSuite}.
	 * @throws Exception
	 */
	public List<TestRun> getTestRuns(TestSuite testSuite) throws Exception;
	
	public Map<Long, Float> getMeasurementResultsForTestRun(TestRun run) throws Exception;

	/**
	 * Returns all {@link TestRun}s that belong to a given {@link User}.
	 * 
	 * @param user
	 *            The {@User}.
	 * @return All {@link TestRun}s that belonging to the given {@link User}.
	 * @throws Exception
	 */
	public List<TestRun> getTestRunsOfUser(User user) throws Exception;

	/**
	 * Updates a given {@link TestRun} in the DB.
	 * 
	 * @param testRun
	 *            The {@link TestRun} to be updated.
	 */
	public void updateTestRun(TestRun testRun) throws Exception;

	/**
	 * Updates a given {@link TestSuite} in the DB.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite} to be updated.
	 */
	public void updateTestSuite(TestSuite testSuite) throws Exception;
}
