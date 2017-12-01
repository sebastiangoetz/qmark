package org.tud.qmark.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.tud.qmark.entities.Binary;
import org.tud.qmark.entities.EnergyModel;
import org.tud.qmark.entities.PowerRate;
import org.tud.qmark.entities.Version;
import org.tud.qmark.interfaces.IVersionManager;
import org.tud.qmark.interfaces.QMarkSessionBean;

/**
 * EJB manager implementation of {@link IVersionManager}.
 * 
 * @author Claas Wilke
 */
@Named("versionManager")
@Stateful
@RequestScoped
public class EJBVersionManager extends QMarkSessionBean implements IVersionManager {

	/** The {@link Logger} used. */
	@Inject
	private transient Logger logger;

	/** The {@link EntityManager} used. */
	@Inject
	private EntityManager versionDatabase;

	/** The current {@link UserTransaction}. */
	@Inject
	private UserTransaction utx;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.IVersionManager#addVersion(org.tud.qmark.entities
	 * .Version)
	 */
	public String addVersion(Version version) throws Exception {
		persistApk(version);
		versionDatabase.persist(version);
		logger.info("Added " + version);
		return "versionAdded";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.IVersionManager#getApkOfVersion(org.tud.qmark
	 * .entities.Version)
	 */
	public Binary getApkOfVersion(Version version) throws Exception {
		@SuppressWarnings("unchecked")
		List<Binary> results = versionDatabase
				.createQuery(
						"select b from Binary b, Version v where v=:version and v.apk=b")
				.setParameter("version", version).getResultList();
		if (results.isEmpty()) {
			return null;
		} else if (results.size() > 1) {
			throw new IllegalStateException(
					"Cannot have more than one APK file for the same version!");
		} else {
			return results.get(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tud.qmark.interfaces.IVersionManager#updateVersion(org.tud.qmark.
	 * entities.Version)
	 */
	public void updateVersion(Version version) throws Exception {
		persistApk(version);
		versionDatabase.merge(version);

		EnergyModel energyModel = version.getEnergyModel();
		if (null != energyModel) {
			if (null != energyModel.getEnergyModelID())
				versionDatabase.merge(energyModel);
			else
				versionDatabase.persist(energyModel);

			for (PowerRate rate : energyModel.getPowerRates()) {
				if (null != rate.getPowerRateID())
					versionDatabase.merge(rate);
				else
					versionDatabase.persist(rate);
			}
		}
		// no else.

		logger.info("Updated " + version);
	}

	/**
	 * Helper method storing the APK {@link Binary} of a given {@link Version}
	 * in the DB if it is set.
	 * 
	 * @param version
	 *            The {@link Version} whose {@link Binary} shall be persisted.
	 */
	private boolean persistApk(Version version) {
		if (null != version.getApk()) {
			if (null == version.getApk().getBinaryID())
				versionDatabase.persist(version.getApk());
			/* Do not update APK. Too large files may cause timeouts. */
			// else
			// versionDatabase.merge(version.getApk());
			return true;
		}

		else {
			return false;
		}
	}
}
