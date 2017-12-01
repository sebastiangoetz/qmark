package org.tud.qmark.jouleunit.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.qualitune.jouleunit.ProfilingException;
import org.qualitune.jouleunit.android.testrun.ApkFile;
import org.qualitune.jouleunit.android.testrun.TestRun;
import org.qualitune.jouleunit.android.testrun.TestrunFactory;
import org.qualitune.jouleunit.android.testrun.TestrunPackage;
import org.qualitune.jouleunit.android.testrun.resource.testrun.mopp.TestrunResourceFactory;
import org.qualitune.jouleunit.android.testrun.resource.testrun.run.TestrunInterpreter;
import org.tud.qmark.jouleunit.service.db.DbManager;
import org.tud.qmark.jouleunit.service.db.DbManager.TestRunType;
import org.tud.qmark.jouleunit.service.db.DbTestRun;

/**
 * Service class to read JouleUnit test run configurations from the DB and to
 * execute them on a real Android device.
 * 
 * @author Claas Wilke
 */
public class Main {

	/** The port of the {@link Main} service. */
	private static final int SERVICE_PORT = 5724;

	/** A {@link ConsoleStream} to document the test run progress. */
	private ConsoleStream out;

	private DbManager dbMgr;

	private DbTestRun testRun;

	/**
	 * Main method of the {@link Main} service.
	 */
	public static void main(String[] args) throws Exception {

		Main main = new Main();
		main.run(args);
		// main.registerEmfResources();
		// main.out = new ConsoleStream(System.out);
		// main.runTests(2);
	}

	/**
	 * Helper method to register EMF resources.
	 */
	private void registerEmfResources() {
		/* Probably register the test run model resource for EMF. */
		if (Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().get(
				TestrunPackage.eNS_PREFIX) == null) {

			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
					TestrunPackage.eNS_PREFIX, new TestrunResourceFactory());
		}
		// no else.

		if (EPackage.Registry.INSTANCE.getEPackage(TestrunPackage.eNS_URI) == null) {
			EPackage.Registry.INSTANCE.put(TestrunPackage.eNS_PREFIX,
					TestrunPackage.eINSTANCE);
		}
		// no else.
	}

	/**
	 * Run method triggered by the main method.
	 * 
	 * @param args
	 *            Args provided by the main method.
	 * @throws IOException
	 */
	private void run(String[] args) throws IOException {
		registerEmfResources();

		/* Decide, which port to use. */
		int port;

		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			}

			catch (NumberFormatException e) {
				port = SERVICE_PORT;
				System.out
						.println("Illegal argument. First argument should be the port used for this service.");
				System.out.println("Use default port " + SERVICE_PORT
						+ " instead.");
			}
		}

		else {
			port = SERVICE_PORT;
			System.out
					.println("Use default port " + SERVICE_PORT + " instead.");
		}

		/* Open socket and wait for clients. */
		while (true) {
			ServerSocket serverSocket = null;

			try {
				serverSocket = new ServerSocket(port);
			}

			catch (IOException e) {
				System.err.println("Could not listen on port: " + port);
				System.exit(-1);
			}

			Socket clientSocket = null;

			try {
				System.out.println("Wait to accept client ...");
				clientSocket = serverSocket.accept();
			}

			catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(-1);
			}

			out = new ConsoleStream(clientSocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			String inputLine;

			out.println("Ready");

			while ((inputLine = in.readLine()) != null) {

				try {
					int testRunID = Integer.parseInt(inputLine.trim());

					String msg = "Start test run for TestRunID " + testRunID
							+ " ...";
					System.out.println(msg);
					out.println(msg);

					runTests(testRunID);

					out.println("Done");
					break;
				}

				catch (NumberFormatException e) {
					out.println("Expected TestRunID but was: " + inputLine);
					out.println("Expected TestRunID but was: " + inputLine);
					break;
				}
			}
			// end while.

			out.close();
			in.close();
			clientSocket.close();
			serverSocket.close();

			System.out.println("Done\n\n");
		}
		// end while (program termination).
	}

	/**
	 * Helper method to run the {@link DbTestRun} identified by the given ID of
	 * the {@link DbTestRun} in the DB.
	 * 
	 * @param testRunId
	 *            The ID of the {@link DbTestRun}.
	 */
	private void runTests(int testRunId) {
		dbMgr = new DbManager(out);
		testRun = dbMgr.loadTestRunfromDB(testRunId);

		/* Download the APKs. */
		String apkUnderTest = "aut.apk";
		String testApk = "test.apk";

		dbMgr.loadAutFromDB(testRun.getAutApkID(), apkUnderTest);
		dbMgr.loadTestApkFromDB(testRun.getTestApkID(), testApk);

		File autFile = new File(apkUnderTest);
		File testApkFile = new File(testApk);

		if (null != testRun.getTestScript()
				&& testRun.getTestScript().length() > 0) {
			String testScript = "test.testrun";
			PrintWriter writer;
			try {
				writer = new PrintWriter(testScript);
				writer.write(testRun.getTestScript());
				writer.close();
			} catch (FileNotFoundException e) {
				out.print("Error export of test script failed: "
						+ e.getMessage());
				return;
			}

			ResourceSet rs = new ResourceSetImpl();
			File scriptFile = new File(testScript);
			URI fileURI = URI.createFileURI(scriptFile.getAbsolutePath());
			Resource resource = rs.createResource(fileURI);
			try {
				resource.load(null);
			} catch (IOException e) {
				out.print("Error during load of test script: " + e.getMessage());
				return;
			}

			TestRun run = (TestRun) resource.getAllContents().next();
			ApkFile autApkFile = run.getAut();
			if (null == autApkFile && null != autFile)
				autApkFile = TestrunFactory.eINSTANCE.createApkFile();
			// else.

			if (null != autApkFile && null != autFile && autFile.exists()) {
				autApkFile.setPath(autFile.getAbsolutePath());
				run.setAut(autApkFile);
			}
			// no else.

			ApkFile juniutApkFile = run.getJunitApk();
			if (null == juniutApkFile && null != testApkFile && testApkFile.exists())
				juniutApkFile = TestrunFactory.eINSTANCE.createApkFile();

			// else.

			if (null != autApkFile && testApkFile.exists()) {
				juniutApkFile.setPath(testApkFile.getAbsolutePath());
				run.setJunitApk(juniutApkFile);
			}
			// no else.

			run.setRemoteRun(false);

			/* Trigger test run. */
			TestrunInterpreter interpreter = new QmarkTestrunInterpreter(out);
			interpreter.addObjectToInterprete(resource.getContents().get(0));
			interpreter.interprete(true);

			scriptFile.delete();
		}

		else {
			out.print("Error test run without test script not configured yet.");
		}

		/* Delete the APK files from the file system. */
		if (autFile.exists())
			autFile.delete();
		// no else.

		if (testApkFile.exists())
			testApkFile.delete();
		// no else.
	}

	private class QmarkTestrunInterpreter extends TestrunInterpreter {

		/**
		 * Creates a new {@link QmarkTestrunInterpreter}.
		 * 
		 * @param console
		 *            The {@link PrintStream} to propagate progress and errors
		 *            during interpretation.
		 */
		public QmarkTestrunInterpreter(PrintStream console) {
			super(console);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.qualitune.jouleunit.android.testrun.resource.testrun.run.
		 * TestrunInterpreter
		 * #createCoordinator(org.qualitune.jouleunit.android.testrun.TestRun)
		 */
		@Override
		protected Coordinator createCoordinator(TestRun testRun) {
			return new QMarkCoordinator(testRun);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.qualitune.jouleunit.android.testrun.resource.testrun.run.
		 * TestrunInterpreter#printError(java.lang.String)
		 */
		@Override
		protected void printError(String msg) {
			console.println(new Date().toString() + " - Error: "
					+ super.consoleInline + msg);

			dbMgr.setTestRunFinishedInDB(Main.this.out.getPrintedInformation(),
					Main.this.testRun, TestRunType.FAILED);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.qualitune.jouleunit.android.testrun.resource.testrun.run.
		 * TestrunInterpreter#printProgress(java.lang.String)
		 */
		@Override
		protected void printProgress(String msg) {
			console.println(new Date().toString() + " - " + super.consoleInline
					+ msg);
		}

		/**
		 * Coordinator overwritten to adapt to QMark server side situation.
		 * 
		 * @author Claas Wilke
		 */
		protected class QMarkCoordinator extends Coordinator {

			/**
			 * Creates a new {@link QMarkCoordinator}
			 * 
			 * @param testRun
			 *            The {@link TestRun} to be coordinated.
			 */
			public QMarkCoordinator(TestRun testRun) {
				super(testRun);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.qualitune.jouleunit.android.testrun.resource.testrun.run.
			 * TestrunInterpreter#getHardwareServiceFile()
			 */
			@Override
			protected File getHardwareServiceFile() {
				return new File("org.qualitune.jouleunit.android.hwservice.apk");
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.qualitune.jouleunit.android.testrun.resource.testrun.run.
			 * TestrunInterpreter.Coordinator#propagateResults()
			 */
			@Override
			protected void propagateResults() throws ProfilingException {
				reportProgress("Report test data to DB ...");
				dbMgr.setTestRunFinishedInDB(
						Main.this.out.getPrintedInformation(),
						Main.this.testRun, TestRunType.FINISHED);
				reportProgress("Done.");
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.qualitune.jouleunit.coordinator.JouleUnitCoordinator#
			 * stopHardwareProfiling()
			 */
			protected void stopHardwareProfiling() throws ProfilingException {
				String command = "am startservice -a 'org.qualitune.jouleunit.android.HWServiceStopService'";

				try {
					executeAdbCommand(command);
				} catch (ProfilingException e) {
					reportError(e);
				}

				reportProgress("Hardware probe service stopped.");

				/* Do not uninstall the hardware service. */
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.qualitune.jouleunit.android.ConsoleAndroidJouleUnitCoordinator
			 * # runTestCases()
			 */
			@Override
			protected void runTestCases() throws ProfilingException {
				super.runTestCases();

				/* Wait to avoid that log cat messages get lost during testing. */
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					/* Not that important. */
				}

				/*
				 * Export already profiled test cases to avoid out of memory
				 * problems.
				 */
				profiler.endProfiling();
				reportProgress("Report test run to DB ...");
				dbMgr.saveTestRunToDB(testSuiteProfile, Main.this.testRun);
				testSuiteProfile.clear();
				testSuiteProfile.setEnergyProfile(profiler.startProfiling());
				reportProgress("... done.");
			}
		}
	}
}
