package org.tud.qmark.interfaces;

import org.tud.qmark.entities.Binary;
import org.tud.qmark.entities.Version;

/**
 * Manages {@link Version} entities.
 * 
 * @author Claas Wilke
 */
public interface IVersionManager {

	/**
	 * Adds a new {@link Version}.
	 * 
	 * @param version
	 *            The {@link Version} to be addded.
	 * @return A message indicating the result of the methods invocation.
	 * @throws Exception
	 */
	public String addVersion(Version version) throws Exception;

	/**
	 * Returns the APK file of a given {@link Version} as a {@link Binary}
	 * entity.
	 * 
	 * @param version
	 *            The {@link Version} whose APK file shall be returned.
	 * @return The APK file as a {@link Binary} or <code>null</code>, if no APK
	 *         file exists for the given {@link Version}.
	 * @throws Exception
	 */
	public Binary getApkOfVersion(Version version) throws Exception;

	/**
	 * Updates a given {@link Version} in the DB.
	 * 
	 * @param version
	 *            The {@link Version} to be updated.
	 */
	public void updateVersion(Version version) throws Exception;
}
