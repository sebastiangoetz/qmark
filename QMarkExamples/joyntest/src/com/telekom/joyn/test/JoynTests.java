package com.telekom.joyn.test;

import org.qualitune.jouleunit.android.utils.AndroidUtility;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;

/**
 * Contains test cases to profile the
 * {@link com.telekom.joyn.JoynSplashActivity} activity w.r.t. energy
 * consumption.
 * 
 * @author JouleUnit for Android
 */
@SuppressWarnings("rawtypes")
public class JoynTests extends ActivityInstrumentationTestCase2 {

	/** Tag for logging during testing. */
	public static final String LOG_TAG = "TestRunner";

	/** Launcher activity class. */
	private static Class<?> launcherActivityClass;

	/** Static code to find the activity class. */
	static {
		try {
			launcherActivityClass = Class
					.forName("com.telekom.joyn.JoynSplashActivity");
		}

		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/** {@link Solo} object for testing. */
	private Solo solo;

	/** Constructor. */
	@SuppressWarnings("unchecked")
	public JoynTests() {
		super("com.telekom.joyn", launcherActivityClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	@Override
	public void setUp() {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	/**
	 * Simple test case sending a Joyn message.
	 * 
	 * @throws InterruptedException
	 */
	public void testSendMessage() throws InterruptedException {
		Log.i(LOG_TAG, "started: startJoyn");
		solo.waitForActivity("JoynSplashActivity", 10000);
		solo.waitForActivity("ContactHistoryActivity", 10000);
		solo.sleep(500);
		Log.i(LOG_TAG, "finished: startJoyn");
		
		Log.i(LOG_TAG, "started: selectPartner");
		solo.clickOnScreen(550, 95);
		solo.sleep(500);
		solo.clickOnText("Claas");
		solo.waitForActivity("ChatOne2OneActivity", 100000);
		Log.i(LOG_TAG, "finished: selectPartner");

		Log.i(LOG_TAG, "started: enterMessage");
		AndroidUtility.enterTextCharByChar(solo, 0, "This is a test");
		Log.i(LOG_TAG, "finished: enterMessage");
	
		Log.i(LOG_TAG, "started: sendMessage");
		solo.clickOnButton(3);
		solo.sleep(10000);
		Log.i(LOG_TAG, "finished: sendMessage");	
	}
}
