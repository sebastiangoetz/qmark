package org.tud.qmark.interfaces;

import java.util.List;

import org.tud.qmark.entities.Type;

/**
 * Manages {@link Type} entities.
 * 
 * @author Claas Wilke
 */
public interface ITypeManager {

	/**
	 * Returns the {@link Type} having the given name or <code>null</code> if no
	 * such type exists.
	 * 
	 * @param name
	 *            The name the {@link Type} should have.
	 * @return The {@link Type} having the given name or <code>null</code> if no
	 *         such type exists.
	 * @throws Exception
	 */
	public Type getType(String name) throws Exception;

	/**
	 * Returns the direct sub {@link Type}s of the given {@link Type}. Please
	 * note that this method will not return the transitive closure but direct
	 * sub {@link Type}s only.
	 * 
	 * @param type
	 *            The {@link Type} whose direct sub {@link Type}s shall be
	 *            returned.
	 * @return The direct sub {@link Type}s of the given {@link Type}.
	 * @throws Exception
	 */
	public List<Type> getSubTypes(Type type) throws Exception;
}
