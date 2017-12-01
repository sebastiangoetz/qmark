package org.tud.qmark.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import org.tud.qmark.entities.Type;
import org.tud.qmark.entities.Vendor;
import org.tud.qmark.interfaces.ITypeManager;
import org.tud.qmark.interfaces.IVendorManager;

/**
 * {@link Converter} implementation to convert between {@link Type}s and their
 * names.
 * 
 * @author Claas Wilke
 */
@Named("typeConverter")
public class TypeConverter implements Converter {

	/** The {@link IVendorManager} used to resolve {@link Vendor}s. */
	@Inject
	private ITypeManager typeManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		try {
			return typeManager.getType(arg2);
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

		if (arg2 instanceof Type)
			return ((Type) arg2).getName();

		else
			return "";
	}
}
