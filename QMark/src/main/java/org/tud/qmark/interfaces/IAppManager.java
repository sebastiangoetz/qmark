package org.tud.qmark.interfaces;

import java.util.List;
import java.util.Map;

import org.tud.qmark.entities.App;
import org.tud.qmark.entities.EnergyModel;
import org.tud.qmark.entities.Genre;
import org.tud.qmark.entities.Member;
import org.tud.qmark.entities.Transition;
import org.tud.qmark.util.AppOrder;

/**
 * Manages {@link App} entities.
 * 
 * @author Claas Wilke
 */
public interface IAppManager {

	/**
	 * Adds a new {@link App}.
	 * 
	 * @param app
	 *            The {@link App} to be addded.
	 * @return A message indicating the result of the methods invocation.
	 * @throws Exception
	 */
	public String addApp(App app) throws Exception;

	/**
	 * Returns all existing {@link Apps}s.
	 * 
	 * @return All existing {@link Apps}s.
	 * @throws Exception
	 */
	public List<App> getApps() throws Exception;

	/**
	 * Tries to retrieve the {@link App}s of a given {@link Genre} and given
	 * energy weights. Further an {@link AppOrder}, the number of the first
	 * {@link App} in this order to be returned and the maximum number of
	 * {@link App}s to be returned can be defined.
	 * 
	 * @param genre
	 *            The {@link Genre} whose most efficient {@link App} shall be
	 *            returned.
	 * @param weights
	 *            A {@link Map} containing the {@link Transition}s of the
	 *            {@link Genre}'s {@link EnergyModel} as keys and weights how
	 *            they should be considered in the average energy consumption as
	 *            values.
	 * @param order
	 *            The {@link AppOrder} indicating how to sort the result.
	 * @param start
	 *            Indicates which {@link App} of the result should be the first
	 *            {@link App} in the given {@link AppOrder} being returned.
	 * @param limit
	 *            Indicates the maximum number of {@link App}s being returned.
	 * @return A {@link List} of {@link App}s according to the given
	 *         {@link AppOrder} and bounds. <strong>Can be empty!</strong>
	 */
	public List<App> getAppsOfGenreByConsumption(Genre genre,
			Map<Transition, Integer> weights, AppOrder order, int start,
			int limit) throws Exception;

	/**
	 * Computes the energy rank of a given {@link App} for a given set of energy
	 * weights.
	 * 
	 * @param app
	 *            The {@lA {@link Map} containing the {@link Transition}s of
	 *            the {@link Genre}'s {@link EnergyModel} as keys and weights
	 *            how they should be considered in the average energy
	 *            consumption as values.ink App} whose power rate shall be
	 *            computed.
	 * @param genre
	 *            The {@link Genre} for which the {@link App}'s rank shall be
	 *            computed.
	 * @param weights
	 *            A {@link Map} containing the {@link Transition}s of the
	 *            {@link Genre}'s {@link EnergyModel} as keys and weights how
	 *            they should be considered in the average energy consumption as
	 *            values.
	 * @return The energy rank of the {@link App}.
	 */
	public int getEnergyRankOfApp(App app, Genre genre,
			Map<Transition, Integer> weights) throws Exception;

	/**
	 * Returns all {@link Member}s of a given {@link App}.
	 * 
	 * @param app
	 *            The {@link App}.
	 * @return A {@link List} of the {@link App}'s {@link Member}s.
	 * @throws Exception
	 */
	public List<Member> getMembers(App app) throws Exception;

	/**
	 * Tries to retrieve the number of {@link App}s in a given {@link Genre}.
	 * 
	 * @param genre
	 *            The {@link Genre}.
	 * @return The number as an int.
	 * @throws Exception
	 */
	public int getNumberOfAppsInGenre(Genre genre) throws Exception;

	/**
	 * Computes the power rate of a given {@link App} for a given set of energy
	 * weights.
	 * 
	 * @param app
	 *            The {@lA {@link Map} containing the {@link Transition}s of
	 *            the {@link Genre}'s {@link EnergyModel} as keys and weights
	 *            how they should be considered in the average energy
	 *            consumption as values.ink App} whose power rate shall be
	 *            computed.
	 * @param weights
	 *            A {@link Map} containing the {@link Transition}s of the
	 *            {@link Genre}'s {@link EnergyModel} as keys and weights how
	 *            they should be considered in the average energy consumption as
	 *            values.
	 * @return The computed power rate of the {@link App}.
	 */
	public double getPowerRateOfApp(App app, Map<Transition, Integer> weights)
			throws Exception;

	/**
	 * Updates a given {@link App} in the DB.
	 * 
	 * @param app
	 *            The {@link App} to be updated.
	 */
	public void updateApp(App app) throws Exception;
}
