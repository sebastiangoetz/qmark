package org.tud.qmark.interfaces;

import java.util.List;
import java.util.Map;

import org.tud.qmark.entities.Genre;
import org.tud.qmark.util.GenreConverter;

/**
 * Manages {@link Genre} entities.
 * 
 * @author Claas Wilke
 */
public interface IGenreManager {

	/**
	 * Adds a new {@link Genre}.
	 * 
	 * @return A message indicating the result of the methods invocation.
	 * @throws Exception
	 */
	public String addGenre() throws Exception;

	/**
	 * Tries to delete the {@link Genre} identified by a given ID.
	 * 
	 * @param id
	 *            The ID of the {@link Genre}.
	 * @throws Exception
	 */
	public void deleteGenre(Genre genre) throws Exception;

	/**
	 * Tries to retrieve a {@link Genre} for the given ID.
	 * 
	 * @param name
	 *            The name of the {@link Genre}.
	 * @return The resolved {@link Genre} or <code>null</code>.
	 * @throws Exception
	 */
	public Genre getGenre(String name) throws Exception;

	/**
	 * Returns a {@link GenreConverter} to convert between {@link Genre}s and
	 * their IDs.
	 * 
	 * @return A {@link GenreConverter} to convert between {@link Genre}s and
	 *         their IDs.
	 */
	public GenreConverter getGenreConverter() throws Exception;

	/**
	 * Returns all existing {@link Genre}s.
	 * 
	 * @return All existing {@link Genre}s.
	 * @throws Exception
	 */
	public List<Genre> getGenres() throws Exception;

	/**
	 * Returns all existing {@link Genre}s in a map identified by their name.
	 * 
	 * @return All existing {@link Genre}s in a map identified by their name.
	 * @throws Exception
	 */
	public Map<String, Genre> getGenresByName() throws Exception;

	/**
	 * Returns a new empty {@link Genre}
	 * 
	 * @return The new {@link Genre}.
	 */
	public Genre getNewGenre();
}
