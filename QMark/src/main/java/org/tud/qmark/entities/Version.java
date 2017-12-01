/*
 * Created on 24.10.2012
 */

package org.tud.qmark.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author Sebastian Richy - maquiz@googlemail.com This Class is not fully
 *         controlled via JPA. On the database, it has also the column "apk" as
 *         BLOB to save the binary of the App.
 */
@Entity(name = "Version")
@Table(name = "Version")
public class Version implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = 2776032648657746180L;

	/** The APK file ({@link Binary}) of this {@link Version}. */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "apkBinaryID", nullable = true, unique = true)
	private Binary apk;

	/** The {@link App} this {@link Version} belongs to. */
	@ManyToOne
	@JoinColumn(name = "appID", unique = false, updatable = false, nullable = false)
	private App app;

	/**
	 * The {@link EnergyModel} representing the energy efficiency of this
	 * {@link Version}.
	 */
	@OneToOne
	@JoinColumn(name = "energyModelID", unique = true, nullable = true, updatable = true)
	private EnergyModel energyModel;

	/** The name of this {@link Version} (e.g., honeycomb). */
	@Column(name = "name", nullable = true)
	private String name;

	/** The {@link TestSuite}s associated to this {@link Version}. */
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "versionID")
	private Set<TestSuite> testSuites;

	/** The visibility of this {@link Version} (e.g., private or released). */
	@OneToOne
	@JoinColumn(name = "visibilityTypeID", nullable = false, unique = false)
	private Type visibility;

	/** The version ID of this {@link Version} (e.g., 1.0.1). */
	@Column(name = "vendorVersionID", nullable = false)
	private String vendorVersionID;

	/** The ID of this {@link Version}. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "versionID")
	private Long versionID;

	/**
	 * Returns the APK file ({@link Binary}) of this {@link Version}.
	 * 
	 * @return The APK file ({@link Binary}) of this {@link Version}.
	 */
	public Binary getApk() {
		return apk;
	}

	/**
	 * Returns the {@link App} this {@link Version} belongs to.
	 * 
	 * @return The {@link App} this {@link Version} belongs to.
	 */
	public App getApp() {
		return app;
	}

	/**
	 * Returns the {@link EnergyModel} representing the energy efficiency of
	 * this {@link Version}.
	 * 
	 * @return The {@link EnergyModel} representing the energy efficiency of
	 *         this {@link Version}.
	 */
	public EnergyModel getEnergyModel() {
		return energyModel;
	}

	/**
	 * Returns the name of this {@link Version} (e.g., honeycomb).
	 * 
	 * @return The name of this {@link Version} (e.g., honeycomb).
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the power rate of this {@link App} for a given {@link Transition}
	 * belonging to the {@link UseCaseModel} of this {@link App}'s
	 * {@link EnergyModel}.
	 * 
	 * @param transition
	 *            The {@link Transition} whose power rate shall be returned.
	 * @return The found power rate or <code>-1</code>.
	 */
	public double getPowerRate(Transition transition) {

		double result = -1d;

		for (PowerRate rate : getEnergyModel().getPowerRates()) {
			if (rate.getTransition().getName().equals(transition.getName())) {
				if (null != rate.getValue())
					result = rate.getValue();
				break;
			}
			// no else.
		}
		// end for.

		return result;
	}

	/**
	 * Returns the {@link TestSuite}s associated to this {@link Version}.
	 * 
	 * @return The {@link TestSuite}s associated to this {@link Version}.
	 */
	public Set<TestSuite> getTestSuites() {
		if (null == testSuites)
			testSuites = new HashSet<TestSuite>();
		// no else.

		return testSuites;
	}

	/**
	 * Returns the version ID of this {@link Version} (e.g., 1.0.1).
	 * 
	 * @return The version ID of this {@link Version} (e.g., 1.0.1).
	 */
	public String getVendorVersionID() {
		return vendorVersionID;
	}

	/**
	 * Returns the ID of this {@link Version}.
	 * 
	 * @return The ID of this {@link Version}.
	 */
	public Long getVersionID() {
		return versionID;
	}

	/**
	 * Returns the visibility of this {@link Version} (e.g., private or
	 * released).
	 * 
	 * @return The visibility of this {@link Version} (e.g., private or
	 *         released).
	 */
	public Type getVisibility() {
		return visibility;
	}

	/**
	 * Sets the APK file ({@link Binary}) of this {@link Version}.
	 * 
	 * @param apk
	 *            The APK file ({@link Binary}) of this {@link Version}.
	 */
	public void setApk(Binary apk) {
		this.apk = apk;
	}

	/**
	 * Sets the {@link App} this {@link Version} belongs to.
	 * 
	 * @param app
	 *            The {@link App} this {@link Version} belongs to.
	 */
	public void setApp(App app) {
		this.app = app;
	}

	/**
	 * Sets the {@link EnergyModel} representing the energy efficiency of this
	 * {@link Version}.
	 * 
	 * @param energyModel
	 *            The {@link EnergyModel} representing the energy efficiency of
	 *            this {@link Version}.
	 */
	public void setEnergyModel(EnergyModel energyModel) {
		this.energyModel = energyModel;
	}

	/**
	 * Sets the name of this {@link Version} (e.g., honeycomb).
	 * 
	 * @param name
	 *            The name of this {@link Version} (e.g., honeycomb).
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the {@link TestSuite}s associated to this {@link Version}.
	 * 
	 * @param testSuites
	 *            The {@link TestSuite}s associated to this {@link Version}.
	 */
	public void setTestSuites(Set<TestSuite> testSuites) {
		this.testSuites = testSuites;
	}

	/**
	 * Sets the version ID of this {@link Version} (e.g., 1.0.1).
	 * 
	 * @param vendorVersionID
	 *            The version ID of this {@link Version} (e.g., 1.0.1).
	 */
	public void setVendorVersionID(String vendorVersionID) {
		this.vendorVersionID = vendorVersionID;
	}

	/**
	 * Sets the ID of this {@link Version}.
	 * 
	 * @param versionID
	 *            The ID of this {@link Version}.
	 */
	public void setVersionID(Long versionID) {
		this.versionID = versionID;
	}

	/**
	 * Sets the visibility of this {@link Version} (e.g., private or released).
	 * 
	 * @param visibility
	 *            The visibility of this {@link Version} (e.g., private or
	 *            released).
	 */
	public void setVisibility(Type visibility) {
		this.visibility = visibility;
	}
}
