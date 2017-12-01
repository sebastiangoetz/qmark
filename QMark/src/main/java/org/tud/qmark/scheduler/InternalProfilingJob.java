package org.tud.qmark.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.quartz.Job;
import org.tud.qmark.entities.EnergyModel;
import org.tud.qmark.entities.PowerRate;
import org.tud.qmark.entities.TestCase;
import org.tud.qmark.entities.TestRun;
import org.tud.qmark.entities.Transition;
import org.tud.qmark.interfaces.ICacheManager;
import org.tud.qmark.interfaces.IEnergyModelManager;
import org.tud.qmark.interfaces.ITestSuiteManager;
import org.tud.qmark.util.BenchmarkUtil;
import org.tud.qmark.util.TypeConstants;

/**
 * {@link Job} implementation to trigger a profiling run on the profiling
 * server.
 * 
 * @author Claas Wilke
 * @author maquiz
 */
public class InternalProfilingJob
{
    private StringBuffer console = new StringBuffer();
    private TestRun testRun;
    private IEnergyModelManager energyModelManager;
    private ICacheManager cacheManager;
    private Long testRunID = 0l;
    private String name = "Nameless Job";
    private int category = 0;
    //the higher the int value, the higher is the priority   
    private int priority = 0;

    public InternalProfilingJob(Long testRunID, String name, int category, int priority)
    {
        this.testRunID = testRunID;
        this.name = name;
        this.category = category;
        this.priority = priority;
    }

    public void prepare(Long testRunID, String name, int category, int priority)
    {
        this.testRunID = testRunID;
        this.name = name;
        this.category = category;
        this.priority = priority;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute()
    {
        ITestSuiteManager testSuiteManager = null;
        try
        {
            Context namingContext = new InitialContext();
            testSuiteManager = (ITestSuiteManager)namingContext
                    .lookup("java:global/QMark/EJBTestSuiteManager!org.tud.qmark.interfaces.ITestSuiteManager");
            energyModelManager = (IEnergyModelManager)namingContext
                    .lookup("java:global/QMark/EJBEnergyModelManager!org.tud.qmark.interfaces.IEnergyModelManager");
            cacheManager = (ICacheManager)namingContext
                    .lookup("java:global/QMark/EJBCacheManager!org.tud.qmark.interfaces.ICacheManager");
            testRun = energyModelManager.getTestRun(testRunID);

            createEmptyEnergyModel(testRunID);

            // Set test run as running.
            testRun.setType(energyModelManager.getType(TypeConstants.TESTRUN_TYPE_RUNNING));
            energyModelManager.updateTestRun(testRun);

            // Trigger test case execution. 
            // TODO Extract server settings. 
            //			String serverIP = "127.0.0.1";
            //String serverIP = "141.76.65.246";
            String serverIP = "141.76.65.137";
            int serverPort = 5724;

            Socket kkSocket = null;
            PrintWriter out = null;
            BufferedReader in = null;

            printProgress("Try to connect to profiling server...");

            try
            {
                kkSocket = new Socket(serverIP, serverPort);
                out = new PrintWriter(kkSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
            }

            catch(UnknownHostException e)
            {
                printError("Cannot connect to profiling server. TestRun cancelled."); 
                testRun.setType(energyModelManager.getType(TypeConstants.TESTRUN_TYPE_FAILED));
                return;
            }

            catch(IOException e)
            {
                printError("Couldn't get I/O for the connection to profiling server. Are you sure that your QMark settings are correct? TestRun cancelled.");
                testRun.setType(energyModelManager.getType(TypeConstants.TESTRUN_TYPE_FAILED));
                return;
            }

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;

            try
            {
                while((fromServer = in.readLine()) != null)
                {
                    printProgress(fromServer);

                    if(fromServer.equals("Ready"))
                    {

                        // Start test run. 
                        out.println(testRunID);
                    }

                    else if(fromServer.equals("Done"))
                    {
                    	testRun.setType(energyModelManager.getType(TypeConstants.TESTRUN_TYPE_FINISHED));
                        break;
                    }
                }
                in.close();
                out.close();
                stdIn.close();
                kkSocket.close();
            }

            catch(IOException e)
            {
                printError("Connection to profiling server failed.\nCaused Exception: " + e.getMessage()
                        + ". TestRun cancelled."); 
                testRun.setType(energyModelManager.getType(TypeConstants.TESTRUN_TYPE_FAILED));
                return;
            }

            // Update the test run.
            if(null != energyModelManager)
            {
                try
                {
                    energyModelManager.updateTestRun(testRun);
                    if(cacheManager != null) {
                    		cacheManager.removeObjectforID("getTestRun");
                    		cacheManager.removeObjectforID("getTestRuns");
                    		cacheManager.removeObjectforID("getTestRunsForUser");
                    }
//                  testRun = energyModelManager.getTestRun(testRunID);
//
//                    if(testRun.getType().isOfType(TypeConstants.TESTRUN_TYPE_RUNNING))
//                    {
//                        testRun.setType(energyModelManager.getType(TypeConstants.TESTRUN_TYPE_FINISHED));
//                        
//                    }
//                    // no else.
                }  catch(Exception e)  {
                	e.printStackTrace();
                }
            }
            // no else.

            // Probably extract energy model. 
            if(null != testRun
                    && testRun.getTestSuite().getName().equals(BenchmarkUtil.BENCHMARK_TESTSUITE_NAME))
            {

                EnergyModel energyModel = testRun.getTestSuite().getVersion().getEnergyModel();

                if(null == energyModel)
                {
                    energyModel = new EnergyModel();
                }
                // no else.

                energyModel.setDevice(testRun.getDevice());
                energyModel.setProfilingDate(new Date());
                energyModel.setUseCaseModel(testRun.getTestSuite().getVersion().getApp().getGenre()
                        .getUseCaseModel());
                testRun.getTestSuite().getVersion().setEnergyModel(energyModel);

                // derive power rates.
                energyModelManager.clearEnergyModel(testRun.getTestSuite().getVersion());
                testRun = energyModelManager.getTestRun(testRunID);
                energyModel = testRun.getTestSuite().getVersion().getEnergyModel();
                Set<PowerRate> powerRates = energyModel.getPowerRates();
                List<TestCase> testCases;

                try
                {
                    testCases = energyModelManager.getTestCases(testRun);

                    Map<String, String> nameMappings = new HashMap<String, String>();
                    nameMappings.put("Desktop Time", "Desktop");
                    nameMappings.put("Application Usage", "OtherAppActive");
                    nameMappings.put("Locked", "DeviceLocked");
                    nameMappings.put("Lock Screen", "LockScreen");

                    for(Transition transition : energyModel.getUseCaseModel().getTransitions())
                    {

                        String testName = nameMappings.get(transition.getName());

                        for(TestCase testCase : testCases)
                        {
                            if(testName.equals(testCase.getName()))
                            {
                                PowerRate rate = new PowerRate();
                                rate.setTransition(transition);
                                rate.setValue((double)testSuiteManager.getPowerRate(testCase));
                                powerRates.add(rate);
                            }
                            // no else.
                        }
                    }
                    // end for.

                    energyModel.setPowerRates(powerRates);
                    testRun.getTestSuite().getVersion().setEnergyModel(energyModel);

                    // persist energyModel and app version.
                    energyModelManager.updateVersion(testRun.getTestSuite().getVersion());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void createEmptyEnergyModel(Long testRunID) throws Exception
    {
        /*
         * Probably create empty energy model (app already appears in store).
         */
        if(null != testRun && testRun.getTestSuite().getName().equals(BenchmarkUtil.BENCHMARK_TESTSUITE_NAME))
        {

            EnergyModel energyModel = testRun.getTestSuite().getVersion().getEnergyModel();
            if(null == energyModel)
            {
                energyModel = new EnergyModel();
            }
            // no else.

            energyModel.setDevice(testRun.getDevice());
            energyModel.setProfilingDate(new Date());
            energyModel.setUseCaseModel(testRun.getTestSuite().getVersion().getApp().getGenre()
                    .getUseCaseModel());
            testRun.getTestSuite().getVersion().setEnergyModel(energyModel);

            energyModelManager.clearEnergyModel(testRun.getTestSuite().getVersion());
            testRun = energyModelManager.getTestRun(testRunID);
            energyModel = testRun.getTestSuite().getVersion().getEnergyModel();
            Set<PowerRate> powerRates = energyModel.getPowerRates();

            for(Transition transition : energyModel.getUseCaseModel().getTransitions())
            {

                PowerRate rate = new PowerRate();
                rate.setTransition(transition);
                rate.setValue(null);
                powerRates.add(rate);
            }
            // end for.

            // persist energyModel and app version.
            energyModel.setPowerRates(powerRates);
            energyModelManager.updateVersion(testRun.getTestSuite().getVersion());
        }
        // no else.
    }

    protected void printError(String msg)
    {
        console.append("Error: " + msg + "\n");

        testRun.setConsole(console.toString());

        if(null != energyModelManager)
        {
            try
            {
                /* Set test run as running. */
                testRun.setType(energyModelManager.getType(TypeConstants.TESTRUN_TYPE_FAILED));
                energyModelManager.updateTestRun(testRun);

                energyModelManager.updateTestRun(testRun);
            }
            catch(Exception e)
            {
                /* Can't do anything. Console info is simply not propagated. */
            }
        }
        // no else.
    }

    /**
     * Helper method to print a progress message on the console.
     * 
     * @param msg
     *            The message to be printed.
     */
    protected void printProgress(String msg)
    {
        console.append(msg + "\n");

        testRun.setConsole(console.toString());

        if(null != energyModelManager)
        {
            try
            {
                energyModelManager.updateTestRun(testRun);
            }
            catch(Exception e)
            {
            }
        }
    }

    public String getName()
    {
        return name;
    }

    public int getCategory()
    {
        return category;
    }

    public int getPriority()
    {
        return priority;
    }
    
    public Long getTestRunID()
    {
        return testRunID;
    }
    
    public String getID()
    {
        return getTestRunID().toString() + "+" + getName() + "+" + priority;
    }
}