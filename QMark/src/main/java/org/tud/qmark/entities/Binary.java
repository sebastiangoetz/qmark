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
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Represents binary content (e.g., APK files)
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 */
@Entity
@Table(name = "BinaryContent")
public class Binary implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -5688808188125794859L;

	/** The ID of this {@link Binary}. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "binaryID")
	private Long binaryID;

	/** The content of this {@link Binary}. */
	@Column(name = "content", columnDefinition = "blob")
	@Lob
	private byte[] content;

	/** The content type of this {@link Binary}. */
	private String contentType;

	/** The file name of this {@link Binary}. */
	private String name;

	/**
	 * Returns the ID of this {@link Binary}.
	 * 
	 * @return The ID of this {@link Binary}.
	 */
	public Long getBinaryID() {
		return binaryID;
	}

	/**
	 * Sets the ID of this {@link Binary}.
	 * 
	 * @param binaryID
	 *            The ID of this {@link Binary}.
	 */
	public void setBinaryID(Long binaryID) {
		this.binaryID = binaryID;
	}

	/**
	 * Returns the content of this {@link Binary}.
	 * 
	 * @return The content of this {@link Binary}.
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * Returns the content type of this {@link Binary}.
	 * 
	 * @return The content type of this {@link Binary}.
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Returns the file name of this {@link Binary}.
	 * 
	 * @return The file name of this {@link Binary}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the content of this {@link Binary}.
	 * 
	 * @param content
	 *            The content of this {@link Binary}.
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}

	/**
	 * Sets the content type of this {@link Binary}.
	 * 
	 * @param contentType
	 *            The content type of this {@link Binary}.
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Sets the file name of this {@link Binary}.
	 * 
	 * @param name
	 *            The file name of this {@link Binary}.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Binary other = (Binary) obj;
		if (binaryID == null) {
			if (other.binaryID != null)
				return false;
		} else if (!binaryID.equals(other.binaryID))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((binaryID == null) ? 0 : binaryID.hashCode());
		return result;
	}

}
