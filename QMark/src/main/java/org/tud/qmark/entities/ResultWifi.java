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
@Table(name = "ResultWifi")
public class ResultWifi implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -3410187517730553457L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "wifiID")
	private Long wifiID;
	@Column(name = "testRunID")
	private Long testRunID;
	@Column(name = "traffic")
	private Float traffic;
	@Column(name = "time")
	private Long time;

	public Long getWifiID() {
		return wifiID;
	}

	public void setWifiID(Long wifiID) {
		this.wifiID = wifiID;
	}

	public Long getTestRunID() {
		return testRunID;
	}

	public void setTestRunID(Long testRunID) {
		this.testRunID = testRunID;
	}

	public Float getTraffic() {
		return traffic;
	}

	public void setTraffic(Float traffic) {
		this.traffic = traffic;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

}
