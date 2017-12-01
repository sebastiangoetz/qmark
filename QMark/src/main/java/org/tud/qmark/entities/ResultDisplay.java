/*
 * Created on 24.10.2012
 */

package org.tud.qmark.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Sebastian Richy - maquiz@googlemail.com
 * 
 */
@Entity(name = "ResultDisplay")
@Table(name = "ResultDisplay")
public class ResultDisplay implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -4238546799774521280L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "displayID")
	private Long displayID;
	@Column(name = "testRunID")
	private Long testRunID;
	@Column(name = "brightness")
	private Float brightness;
	@Column(name = "time")
	private Long time;

	public Long getDisplayID() {
		return displayID;
	}

	public void setDisplayID(Long displayID) {
		this.displayID = displayID;
	}

	public Long getTestRunID() {
		return testRunID;
	}

	public void setTestRunID(Long testRunID) {
		this.testRunID = testRunID;
	}

	public Float getBrightness() {
		return brightness;
	}

	public void setBrightness(Float brightness) {
		this.brightness = brightness;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
}
