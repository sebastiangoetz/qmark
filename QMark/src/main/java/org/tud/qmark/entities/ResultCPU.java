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
@Table(name = "ResultCPU")
public class ResultCPU implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -4161265657195766023L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cpuID")
	private Long cpuID;
	@Column(name = "testRunID")
	private Long testRunID;
	@Column(name = "number")
	private Float number;
	@Column(name = "frequence")
	private Float frequence;
	@Column(name = "time")
	private Long time;

	public Long getCpuID() {
		return cpuID;
	}

	public void setCpuID(Long cpuID) {
		this.cpuID = cpuID;
	}

	public Long getTestRunID() {
		return testRunID;
	}

	public void setTestRunID(Long testRunID) {
		this.testRunID = testRunID;
	}

	public Float getNumber() {
		return number;
	}

	public void setNumber(Float number) {
		this.number = number;
	}

	public Float getFrequence() {
		return frequence;
	}

	public void setFrequence(Float frequence) {
		this.frequence = frequence;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

}
