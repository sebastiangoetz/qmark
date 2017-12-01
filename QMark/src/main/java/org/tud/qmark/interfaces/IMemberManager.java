package org.tud.qmark.interfaces;

import org.tud.qmark.entities.App;
import org.tud.qmark.entities.Member;
import org.tud.qmark.entities.Type;
import org.tud.qmark.entities.User;

/**
 * Manages {@link Member} entities.
 * 
 * @author Claas Wilke
 */
public interface IMemberManager {

	/**
	 * Adds a new {@link Member}.
	 * 
	 * @param member
	 *            The {@link Member} to be addded.
	 * @return A message indicating the result of the methods invocation.
	 * @throws Exception
	 */
	public String addMember(Member member) throws Exception;

	/**
	 * Checks whether a given {@link User} is a {@link Member} of a given
	 * {@link App}.
	 * 
	 * @param app
	 *            The {@link App}.
	 * @param user
	 *            The {@link User}.
	 * @return <code>true</code> if the {@link User} is a {@link Member} of the
	 *         given {@link App}.
	 */
	public boolean isMember(App app, User user);

	/**
	 * Checks whether a given {@link User} is a {@link Member} of a given
	 * {@link Type} for a given {@link App}.
	 * 
	 * @param app
	 *            The {@link App}.
	 * @param user
	 *            The {@link User}.
	 * @param type
	 *            The {@link Type}.
	 * @return <code>true</code> if the {@link User} is a {@link Member} of the
	 *         given {@link Type} for the given {@link App}.
	 */
	public boolean isMemberOfType(App app, User user, Type type);

	/**
	 * Updates a {@link Member} in the DB.
	 * 
	 * @param member
	 *            The {@link Member} to be updated.
	 * @return A message indicating the result of the methods invocation.
	 * @throws Exception
	 */
	public String updateMember(Member member) throws Exception;
}
