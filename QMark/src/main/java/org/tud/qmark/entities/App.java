/*
 * Created on 24.10.2012
 */

package org.tud.qmark.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Represents Apps and their related project.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 */
@Entity
@Table(name = "App")
public class App implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -6368446908164441661L;

	/** The ID of an {@link App} in the DB. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "appID")
	private Long appID;

	/** The description of this {@link App}. */
	@Column(name = "description")
	private String description;

	/** The {@link Genre} this {@link App} belongs to. */
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "genreID", unique = false, nullable = false, updatable = true)
	private Genre genre;

	/** The logo URL of this app. */
	@Column(name = "logo")
	private String logo;

	/** The name of the {@link App}. */
	@Column(name = "name")
	private String name;

	/**
	 * The package ID of this {@link App} (a unique identifier used in Google
	 * Play).
	 */
	@Column(name = "packageID")
	private String packageID;

	/**
	 * The {@link PaymentPlan} the {@link Vendor} of this {@link App} is using
	 * to profile this {@link App}.
	 */
	@OneToOne
	@JoinColumn(name = "paymentPlanID", unique = false, nullable = false, updatable = true)
	private PaymentPlan paymentPlan;

	/** The rating of the {@link App} within Google Play. */
	@Column(name = "rating")
	private Float rating;

	/**
	 * The {@link Date} the rating of this {@link App} has been extracted from
	 * Google Play.
	 */
	@Column(name = "ratingDate")
	private Date ratingDate;

	/** The {@link Vendor} of this {@link App}. */
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vendorID", unique = false, nullable = false, updatable = true)
	private Vendor vendor;

	/**
	 * The different {@link Version}s (releases) existing of this {@link App}.
	 */
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "appID")
	private Set<Version> versions;

	/**
	 * Returns the ID of an {@link App} in the DB.
	 * 
	 * @return The ID of an {@link App} in the DB.
	 */
	public Long getAppID() {
		return appID;
	}

	/**
	 * Returns the description of this {@link App}.
	 * 
	 * @return The description of this {@link App}.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the {@link Genre} this {@link App} belongs to.
	 * 
	 * @return The {@link Genre} this {@link App} belongs to.
	 */
	public Genre getGenre() {
		return genre;
	}

	/**
	 * Returns the most recent {@link Version} available for this {@link App}.
	 * 
	 * @return The most recent {@link Version} available for this {@link App}.
	 */
	public Version getLatestVersion() {
		List<Version> versions = new ArrayList<Version>(getVersions());

		if (versions.size() > 0) {
			Collections.sort(versions, new Comparator<Version>() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.util.Comparator#compare(java.lang.Object,
				 * java.lang.Object)
				 */
				public int compare(Version o1, Version o2) {
					/*
					 * TODO Assumes that all versions can be compared by a
					 * simple string comparison (inverse order).
					 */
					return o2.getVendorVersionID().compareTo(
							o1.getVendorVersionID());
				}
			});
			return versions.get(0);
		}

		else
			return null;
	}

	/**
	 * Returns the logo URL of this app.
	 * 
	 * @return The logo URL of this app.
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * Returns the name of the {@link App}.
	 * 
	 * @return The name of the {@link App}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the package ID of this {@link App} (a unique identifier used in
	 * Google Play).
	 * 
	 * @return The package ID of this {@link App} (a unique identifier used in
	 *         Google Play).
	 */
	public String getPackageID() {
		return packageID;
	}

	/**
	 * Returns the {@link PaymentPlan} the {@link Vendor} of this {@link App} is
	 * using to profile this {@link App}.
	 * 
	 * @return The {@link PaymentPlan} the {@link Vendor} of this {@link App} is
	 *         using to profile this {@link App}.
	 */
	public PaymentPlan getPaymentPlan() {
		return paymentPlan;
	}

	/**
	 * Returns the rating of the {@link App} within Google Play.
	 * 
	 * @return The rating of the {@link App} within Google Play.
	 */
	public Float getRating() {
		return rating;
	}

	/**
	 * Returns the {@link Date} the rating of this {@link App} has been
	 * extracted from Google Play.
	 * 
	 * @return The {@link Date} the rating of this {@link App} has been
	 *         extracted from Google Play.
	 */
	public Date getRatingDate() {
		return ratingDate;
	}

	/**
	 * Returns the average rating of this {@link App} in Google Play rounded as
	 * a String to retrieve the respective image.
	 * 
	 * @return The average rating rounded as an image.
	 */
	public String getRatingImage() {

		if (null == rating)
			return "unknown";
		else if (rating > 4.75)
			return "5";
		else if (rating >= 4.25)
			return "4_5";
		else if (rating > 3.75)
			return "4";
		else if (rating >= 3.25)
			return "3_5";
		else if (rating > 2.75)
			return "3";
		else if (rating >= 2.25)
			return "2_5";
		else if (rating > 1.75)
			return "2";
		else if (rating >= 1.25)
			return "1_5";
		else if (rating > .75)
			return "1";
		else if (rating >= .25)
			return "0_5";
		else if (rating > 0)
			return "0";
		else
			return "unknown";
	}

	/**
	 * Returns the {@link Vendor} of this {@link App}.
	 * 
	 * @return The {@link Vendor} of this {@link App}.
	 */
	public Vendor getVendor() {
		return vendor;
	}

	/**
	 * Returns the different {@link Version}s (releases) existing of this
	 * {@link App}.
	 * 
	 * @return The different {@link Version}s (releases) existing of this
	 *         {@link App}.
	 */
	public Set<Version> getVersions() {
		return versions;
	}

	/**
	 * Sets the ID of an {@link App} in the DB.
	 * 
	 * @param appID
	 *            The ID of an {@link App} in the DB.
	 */
	public void setAppID(Long appID) {
		this.appID = appID;
	}

	/**
	 * Sets the description of this {@link App}.
	 * 
	 * @param description
	 *            The description of this {@link App}.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the {@link Genre} this {@link App} belongs to.
	 * 
	 * @param genre
	 *            The {@link Genre} this {@link App} belongs to.
	 */
	public void setGenre(Genre genre) {
		this.genre = genre;
	}

	/**
	 * Sets the logo URL of this app.
	 * 
	 * @param logo
	 *            The logo URL of this app.
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}

	/**
	 * Sets the name of the {@link App}.
	 * 
	 * @param name
	 *            The name of the {@link App}.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the package ID of this {@link App} (a unique identifier used in
	 * Google Play).
	 * 
	 * @param packageID
	 *            The package ID of this {@link App} (a unique identifier used
	 *            in Google Play).
	 */
	public void setPackageID(String packageID) {
		this.packageID = packageID;
	}

	/**
	 * Sets the {@link PaymentPlan} the {@link Vendor} of this {@link App} is
	 * using to profile this {@link App}.
	 * 
	 * @param paymentPlan
	 *            The {@link PaymentPlan} the {@link Vendor} of this {@link App}
	 *            is using to profile this {@link App}.
	 */
	public void setPaymentPlan(PaymentPlan paymentPlan) {
		this.paymentPlan = paymentPlan;
	}

	/**
	 * Sets the rating of the {@link App} within Google Play.
	 * 
	 * @param rating
	 *            The rating of the {@link App} within Google Play.
	 */
	public void setRating(Float rating) {
		this.rating = rating;
	}

	/**
	 * Sets the {@link Date} the rating of this {@link App} has been extracted
	 * from Google Play.
	 * 
	 * @param ratingDate
	 *            The {@link Date} the rating of this {@link App} has been
	 *            extracted from Google Play.
	 */
	public void setRatingDate(Date ratingDate) {
		this.ratingDate = ratingDate;
	}

	/**
	 * Sets the {@link Vendor} of this {@link App}.
	 * 
	 * @param vendor
	 *            The {@link Vendor} of this {@link App}.
	 */
	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	/**
	 * Sets the different {@link Version}s (releases) existing of this
	 * {@link App}.
	 * 
	 * @param versions
	 *            The different {@link Version}s (releases) existing of this
	 *            {@link App}.
	 */
	public void setVersions(Set<Version> versions) {
		this.versions = versions;
	}

}
