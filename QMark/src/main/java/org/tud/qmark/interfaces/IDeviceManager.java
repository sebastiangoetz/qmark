package org.tud.qmark.interfaces;

import java.util.List;

import org.tud.qmark.entities.Device;

/**
 * Manages {@link Device} entities.
 * 
 * @author Claas Wilke
 */
public interface IDeviceManager {

	/**
	 * Returns the {@link Device} for a given name.
	 * 
	 * @param name
	 *            The name of the {@link Device}.
	 * @return The {@link Device} or <code>null</code>.
	 * @throws Exception
	 */
	public Device getDevice(String name) throws Exception;

	/**
	 * Returns all existing {@link Device}s.
	 * 
	 * @return All existing {@link Device}s.
	 * @throws Exception
	 */
	public List<Device> getDevices() throws Exception;
}
