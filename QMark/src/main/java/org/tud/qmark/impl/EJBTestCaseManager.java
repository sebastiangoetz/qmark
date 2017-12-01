/*
 * Created on 24.10.2012
 */

package org.tud.qmark.impl;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.tud.qmark.entities.ResultCPU;
import org.tud.qmark.entities.ResultDisplay;
import org.tud.qmark.entities.ResultPowerRate;
import org.tud.qmark.entities.ResultWifi;
import org.tud.qmark.entities.TestCase;
import org.tud.qmark.entities.TestRun;
import org.tud.qmark.entities.TestSuite;
import org.tud.qmark.interfaces.ITestCaseManager;
import org.tud.qmark.interfaces.QMarkSessionBean;

/**
 * EJB manager implementation for {@link ITestCaseManager}.
 * 
 * @author Claas Wilke
 */
@Named("testCaseManager")
@Stateful
@RequestScoped
public class EJBTestCaseManager extends QMarkSessionBean implements ITestCaseManager
{

    /** The {@link EntityManager} used. */
    @Inject
    private EntityManager entityMgr;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.ITestSuiteManager#getTestRuns(org.tud.qmark.
     * entities.TestSuite)
     */
    @SuppressWarnings("unchecked")
    public List<TestRun> getTestRuns(TestSuite testSuite) throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getTestRuns", testSuite.getTestSuiteID().toString()))
        {
            List<TestRun> results = entityMgr.createQuery("select t from TestRun t where t.testSuite=:suite")
                    .setParameter("suite", testSuite).getResultList();
            cache.putObjectsforMethodandID("getTestRuns", testSuite.getTestSuiteID().toString(), results);
            return results;
        }
        else
        {
            return (List<TestRun>)cache.getObjectsforMethodandID("getTestRuns", testSuite.getTestSuiteID()
                    .toString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.ITestCaseManager#getCpuDataOfTestCaseAsJsData
     * (org.tud.qmark.entities.TestCase)
     */
    @SuppressWarnings("unchecked")
    public String getCpuDataOfTestCaseAsJsData(TestCase testCase) throws Exception
    {
        String key = testCase.getTestRunID().toString() + "+" + testCase.getStart().toString() + "+"
                + testCase.getStop().toString();
        if(!cache.existsObjectsforMethodandID("getCpuDataOfTestCaseAsJsData", key))
        {
            List<ResultCPU> cpus = (List<ResultCPU>)entityMgr
                    .createQuery(
                            "select c from ResultCPU c " + "where c.testRunID=:testRunID "
                                    + "and c.time>=:lowerBound "
                                    + "and c.time<=:upperBound order by c.number, c.time")
                    .setParameter("testRunID", testCase.getTestRunID())
                    .setParameter("lowerBound", testCase.getStart())
                    .setParameter("upperBound", testCase.getStop()).getResultList();

            StringBuffer result = new StringBuffer();

            int cpuNumber = 1;
            result.append("{name: 'CPU " + cpuNumber + "', color: '#c63030', data: [");

            for(ResultCPU cpu : cpus)
            {
                if(cpu.getNumber().intValue() != cpuNumber)
                {
                    cpuNumber = cpu.getNumber().intValue();
                    result.append("]}, {name: 'CPU " + cpuNumber + "', color: '#c63030', data: [");
                }
                // no else.
                result.append("[" + cpu.getTime() + ", " + cpu.getFrequence() / 1000 + "],");
            }
            // end for.

            result.append("]}");

            cache.putObjectsforMethodandID("getCpuDataOfTestCaseAsJsData", key, result.toString());

            return result.toString();
        }
        else
        {
            return (String)cache.getObjectsforMethodandID("getCpuDataOfTestCaseAsJsData", key);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.ITestCaseManager#getDisplayDataOfTestCaseAsJsData
     * (org.tud.qmark.entities.TestCase)
     */
    @SuppressWarnings("unchecked")
    public String getDisplayDataOfTestCaseAsJsData(TestCase testCase) throws Exception
    {
        String key = testCase.getTestRunID().toString() + "+" + testCase.getStart().toString() + "+"
                + testCase.getStop().toString();
        if(!cache.existsObjectsforMethodandID("getDisplayDataOfTestCaseAsJsData", key))
        {
            List<ResultDisplay> displays = (List<ResultDisplay>)entityMgr
                    .createQuery(
                            "select d from ResultDisplay d " + "where d.testRunID=:testRunID "
                                    + "and d.time>=:lowerBound " + "and d.time<=:upperBound order by d.time")
                    .setParameter("testRunID", testCase.getTestRunID())
                    .setParameter("lowerBound", testCase.getStart())
                    .setParameter("upperBound", testCase.getStop()).getResultList();

            StringBuffer result = new StringBuffer();

            for(ResultDisplay display : displays)
            {
                result.append("[" + display.getTime() + ", " + display.getBrightness() + "],");
            }
            // end for.
            cache.putObjectsforMethodandID("getDisplayDataOfTestCaseAsJsData", key, result.toString());

            return result.toString();
        }
        else
        {
            return (String)cache.getObjectsforMethodandID("getDisplayDataOfTestCaseAsJsData", key);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.ITestCaseManager#getPowerRatesOfTestCaseAsJsData
     * (org.tud.qmark.entities.TestCase)
     */
    @SuppressWarnings("unchecked")
    public String getPowerRatesOfTestCaseAsJsData(TestCase testCase) throws Exception
    {
        String key = testCase.getTestRunID().toString() + "+" + testCase.getStart().toString() + "+"
                + testCase.getStop().toString();
        if(!cache.existsObjectsforMethodandID("getPowerRatesOfTestCaseAsJsData", key))
        {
            List<ResultPowerRate> powerRates = (List<ResultPowerRate>)entityMgr
                    .createQuery(
                            "select p from ResultPowerRate p " + "where p.testRunID=:testRunID "
                                    + "and p.time>=:lowerBound " + "and p.time<=:upperBound order by p.time")
                    .setParameter("testRunID", testCase.getTestRunID())
                    .setParameter("lowerBound", testCase.getStart())
                    .setParameter("upperBound", testCase.getStop()).getResultList();
            StringBuffer result = new StringBuffer();

            for(ResultPowerRate powerRate : powerRates)
            {
                result.append("[" + powerRate.getTime() + ", " + powerRate.getPower() * (-1) + "],");
            }
            // end for.
            cache.putObjectsforMethodandID("getPowerRatesOfTestCaseAsJsData", key, result.toString());

            return result.toString();
        }
        else
        {
            return (String)cache.getObjectsforMethodandID("getPowerRatesOfTestCaseAsJsData", key);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.ITestCaseManager#getWifiDataOfTestCaseAsJsData
     * (org.tud.qmark.entities.TestCase)
     */
    @SuppressWarnings("unchecked")
    public String getWifiDataOfTestCaseAsJsData(TestCase testCase) throws Exception
    {
        String key = testCase.getTestRunID().toString() + "+" + testCase.getStart().toString() + "+"
                + testCase.getStop().toString();
        if(!cache.existsObjectsforMethodandID("getWifiDataOfTestCaseAsJsData", key))
        {
            List<ResultWifi> wifis = (List<ResultWifi>)entityMgr
                    .createQuery(
                            "select w from ResultWifi w " + "where w.testRunID=:testRunID "
                                    + "and w.time>=:lowerBound " + "and w.time<=:upperBound order by w.time")
                    .setParameter("testRunID", testCase.getTestRunID())
                    .setParameter("lowerBound", testCase.getStart())
                    .setParameter("upperBound", testCase.getStop()).getResultList();

            StringBuffer result = new StringBuffer();

            for(ResultWifi wifi : wifis)
            {
                result.append("[" + wifi.getTime() + ", " + wifi.getTraffic() + "],");
            }
            // end for.
            cache.putObjectsforMethodandID("getWifiDataOfTestCaseAsJsData", key, result.toString());
            return result.toString();
        }
        else
        {
            return (String)cache.getObjectsforMethodandID("getWifiDataOfTestCaseAsJsData", key);
        }
    }
    
    protected void resetCache()
    {
        cache.removeObjectforMethod("getWifiDataOfTestCaseAsJsData");
        cache.removeObjectforMethod("getPowerRatesOfTestCaseAsJsData");
        cache.removeObjectforMethod("getDisplayDataOfTestCaseAsJsData");
        cache.removeObjectforMethod("getCpuDataOfTestCaseAsJsData");
        cache.removeObjectforMethod("getTestRuns");
    }
}
