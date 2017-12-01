package org.tud.qmark.interfaces;

import java.util.List;

import org.tud.qmark.entities.EnergyModel;
import org.tud.qmark.entities.PowerRate;
import org.tud.qmark.entities.TestCase;
import org.tud.qmark.entities.TestRun;
import org.tud.qmark.entities.Type;
import org.tud.qmark.entities.Version;

/**
 * Manages {@link EnergyModel} entities and related properties.
 * 
 * @author Claas Wilke
 */
public interface IEnergyModelManager {

	/**
	 * Empties the {@link EnergyModel} of a given {@link Version} (i.e.,
	 * deleting all {@link PowerRate}s contained in the {@link EnergyModel}),
	 * and propagates the changes for the {@link PowerRate}s, the
	 * {@link EnergyModel} and the {@link Version} to the DB.
	 * 
	 * @param version
	 *            The {@link Version} whose {@link EnergyModel} shall be
	 *            emptied.
	 * @throws Exception
	 */
	public void clearEnergyModel(Version version) throws Exception;

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
	 * Returns the {@link Type} having the given name or <code>null</code> if no
	 * such type exists.
	 * 
	 * @param name
	 *            The name the {@link Type} should have.
	 * @return The {@link Type} having the given name or <code>null</code> if no
	 *         such type exists.
	 * @throws Exception
	 */
	public Type getType(String name) throws Exception;

	/**
	 * Updates a given {@link TestRun} in the DB.
	 * 
	 * @param testRun
	 *            The {@link TestRun} to be updated.
	 */
	public void updateTestRun(TestRun testRun) throws Exception;

	/**
	 * Updates a given {@link Version} in the DB.
	 * 
	 * @param version
	 *            The {@link Version} to be updated.
	 */
	public void updateVersion(Version version) throws Exception;
}
