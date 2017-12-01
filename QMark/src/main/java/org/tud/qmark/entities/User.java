/*
 * Created on 24.10.2012
 */

package org.tud.qmark.entities;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * A {@link User} represents a valid login for QMark.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 */
@Entity(name = "User")
@Table(name = "Uzer")
public class User implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -8969050871709638831L;

	/**
	 * The Memberships of this {@link User} each {@link Member} relates the user
	 * to an {@link App}.
	 */
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany
	@JoinColumn(name = "userID")
	private List<Member> memberships;

	/** The email address of this {@link User}. */
	@Column(name = "emailaddress", nullable = false)
	private String emailaddress;

	/** The status of this {@link User} (e.g., 'active' or 'registered'). */
	@OneToOne
	@JoinColumn(name = "typeID", unique = false, nullable = false, updatable = false)
	private Type status;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userID")
	private Long userID;
	@Column(name = "login")
	private String login;
	@Column(name = "password")
	private String password;
	@Column(name = "salt")
	private String salt;

	public String getSalt()
    {
        return salt;
    }

    public void setSalt(String salt)
    {
        this.salt = salt;
    }

    @OneToOne(optional = false/*
							 * , cascade={CascadeType.PERSIST,
							 * CascadeType.REMOVE, CascadeType.REFRESH}
							 */)
	@JoinColumn(name = "userMetaDataID", unique = true, nullable = false, updatable = false)
	private UserMetaData userMetaData;

	/**
	 * Returns the email address of this {@link User}.
	 * 
	 * @return The email address of this {@link User}.
	 */
	public String getEmailaddress() {
		return emailaddress;
	}

	/**
	 * Returns the Memberships of this {@link User} each {@link Member} relates
	 * the user to an {@link App}.
	 * 
	 * @return The Memberships of this {@link User} each {@link Member} relates
	 *         the user to an {@link App}.
	 */
	public List<Member> getMemberships() {
		Collections.sort(memberships);
		return memberships;
	}

	/**
	 * Returns the status of this {@link User} (e.g., 'active' or 'registered').
	 * 
	 * @return The status of this {@link User} (e.g., 'active' or 'registered').
	 */
	public Type getStatus() {
		return status;
	}

	/**
	 * Sets the email address of this {@link User}.
	 * 
	 * @param emailaddress
	 *            The email address of this {@link User}.
	 */
	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	/**
	 * Sets the Memberships of this {@link User} each {@link Member} relates the
	 * user to an {@link App}.
	 * 
	 * @param memberships
	 *            The Memberships of this {@link User} each {@link Member}
	 *            relates the user to an {@link App}.
	 */
	public void setMemberships(List<Member> memberships) {
		this.memberships = memberships;
	}

	/**
	 * Sets the status of this {@link User} (e.g., 'active' or 'registered').
	 * 
	 * @param status
	 *            The status of this {@link User} (e.g., 'active' or
	 *            'registered').
	 */
	public void setStatus(Type status) {
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User (username = " + login + ")";
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public UserMetaData getUserMetaData() {
		return userMetaData;
	}

	public void setUserMetaData(UserMetaData userMetaData) {
		this.userMetaData = userMetaData;
	}
}
