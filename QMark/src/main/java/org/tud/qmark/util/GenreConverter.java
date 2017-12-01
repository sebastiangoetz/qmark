package org.tud.qmark.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import org.tud.qmark.entities.Genre;
import org.tud.qmark.interfaces.IGenreManager;

/**
 * {@link Converter} implementation to convert between {@link Genre}s and their
 * IDs.
 * 
 * @author Claas Wilke
 */
@Named("genreConverter")
public class GenreConverter implements Converter {

	/** The {@link IGenreManager} used to resolve {@link Genre}s. */
	@Inject
	private IGenreManager genreManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		try {
			return genreManager.getGenre(arg2);
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

		if (arg2 instanceof Genre)
			return ((Genre) arg2).getName();

		else
			return "";
	}
}
