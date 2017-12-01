/*
 * Created on 24.10.2012
 */

package org.tud.qmark.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.tud.qmark.entities.AvgTestCase;
import org.tud.qmark.entities.Binary;
import org.tud.qmark.entities.TestCase;
import org.tud.qmark.entities.TestRun;
import org.tud.qmark.entities.TestSuite;
import org.tud.qmark.entities.User;
import org.tud.qmark.interfaces.ITestSuiteManager;
import org.tud.qmark.interfaces.QMarkSessionBean;

/**
 * EJB manager implementation for {@link ITestSuiteManager}.
 * 
 * @author Claas Wilke
 */
@Named("testSuiteManager")
@Stateful
@RequestScoped
@Local(ITestSuiteManager.class)
public class EJBTestSuiteManager extends QMarkSessionBean implements
		ITestSuiteManager {

	/** The {@link EntityManager} used. */
	@Inject
	private EntityManager testSuiteDatabase;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.ITestSuiteManager#addTestRun(org.tud.qmark.entities
	 * .TestRun)
	 */
	public String addTestRun(TestRun testRun) throws Exception {
		testSuiteDatabase.persist(testRun);
		cache.removeObjectforID("getTestRuns");
		cache.removeObjectforID("getTestRun");
		cache.removeObjectforID("getTestRunsOfUser");
		return "testRunAdded";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.ITestSuiteManager#addTestSuite(org.tud.qmark
	 * .entities.TestSuite)
	 */
	public String addTestSuite(TestSuite testSuite) throws Exception {
		persistApk(testSuite);
		testSuiteDatabase.persist(testSuite);
		return "testSuiteAdded";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.ITestSuiteManager#deleteApkFromTestSuite(org
	 * .tud.qmark.entities.TestSuite)
	 */
	public void deleteApkFromTestSuite(TestSuite testSuite) {
		if (null != testSuite.getApk()) {
			Binary apk = (Binary) testSuiteDatabase
					.createQuery("select b from Binary b where b.binaryID=:id")
					.setParameter("id", testSuite.getApk().getBinaryID())
					.getSingleResult();
			testSuite.setApk(null);
			testSuiteDatabase.merge(testSuite);
			testSuiteDatabase.remove(apk);
			resetCache();
		}
		// no else.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.ITestSuiteManager#deleteTestRunFromTestSuite
	 * (org.tud.qmark.entities.TestRun)
	 */
	public void deleteTestRunFromTestSuite(TestRun testRun) {
		if (null != testRun) {

			testRun = (TestRun) testSuiteDatabase
					.createQuery(
							"select t from TestRun t where t.testRunID=:id")
					.setParameter("id", testRun.getTestRunID())
					.getSingleResult();

			/* Delete test run data. */
			testSuiteDatabase.createNativeQuery(
					"DELETE FROM ResultCPU WHERE testRunID = "
							+ testRun.getTestRunID()).executeUpdate();
			testSuiteDatabase.createNativeQuery(
					"DELETE FROM ResultDisplay WHERE testRunID = "
							+ testRun.getTestRunID()).executeUpdate();
			testSuiteDatabase.createNativeQuery(
					"DELETE FROM ResultPowerRate WHERE testRunID = "
							+ testRun.getTestRunID()).executeUpdate();
			testSuiteDatabase.createNativeQuery(
					"DELETE FROM ResultWifi WHERE testRunID = "
							+ testRun.getTestRunID()).executeUpdate();
			testSuiteDatabase.createNativeQuery(
					"DELETE FROM TestCase WHERE testRunID = "
							+ testRun.getTestRunID()).executeUpdate();

			TestSuite testSuite = testRun.getTestSuite();

			/* Remove test run from test suite. */
			testSuiteDatabase.createNativeQuery(
					"DELETE FROM TestRun WHERE testRunID = "
							+ testRun.getTestRunID()).executeUpdate();

			testSuiteDatabase.refresh(testSuite);
			resetCache();
		}
		// no else.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.ITestSuiteManager#getNumberOfTestRuns(org.tud
	 * .qmark.entities.TestSuite)
	 */
	@SuppressWarnings("unchecked")
	public int getNumberOfTestRuns(TestSuite testSuite) throws Exception {
		if (!cache.existsObjectsforMethodandID("getNumberOfTestRuns", testSuite
				.getTestSuiteID().toString())) {
			int result = -0;
			List<Long> results = testSuiteDatabase
					.createQuery(
							"select count(t) from TestRun t where t.testSuite=:suite")
					.setParameter("suite", testSuite).getResultList();

			if (results.size() > 0)
				result = results.get(0).intValue();
			cache.putObjectsforMethodandID("getNumberOfTestRuns", testSuite
					.getTestSuiteID().toString(), new Integer(result));
			return result;
		} else {
			return ((Integer) cache.getObjectsforMethodandID(
					"getNumberOfTestRuns", testSuite.getTestSuiteID()
							.toString())).intValue();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.ITestSuiteManager#getPowerRate(org.tud.qmark
	 * .entities.TestCase)
	 */
	public long getPowerRate(TestCase testCase) throws Exception {
		if (null == testCase)
			return -1;
		else if (null != testCase.getAvgPowerRate())
			return testCase.getAvgPowerRate();
		else {
			if (!cache.existsObjectsforMethodandID("getPowerRate", testCase
					.getTestCaseID().toString())) {
				long ret = -1l;
				Object result = testSuiteDatabase.createNativeQuery(
						"SELECT avgPowerRate FROM testcase WHERE testCaseID = "
								+ testCase.getTestCaseID() + ";")
						.getSingleResult();
				if (null != result)
					ret = new Double(Double.parseDouble((String) result)).longValue();
				// no else.
				
				cache.putObjectsforMethodandID("getPowerRate", testCase
						.getTestCaseID().toString(), new Long(ret));
				return ret;
			} else {
				return ((Long) cache.getObjectsforMethodandID("getPowerRate",
						testCase.getTestCaseID().toString())).longValue();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.ITestSuiteManager#getTestCases(org.tud.qmark
	 * .entities.TestRun)
	 */
	@SuppressWarnings("unchecked")
	public List<TestCase> getTestCases(TestRun testRun) throws Exception {
		if (!cache.existsObjectsforMethodandID("getTestCases", testRun
				.getTestRunID().toString())) {
			List<TestCase> results = testSuiteDatabase
					.createQuery(
							"select t from TestCase t where t.testRunID=:id order by t.name")
					.setParameter("id", testRun.getTestRunID()).getResultList();
			cache.putObjectsforMethodandID("getTestCases", testRun
					.getTestRunID().toString(), results);
			return results;
		} else {
			return ((List<TestCase>) cache.getObjectsforMethodandID(
					"getTestCases", testRun.getTestRunID().toString()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tud.qmark.interfaces.ITestSuiteManager#getTestRun(long)
	 */
	public TestRun getTestRun(long testRunID) throws Exception {
		if (!cache.existsObjectsforMethodandID("getTestRun",
				new Long(testRunID).toString())) {
			TestRun result = (TestRun) testSuiteDatabase
					.createQuery(
							"select t from TestRun t where t.testRunID=:id")
					.setParameter("id", testRunID).getSingleResult();
			cache.putObjectsforMethodandID("getTestRun",
					new Long(testRunID).toString(), result);
			return result;
		} else {
			return (TestRun) cache.getObjectsforMethodandID("getTestRun",
					new Long(testRunID).toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.ITestSuiteManager#getTestRuns(org.tud.qmark.
	 * entities.TestSuite)
	 */
	@SuppressWarnings("unchecked")
	public List<TestRun> getTestRuns(TestSuite testSuite) throws Exception {
		if (!cache.existsObjectsforMethodandID("getTestRuns", testSuite
				.getTestSuiteID().toString())) {
			List<TestRun> results = testSuiteDatabase
					.createQuery(
							"select t from TestRun t where t.testSuite=:suite")
					.setParameter("suite", testSuite).getResultList();
			cache.putObjectsforMethodandID("getTestRuns", testSuite
					.getTestSuiteID().toString(), results);
			return results;
		} else {
			return (List<TestRun>) cache.getObjectsforMethodandID(
					"getTestRuns", testSuite.getTestSuiteID().toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.ITestSuiteManager#getTestRuns(org.tud.qmark.
	 * entities.User)
	 */
	@SuppressWarnings("unchecked")
	public List<TestRun> getTestRunsOfUser(User user) throws Exception {
		if (!cache.existsObjectsforMethodandID("getTestRunsOfUser", user
				.getUserID().toString())) {
			List<TestRun> results = testSuiteDatabase
					.createQuery(
							"select tr from Member m, Version v, TestSuite ts, TestRun tr "
									+ "where m.user=:user and m.app = v.app and ts.version = v and tr.testSuite = ts")
					.setParameter("user", user).getResultList();
			cache.putObjectsforMethodandID("getTestRunsOfUser", user
					.getUserID().toString(), results);
			return results;
		} else {
			return (List<TestRun>) cache.getObjectsforMethodandID(
					"getTestRunsOfUser", user.getUserID().toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.ITestSuiteManager#updateTestRun(org.tud.qmark
	 * .entities.TestRun)
	 */
	public void updateTestRun(TestRun testRun) throws Exception {
		testSuiteDatabase.merge(testRun);
		resetCache();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.ITestSuiteManager#updateTestSuite(org.tud.qmark
	 * .entities.TestSuite)
	 */
	public void updateTestSuite(TestSuite testSuite) throws Exception {
		//persistApk(testSuite);
		testSuiteDatabase.merge(testSuite);
		resetCache();
	}

	/**
	 * Helper method storing the APK {@link Binary} of a given {@link TestSuite}
	 * in the DB if it is set.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite} whose {@link Binary} shall be persisted.
	 */
	private boolean persistApk(TestSuite testSuite) {
		if (null != testSuite.getApk()) {
			if (testSuiteDatabase.contains(testSuite.getApk()))
				testSuiteDatabase.merge(testSuite.getApk());
			else
				testSuiteDatabase.persist(testSuite.getApk());
			return true;
		}

		else {
			return false;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AvgTestCase> getAvgTestCasesOfTestRun(TestRun testRun) {
		if (!cache.existsObjectsforMethodandID("getAvgTestCasesOfTestRun",
				testRun.getTestRunID().toString())) {
			Map<String, AvgTestCase> resultMap;

			List<Object[]> avgResults = (List<Object[]>) testSuiteDatabase
					.createNativeQuery(
							"SELECT [name], result, COUNT(*), "
									+ "AVG(avgPowerRate) as Toll, AVG (stop - start) as Toller "
									+ "FROM TestCase " + "WHERE TestRunID = "
									+ testRun.getTestRunID() + " "
									+ "GROUP BY [name], result "
									+ "ORDER BY [name], result")
					.getResultList();
			resultMap = new HashMap<String, AvgTestCase>();

			for (Object[] aResult : avgResults) {
				String name = (String) aResult[0];
				AvgTestCase avgTestCase;

				if (resultMap.containsKey(name))
					avgTestCase = resultMap.get(name);
				else {
					avgTestCase = new AvgTestCase();
					avgTestCase.setName(name);
				}

				Integer testResult = (Integer) aResult[1];
				Integer count = (Integer) aResult[2];

				/* Success case */
				if (testResult == 1 && null != aResult[3]) {
					avgTestCase.setOkCases(count);

					BigInteger avgPowerRate = (BigInteger) aResult[3];
					BigInteger avgDuration = (BigInteger) aResult[4];

					avgTestCase.setAvgPowerRate(avgPowerRate.longValue());
					avgTestCase.setAvgDuration(avgDuration.longValue());
				}

				/* Failed case */
				else {
					avgTestCase.setFailedCases(count);
				}

				resultMap.put(name, avgTestCase);
			}
			// end for.

			List<AvgTestCase> result = new ArrayList<AvgTestCase>(
					resultMap.values());

			cache.putObjectsforMethodandID("getAvgTestCasesOfTestRun", testRun
					.getTestRunID().toString(), result);

			return result;
		} else {
			return (List<AvgTestCase>) cache.getObjectsforMethodandID(
					"getAvgTestCasesOfTestRun", testRun.getTestRunID()
							.toString());
		}
	}

	protected void resetCache() {
		cache.removeObjectforMethod("getNumberOfTestRuns");
		cache.removeObjectforMethod("getPowerRate");
		cache.removeObjectforMethod("getTestCases");
		cache.removeObjectforMethod("getTestRun");
		cache.removeObjectforMethod("getTestRuns");
		cache.removeObjectforMethod("getTestRuns");
		cache.removeObjectforMethod("getAvgTestCasesOfTestRun");
	}

	@Override
	public Map<Long, Float> getMeasurementResultsForTestRun(TestRun run)
			throws Exception {
		
			Map<Long, Float> ret = new HashMap<Long, Float>();

			@SuppressWarnings("unchecked")
			List<Object[]> res = (List<Object[]>) testSuiteDatabase
					.createNativeQuery(
							"SELECT power, time "
									+ "FROM ResultPowerRate " + "WHERE testRunID = "
									+ run.getTestRunID())
					.getResultList();

			for (Object[] aResult : res) {
				Long time = ((BigInteger) aResult[1]).longValue();
				Float power = (Float) aResult[0];
				ret.put(time, power);
			}
			
			return ret;
	}
}
