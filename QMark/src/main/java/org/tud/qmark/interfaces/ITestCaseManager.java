package org.tud.qmark.interfaces;

import org.tud.qmark.entities.PowerRate;
import org.tud.qmark.entities.ResultCPU;
import org.tud.qmark.entities.ResultDisplay;
import org.tud.qmark.entities.ResultPowerRate;
import org.tud.qmark.entities.ResultWifi;
import org.tud.qmark.entities.TestCase;
import org.tud.qmark.entities.TestSuite;

/**
 * Manages {@link TestSuite} entities.
 * 
 * @author Claas Wilke
 */
public interface ITestCaseManager {

	/**
	 * Returns all {@link ResultCPU} values profiled while executing a
	 * {@link TestCase} formatted as JavaScript data in one {@link String}.
	 * 
	 * @param testCase
	 *            The {@link TestCase}.
	 * @return The {@link ResultCPU} values as a String.
	 * @throws Exception
	 */
	public String getCpuDataOfTestCaseAsJsData(TestCase testCase)
			throws Exception;

	/**
	 * Returns all {@link ResultDisplay} values profiled while executing a
	 * {@link TestCase} formatted as JavaScript data in one {@link String}.
	 * 
	 * @param testCase
	 *            The {@link TestCase}.
	 * @return The {@link ResultDisplay} values as a String.
	 * @throws Exception
	 */
	public String getDisplayDataOfTestCaseAsJsData(TestCase testCase)
			throws Exception;

	/**
	 * Returns all {@link ResultPowerRate} values profiled while executing a
	 * {@link TestCase} formatted as JavaScript data in one {@link String}.
	 * 
	 * @param testCase
	 *            The {@link TestCase}.
	 * @return The {@link PowerRate} values as a String.
	 * @throws Exception
	 */
	public String getPowerRatesOfTestCaseAsJsData(TestCase testCase)
			throws Exception;

	/**
	 * Returns all {@link ResultWifi} values profiled while executing a
	 * {@link TestCase} formatted as JavaScript data in one {@link String}.
	 * 
	 * @param testCase
	 *            The {@link TestCase}.
	 * @return The {@link ResultWifi} values as a String.
	 * @throws Exception
	 */
	public String getWifiDataOfTestCaseAsJsData(TestCase testCase)
			throws Exception;
}
