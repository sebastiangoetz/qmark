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
@Entity
@Table(name = "ResultPowerRate")
public class ResultPowerRate implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = 2899296690754840429L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "powerRateID")
	private Long powerRateID;
	@Column(name = "testRunID")
	private Long testRunID;
	@Column(name = "power")
	private Float power;
	@Column(name = "time")
	private Long time;

	public Long getPowerRateID() {
		return powerRateID;
	}

	public void setPowerRateID(Long powerRateID) {
		this.powerRateID = powerRateID;
	}

	public Long getTestRunID() {
		return testRunID;
	}

	public void setTestRunID(Long testRunID) {
		this.testRunID = testRunID;
	}

	public Float getPower() {
		return power;
	}

	public void setPower(Float power) {
		this.power = power;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

}
