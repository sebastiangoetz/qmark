/*
 * Created on 24.10.2012
 */

package org.tud.qmark.interfaces;

import java.util.List;

import org.tud.qmark.entities.CompanyMember;
import org.tud.qmark.entities.Member;
import org.tud.qmark.entities.Type;
import org.tud.qmark.entities.User;
import org.tud.qmark.entities.UserMetaData;

/**
 * Manages {@link User} entities.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 * @author Sebastian Götz - sebastian.goetz@acm.org
 */

public interface IUserManager {

	/** Tries to find a {@link User} for a given login and password. */
	public User findUser(String login, String password) throws Exception;

	/**
	 * Adds a new {@link User} to the DB.
	 * 
	 * @param user
	 *            The {@link User} to be added.
	 * @param type
	 * 			  The {@link Type} status (active or registered)
	 * @return A String indicating whether the add was successful.
	 * @throws Exception
	 */
	public String addUser(User user) throws Exception;

	/**
	 * Returns all {@link Member}s of a given {@link User}.
	 * 
	 * @param user
	 *            The {@link User}.
	 * @return A {@link List} of the {@link User}'s {@link Member}s.
	 * @throws Exception
	 */
	public List<Member> getAppMembers(User user) throws Exception;

	/**
	 * Returns all {@link CompanyMember}s of a given {@link User}.
	 * 
	 * @param user
	 *            The {@link User}.
	 * @return A {@link List} of the {@link User}'s {@link CompanyMember}s.
	 * @throws Exception
	 */
	public List<CompanyMember> getCompanyMembers(User user) throws Exception;

	/**
	 * Returns the {@link User} for the given mail address.
	 * 
	 * @param mail
	 *            The mail address as a {@link String}.
	 * @return The associated {@link User}.
	 * @throws Exception
	 */
	public User getUser(String mail) throws Exception;
	
	/**
	 * Returns all {@link User}s which are not yet actived.
	 * 
	 * @return A list of all not yet activated {@link User}s.
	 * @throws Exception
	 */
	public List<User> getUserRegistrations() throws Exception;

	/**
	 * Returns the {@link User} for the given {@link UserMetaData}.
	 * 
	 * @param metaData
	 *            The {@link UserMetaData}.
	 * @return The associated {@link User}.
	 * @throws Exception
	 */
	public User getUserFromMetaData(UserMetaData metaData) throws Exception;

	/**
	 * Updates a given {@link User} in the DB.
	 * 
	 * @param user
	 *            The {@link User} to be updated.
	 * @return A String indicating whether the update was successful.
	 * @throws Exception
	 */
	public String updateUser(User user) throws Exception;
}