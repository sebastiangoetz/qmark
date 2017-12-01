package org.tud.qmark.sessionmgmt;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.tud.qmark.entities.App;
import org.tud.qmark.entities.Genre;
import org.tud.qmark.entities.Transition;
import org.tud.qmark.entities.UseCaseModel;
import org.tud.qmark.entities.Version;
import org.tud.qmark.interfaces.IAppManager;
import org.tud.qmark.util.AppOrder;

/**
 * Represents the state of a user sessions. Contains some variables as the
 * currently selected {@link Genre} and provides some functionality as to
 * compute the top runner {@link App} for a certain {@link Genre}.
 * 
 * @author Claas Wilke
 */
@SessionScoped
@Named("sessionState")
public class SessionState implements Serializable {

	/**
	 * Indicates the number of {@link App}s to be displayed on a page in the
	 * {@link Genre} view.
	 */
	private static int APPS_PER_PAGE = 10;

	/** ID used for serialization. */
	private static final long serialVersionUID = 7965455427888195913L;

	/** The {@link IAppManager} used to resolve {@link App} entities. */
	@Inject
	private IAppManager appManager;

	/** The currently selected {@link App}, if any. */
	private App currentApp;

	/** The currently selected {@link App} {@link Version}, if any. */
	private Version currentAppVersion;

	/** The currently selected {@link AppOrder} to display {@link App}s. */
	private AppOrder currentAppOrder = AppOrder.ENERGY_ASC;

	/** The currently selected {@link Genre}, if any. */
	private Genre currentGenre;

	/**
	 * Indicates which is the first {@link App} to be displayed in a
	 * {@link Genre} (currentPage / APPS_PER_PAGE).
	 */
	private Integer currentPage = 0;

	/**
	 * The {@link App} having the best energy behavior in the currently selected
	 * {@link Genre} under the current use case weights or <code>null</code> if
	 * the {@link App} has not been computed yet.
	 */
	private App topRunnerOfGenre = null;

	private Long topRunnerTimestamp = null;

	/**
	 * The total number of pages for {@link App}s being displayed in the
	 * currently selected {@link Genre}.
	 */
	private int totalPages;

	private Long totalPagesTimestamp = null;

	/**
	 * Contains the current weights of the use cases of the currently selected
	 * {@link Genre}.
	 */
	private Map<String, String> useCaseIntensity = new HashMap<String, String>();

	/**
	 * Returns a {@link List} of all {@link App}s belonging to the currently
	 * selected {@link Genre} in the currently selected {@link AppOrder}.
	 * 
	 * @return A {@link List} of all {@link App}s belonging to the currently
	 *         selected {@link Genre} in the currently selected {@link AppOrder}
	 *         .
	 */
	public List<App> getAppsOfCurrentGenre() {
		if (null != currentGenre)
			try {
				Map<Transition, Integer> weights = getCurrentWeights();

				List<App> result = appManager.getAppsOfGenreByConsumption(
						currentGenre, weights, currentAppOrder, APPS_PER_PAGE
								* currentPage, APPS_PER_PAGE);

				return result;
			} catch (Exception e) {
				return Collections.emptyList();
			}
		else
			return Collections.emptyList();
	}

	/**
	 * Returns the currently selected {@link App}, if any.
	 * 
	 * @return The currently selected {@link App}, if any.
	 */
	public App getCurrentApp() {
		return currentApp;
	}

	/**
	 * Returns the currently selected {@link AppOrder} to display {@link App}s.
	 * 
	 * @return The currently selected {@link AppOrder} to display {@link App}s.
	 */
	public AppOrder getCurrentAppOrder() {
		return currentAppOrder;
	}

	/**
	 * Returns the currently selected {@link App} {@link Version}, if any.
	 * 
	 * @return The currently selected {@link App} {@link Version}, if any.
	 */
	public Version getCurrentAppVersion() {
		return currentAppVersion;
	}

	/**
	 * Returns the offset to the rank of the first {@link App} of the currently
	 * displayed page of {@link App}s in a {@link Genre}.
	 * 
	 * @return The offset to the rank of the first {@link App} of the currently
	 *         displayed page of {@link App}s in a {@link Genre}.
	 */
	public int getCurrentFirstRank() {
		return currentPage * APPS_PER_PAGE;
	}

	/**
	 * Returns the currently selected {@link Genre}, if any.
	 * 
	 * @return The currently selected {@link Genre}, if any.
	 */
	public Genre getCurrentGenre() {
		return currentGenre;
	}

	/**
	 * Returns the index of the currently displayed page of {@link App}s in a
	 * {@link Genre} (starts with 0).
	 * 
	 * @return The index of the currently displayed page of {@link App}s in a
	 *         {@link Genre} (starts with 0).
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * Returns the {@link App} having the best energy behavior in the currently
	 * selected {@link Genre} under the current use case weights.
	 * 
	 * @return The {@link App} having the best energy behavior in the currently
	 *         selected {@link Genre} under the current use case weights.
	 */
	public App getTopRunnerAppOfGenre() {
		if ((null == topRunnerOfGenre || (System.currentTimeMillis() - topRunnerTimestamp) > 1000)
				&& null != currentGenre) {
			try {
				List<App> result = appManager.getAppsOfGenreByConsumption(
						currentGenre, getCurrentWeights(), AppOrder.ENERGY_ASC,
						0, 1);

				if (result.size() > 0) {
					topRunnerOfGenre = result.get(0);
					topRunnerTimestamp = System.currentTimeMillis();
				}
				// no else.

			} catch (Exception e) {
				/* No top runner could be retrieved. */
			}
		}
		// no else.

		return topRunnerOfGenre;
	}

	/**
	 * Returns the total number of pages for {@link App}s being displayed in the
	 * currently selected {@link Genre}.
	 * 
	 * @return The total number of pages for {@link App}s being displayed in the
	 *         currently selected {@link Genre}.
	 */
	public int getTotalPages() {
		if (0 == totalPages
				|| ((System.currentTimeMillis() - totalPagesTimestamp) > 1000)) {
			if (null != currentGenre)
				try {
					totalPages = (int) Math
							.round((appManager
									.getNumberOfAppsInGenre(currentGenre) / (float) APPS_PER_PAGE));
					if (0 == totalPages)
						totalPages = 1;
					// no else.

					totalPagesTimestamp = System.currentTimeMillis();
				} catch (Exception e) {
					totalPages = 0;
				}
			else
				totalPages = 0;
		}
		// no else.

		return totalPages;
	}

	/**
	 * Returns the current weights of the use cases of the currently selected
	 * {@link Genre}.
	 * 
	 * @return The current weights of the use cases of the currently selected
	 *         {@link Genre}.
	 */
	public Map<String, String> getUseCaseIntensity() {
		return useCaseIntensity;
	}

	/**
	 * Sets the currently selected {@link App} and triggers the opening of the
	 * app detail page.
	 * 
	 * @param appToBeOpened
	 *            The {@link App} to be opened.
	 */
	public void openApp(App appToBeOpened) {
		setCurrentApp(appToBeOpened);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String redirect = "app";
		NavigationHandler myNav = facesContext.getApplication()
				.getNavigationHandler();
		myNav.handleNavigation(facesContext, null, redirect);
	}

	/**
	 * Sets the currently selected {@link Genre} and triggers the opening of the
	 * genre/store page.
	 * 
	 * @param currentGenre
	 *            The currently selected {@link Genre} or <code>null</code>.
	 */
	public void openGenre(Genre currentGenre) {
		setCurrentGenre(currentGenre);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String redirect = "genre";
		NavigationHandler myNav = facesContext.getApplication()
				.getNavigationHandler();
		myNav.handleNavigation(facesContext, null, redirect);
	}

	/**
	 * Sets the currently selected {@link AppOrder} to display {@link App}s.
	 * 
	 * @param currentAppOrder
	 *            The currently selected {@link AppOrder} to display {@link App}
	 *            s.
	 */
	public void setCurrentAppOrder(AppOrder currentAppOrder) {
		this.currentAppOrder = currentAppOrder;
	}

	/**
	 * Sets the currently selected {@link App} {@link Version}, if any.
	 * 
	 * @param currentAppVersion
	 *            The currently selected {@link App} {@link Version}, if any.
	 */
	public void setCurrentAppVersion(Version currentAppVersion) {
		this.currentAppVersion = currentAppVersion;
	}

	/**
	 * Sets the currently selected {@link Genre}.
	 * 
	 * @param currentGenre
	 *            The currently selected {@link Genre} or <code>null</code>.
	 */
	public void setCurrentGenre(Genre currentGenre) {

		if (null == this.currentGenre
				|| !this.currentGenre.getName().equals(currentGenre.getName())) {
			this.currentGenre = currentGenre;

			resetGenreCache();
		}
		// no else.
	}

	/**
	 * Sets the currently selected {@link App}.
	 * 
	 * @param currentApp
	 *            The currently selected {@link App} or <code>null</code>.
	 */
	public void setCurrentApp(App currentApp) {

		if (null == this.currentApp || !currentApp.equals(this.currentApp)) {
			resetAppCache();

			this.currentApp = currentApp;
			this.currentAppVersion = currentApp.getLatestVersion();
		}
		// no else.
	}

	/**
	 * Sets which is the first page of {@link App}s to be displayed in a
	 * {@link Genre}.
	 * 
	 * @param page
	 *            Indicates which is the first page of {@link App}s to be
	 *            displayed in a {@link Genre} (starts with 0).
	 */
	public void setCurrentPage(int page) {
		currentPage = page;

		if (currentPage < 0)
			currentPage = 0;
		// no else.

		int maxPages = getTotalPages();
		if (currentPage >= maxPages)
			currentPage = maxPages - 1;
		// no else.
	}

	/**
	 * Sets the current weights of the use cases of the currently selected
	 * {@link Genre}.
	 * 
	 * @param useCaseIntensity
	 *            The current weights of the use cases of the currently selected
	 *            {@link Genre}.
	 */
	public void setUseCaseIntensity(Map<String, String> useCaseIntensity) {
		this.useCaseIntensity = useCaseIntensity;
	}

	/**
	 * Helper method called to signal an update of the {@link SessionState} from
	 * a JSF page.
	 */
	public void updateGenre() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String redirect = "genre";
		NavigationHandler myNav = facesContext.getApplication()
				.getNavigationHandler();
		myNav.handleNavigation(facesContext, null, redirect);
	}

	/**
	 * Returns the {@link Transition}s belonging to the {@link UseCaseModel} of
	 * the currently selected {@link Genre} as a {@link Map} having their
	 * current settings as values.
	 * 
	 * @return The values of the {@link Transition}s as a {@link Map}.
	 */
	private Map<Transition, Integer> getCurrentWeights() {
		Map<Transition, Integer> weights;

		if (null != currentGenre.getUseCaseModel()) {
			weights = new HashMap<Transition, Integer>();

			for (Transition transition : currentGenre.getUseCaseModel()
					.getTransitions()) {
				String value = useCaseIntensity.get(transition.getName());
				int weight;

				if (null != value) {
					try {
						weight = Integer.parseInt(value.trim());
					} catch (NumberFormatException e) {
						weight = 1;
					}
				}

				else
					weight = 1;

				weights.put(transition, weight);
			}
			// end for.
		}

		else
			weights = Collections.emptyMap();
		return weights;
	}

	/**
	 * Helper method computing the energy label for an app by its power rate and
	 * a top runner power rate.
	 * 
	 * @param powerRate
	 *            The power rate of the {@link App} to compute the label for.
	 * @param topRunnerRate
	 *            The power rate of the top runner {@link App}.
	 * @return The computed energy label as a lower case letter.
	 */
	private String getEnergyLabel(double powerRate, double topRunnerRate) {

		if (powerRate == -1 || topRunnerRate == -1)
			return "unknown";
		// no else.

		/* Compute percentual difference. */
		float percentage = (float) (powerRate / topRunnerRate) - 1f;
		float factor = 1 / 6f;

		if (percentage < factor)
			return "a";
		else if (percentage < 2 * factor)
			return "b";
		else if (percentage < 3 * factor)
			return "c";
		else if (percentage < 4 * factor)
			return "d";
		else if (percentage < 5 * factor)
			return "e";
		else if (percentage < 6 * factor)
			return "f";
		else
			return "g";
	}

	/**
	 * Helper method to reset {@link App}-specific values that were cached and
	 * have to be reset once the currently selected {@link App} changed.
	 */
	private void resetAppCache() {
		currentAppVersion = null;
	}

	/**
	 * Helper method to reset {@link Genre}-specific values that were cached and
	 * have to be reset once the currently selected {@link Genre} changed.
	 */
	private void resetGenreCache() {

		topRunnerOfGenre = null;
		topRunnerTimestamp = null;
		currentPage = 0;
		totalPages = 0;
		totalPagesTimestamp = null;

		if (null != currentGenre.getUseCaseModel())
			for (Transition transition : currentGenre.getUseCaseModel()
					.getTransitions()) {
				useCaseIntensity.put(transition.getName(), "1");
			}
		// end for.
		// no else.
	}

	private static final int REFACTOR_FROM_HERE = 0;

	/**
	 * Computes and returns the energy label for the given {@link App} under the
	 * current usage profile context.
	 * 
	 * @param app
	 *            The {@link App} whose label shall be computed.
	 * @return The compute label as a character between 'a' and 'g'.
	 */
	public String getEnergyLabelOfApp(App app) {

		double currentPowerRate = getPowerRateOfApp(app);
		double topRunnerRate = getPowerRateOfApp(getTopRunnerAppOfGenre());

		return getEnergyLabel(currentPowerRate, topRunnerRate);
	}

	/**
	 * Computes and returns the energy label for the given {@link App} for a
	 * given {@link Transition}.
	 * 
	 * @param app
	 *            The {@link App} whose label shall be computed.
	 * @param transition
	 *            The {@link Transition}.
	 * @return The compute label as a character between 'a' and 'g'.
	 */
	public String getEnergyLabelOfApp(App app, final Transition transition) {

		try {
			Map<Transition, Integer> weights = new HashMap<Transition, Integer>();
			weights.put(transition, 1);

			Version appVersion = app.getLatestVersion();
			Version topRunnerVersion = appManager
					.getAppsOfGenreByConsumption(currentGenre, weights,
							AppOrder.ENERGY_ASC, 0, 1).get(0)
					.getLatestVersion();

			if (null != appVersion && null != topRunnerVersion)
				return getEnergyLabel(appVersion.getPowerRate(transition),
						topRunnerVersion.getPowerRate(transition));
			else
				return "unknown";
		}

		catch (Exception e) {
			return "unknown";
		}
	}

	/**
	 * Computes and returns the energy rank for the given {@link App} under the
	 * current usage profile context.
	 * 
	 * @param app
	 *            The {@link App} whose rank shall be computed.
	 * @return The compute rank.
	 */
	public int getEnergyRankOfApp(App app) {

		if (null != currentGenre) {
			try {
				return appManager.getEnergyRankOfApp(app, currentGenre,
						getCurrentWeights());
			} catch (Exception e) {
				return -1;
			}
		} else
			return -1;
	}

	/**
	 * Computes and returns the energy rank for the given {@link App} for the
	 * given {@link Transition}.
	 * 
	 * @param app
	 *            The {@link App} whose rank shall be computed.
	 * @param transition
	 *            The {@link Transition}.
	 * @return The compute rank.
	 */
	public int getEnergyRankOfApp(App app, final Transition transition) {

		if (null != currentGenre) {
			try {
				Map<Transition, Integer> weights = new HashMap<Transition, Integer>();
				weights.put(transition, 1);

				return appManager
						.getEnergyRankOfApp(app, currentGenre, weights);
			} catch (Exception e) {
				return -1;
			}
		} else
			return -1;
	}

	/**
	 * Computes and returns an average power rate for the given {@link App} in
	 * the current usage profile context.
	 * 
	 * @param app
	 *            The {@link App} for which an average power rate shall be
	 *            computed.
	 * @return The computed average power rate.
	 */
	public double getPowerRateOfApp(App app) {
		try {
			return appManager.getPowerRateOfApp(app, getCurrentWeights());
		} catch (Exception e) {
			return -1;
		}
	}
}