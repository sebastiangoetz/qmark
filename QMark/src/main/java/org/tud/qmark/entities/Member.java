/*
 * Created on 30.10.2012
 */

package org.tud.qmark.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * A {@link Member} represents relations between {@link App}s and {@link User}s.
 * Thus, multiple {@link User}s can maintain the same {@link App}s, possibly
 * having different {@link Type}s.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 */
@Entity
@Table(name = "Member")
public class Member implements Serializable, Comparable<Member> {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = 7138529604543052487L;

	/** The {@link App} of this {@link Member}. */
	@OneToOne
	@JoinColumn(name = "appID", unique = false, nullable = false, updatable = false)
	private App app;

	/** The ID of this {@link Member}. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "memberID")
	private Long memberID;

	/** The {@link Type} of this {@link Member}. */
	@OneToOne
	@JoinColumn(name = "typeID", unique = false, nullable = false, updatable = true)
	private Type type;

	/** The {@link User} of this {@link Member}. */
	@ManyToOne
	@JoinColumn(name = "userID", unique = false, nullable = false, updatable = false)
	private User user;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Member o) {
		return getApp().getName().compareTo(o.getApp().getName());
	}

	/**
	 * Returns the {@link App} of this {@link Member}.
	 * 
	 * @return The {@link App} of this {@link Member}.
	 */
	public App getApp() {
		return app;
	}

	/**
	 * Returns the ID of this {@link Member}.
	 * 
	 * @return The ID of this {@link Member}.
	 */
	public Long getMemberID() {
		return memberID;
	}

	/**
	 * Returns the {@link Type} of this {@link Member}.
	 * 
	 * @return The {@link Type} of this {@link Member}.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the {@link User} of this {@link Member}.
	 * 
	 * @return The {@link User} of this {@link Member}.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the {@link App} of this {@link Member}.
	 * 
	 * @param app
	 *            The {@link App} of this {@link Member}.
	 */
	public void setApp(App app) {
		this.app = app;
	}

	/**
	 * Sets the ID of this {@link Member}.
	 * 
	 * @param memberID
	 *            The ID of this {@link Member}.
	 */
	public void setMemberID(Long memberID) {
		this.memberID = memberID;
	}

	/**
	 * Sets the {@link Type} of this {@link Member}.
	 * 
	 * @param type
	 *            The {@link Type} of this {@link Member}.
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Sets the {@link User} of this {@link Member}.
	 * 
	 * @param user
	 *            The {@link User} of this {@link Member}.
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
