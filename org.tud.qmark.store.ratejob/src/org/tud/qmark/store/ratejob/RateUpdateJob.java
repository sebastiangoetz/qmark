package org.tud.qmark.store.ratejob;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.model.Market.App;
import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;

/**
 * Helper job that updates the ratings of apps in the store from Google Play.
 * 
 * @author Claas Wilke
 */
public class RateUpdateJob {

	/** The maximum number of apps being updated in one iteration. */
	private static final int APPS_PER_JOB = 10;

	/**
	 * Time interval between two executions of the {@link RateUpdateJob} in
	 * seconds.
	 */
	private static final int TIME_INTERVAL = 4 * 60 * 60;

	/** The {@link PrintStream} used to print progress messages (e.g., stdout). */
	private PrintStream console;

	/** The current {@link MarketSession} of this {@link RateUpdateJob}. */
	private MarketSession session;

	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		RateUpdateJob job = new RateUpdateJob();
		job.console = System.out;
		job.run();
	}

	/** Run method called by main. */
	public void run() {

		while (true) {

			logIn();
			DbManager mgr = new DbManager(console);

			try {
				mgr.createConnection();

				/* Fetch apps to be updated from the DB. */
				List<String> apps = mgr.getAppsForUpdate(APPS_PER_JOB);
				Map<String, Float> rates = new HashMap<String, Float>(
						APPS_PER_JOB);

				console.println("Fetch app ratings from Google Play ...");

				for (String appId : apps) {

					Float appRate = fetchAppRate(appId);
					console.println("Fetched rate for '" + appId + "': "
							+ appRate);
					rates.put(appId, appRate);
				}
				// end for.

				/* Update app rates in DB. */
				console.println("Update app ratings in DB ...");
				mgr.updateAppRates(rates);
				console.println("Done.");

				mgr.closeConnection();
			}

			catch (Exception e) {
				console.print("Error during update of app rates: "
						+ e.getMessage());
				try {
					mgr.closeConnection();
				} catch (Exception e2) {
					/* Not important. */
				}
			}

			logOut();

			/* TODO Change this for infinite loop. */
			break;
			// try {
			// Thread.sleep(TIME_INTERVAL * 1000);
			// } catch (InterruptedException e) {
			// /* Not that important. */
			// }
		}
	}

	/**
	 * Helper method to log in and create a new {@link MarketSession}.
	 */
	private void logIn() {
		String login = "chuck.nao@gmail.com";
		String password = "Head2Hot";
		String androidId = "34f929c9a64750ca";

		console.println("Login...");
		session = new MarketSession(false);
		session.login(login, password, androidId);
		console.println("Login done");
	}

	/**
	 * Helper method to log out (destroys {@link MarketSession}).
	 */
	private void logOut() {
		session = null;
	}

	/**
	 * Fetches the average rating of an app from Google Play by its given
	 * package ID.
	 * 
	 * @param appId
	 *            The package ID (a unique identifier, e.g.,
	 *            com.joko.paperland).
	 * @return The rate as a float between 0.0 and 5.0 or -1 if an error
	 *         occurred.
	 */
	private float fetchAppRate(String appId) {
		float result = -1;

		AppsRequest appsRequest = AppsRequest.newBuilder().setAppId(appId)
				.build();

		List<Object> response = session.queryApp(appsRequest);

		AppsResponse apps;

		if (response.size() > 0) {
			apps = (AppsResponse) response.get(0);

			if (apps.getAppCount() > 0) {
				App app = apps.getApp(0);
				result = Float.parseFloat(app.getRating());
			}
			// no else.
		}
		// no else.

		return result;
	}

}
