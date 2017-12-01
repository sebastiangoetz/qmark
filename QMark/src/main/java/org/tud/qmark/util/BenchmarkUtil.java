package org.tud.qmark.util;

import javax.persistence.Version;

import org.tud.qmark.entities.EnergyModel;
import org.tud.qmark.entities.TestSuite;

/**
 * Utility class for energy benchmarks.
 * 
 * @author Claas Wilke
 */
public interface BenchmarkUtil {

	/**
	 * The name of a {@link TestSuite} representing the benchmark of a
	 * {@link Version} to derive an {@link EnergyModel}.
	 */
	public final static String BENCHMARK_TESTSUITE_NAME = "QmarkBenchmark";

	public final static String BENCHMARK_TESTSCRIPT_APK_NAME_PLACEHOLDER = "$APK_NAME$";

	public final static String BENCHMARK_TESTSCRIPT_APPID_PLACEHOLDER = "$APP_ID$";

	public final static String BENCHMARK_TESTCRIPT_WALLPAPER_APP_SELECTION_PLACEHOLDER = "$APP_SELECT";

	public final static String BENCHMARK_TESTSCRIPT_WALLPAPER = "TestRun "
			+ BENCHMARK_TESTSUITE_NAME
			+ "\n"
			+ "applicationUnderTest : \""
			+ BENCHMARK_TESTSCRIPT_APK_NAME_PLACEHOLDER
			+ "\" "
			+ BENCHMARK_TESTSCRIPT_APPID_PLACEHOLDER
			+ "\n"
			+ "hardwareProfiling : on\n"
			+ "run : onServer\n"
			+ "eachTestWithFullBattery : on\n\n"
			+ "TestSuite BackgroundTests {\n\n"
			+ "	SetUp {\n"
			+ "		DISPLAY ON\n"
			+ "		UNLOCK\n"
			+ "		START ACTIVITY \"com.android.wallpaper.livepicker\" \"LiveWallpaperActivity\"\n"
			+ BENCHMARK_TESTCRIPT_WALLPAPER_APP_SELECTION_PLACEHOLDER
			+ "		ENTER\n" + "		CURSOR right\n" + "		CURSOR right\n"
			+ "		ENTER\n" + "		HOME BUTTON\n" + "		DISPLAY OFF\n" + "	}\n\n"
			+ "    TestCase DeviceLocked {\n" + "        WAIT FOR 300\n"
			+ "    }\n\n" + "    TestCase TestInLockScreen {\n"
			+ "        DISPLAY ON\n\n" + "        Test LockScreen\n"
			+ "        WAIT FOR 300\n\n" + "        Test SwitchOffLockScreen\n"
			+ "        DISPLAY OFF\n" + "    }\n\n"
			+ "    TestCase TestOnDesktop {\n" + "        Test DisplayOn\n"
			+ "        DISPLAY ON\n" + "        WAIT FOR 5\n"
			+ "        UNLOCK\n" + "        WAIT FOR 5\n\n"
			+ "        Test Desktop\n" + "        WAIT FOR 300\n\n"
			+ "        Test SwitchOffDesktop\n" + "        DISPLAY OFF\n"
			+ "    }\n\n" + "    TestCase TestOtherApp {\n" + "\n"
			+ "        Test DisplayOn\n" + "        DISPLAY ON\n"
			+ "        WAIT FOR 5\n" + "        UNLOCK\n"
			+ "        WAIT FOR 5\n" + "        OPEN SETTINGS\n"
			+ "        WAIT FOR 5\n\n" + "        Test OtherAppActive\n"
			+ "        WAIT FOR 300\n\n" + "        Test SwitchOffSettings\n"
			+ "        HOME BUTTON\n" + "        DISPLAY OFF\n" + "	}\n" + "}";

	public final static String BENCHMARK_TESTCRIPT_WALLPAPER_APP_SELECTION_STATEMENT = "		CURSOR down\n";
}
