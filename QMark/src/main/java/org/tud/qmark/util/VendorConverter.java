package org.tud.qmark.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import org.tud.qmark.entities.Vendor;
import org.tud.qmark.interfaces.IVendorManager;

/**
 * {@link Converter} implementation to convert between {@link Vendor}s and their
 * IDs.
 * 
 * @author Claas Wilke
 */
@Named("vendorConverter")
public class VendorConverter implements Converter {

	/** The {@link IVendorManager} used to resolve {@link Vendor}s. */
	@Inject
	private IVendorManager vendorManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		try {
			return vendorManager.getVendor(arg2);
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

		if (arg2 instanceof Vendor)
			return ((Vendor) arg2).getName();

		else
			return "";
	}
}
