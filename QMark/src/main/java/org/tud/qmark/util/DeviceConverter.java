package org.tud.qmark.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import org.tud.qmark.entities.Device;
import org.tud.qmark.interfaces.IDeviceManager;

/**
 * {@link Converter} implementation to convert between {@link Device}s and their
 * IDs.
 * 
 * @author Claas Wilke
 */
@Named("deviceConverter")
public class DeviceConverter implements Converter {

	/** The {@link IDevieManager} used to resolve {@link Devices}s. */
	@Inject
	private IDeviceManager deviceManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		try {
			return deviceManager.getDevice(arg2);
		}

		catch (Exception e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.Object)
	 */
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		if (arg2 instanceof Device)
			return ((Device) arg2).getName();

		else
			return "";
	}
}
