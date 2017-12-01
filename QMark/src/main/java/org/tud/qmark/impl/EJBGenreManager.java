package org.tud.qmark.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Remote;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.tud.qmark.entities.Genre;
import org.tud.qmark.interfaces.IGenreManager;
import org.tud.qmark.interfaces.QMarkSessionBean;
import org.tud.qmark.util.GenreConverter;

@Named("genreManager")
@Remote(IGenreManager.class)
@RequestScoped
public class EJBGenreManager extends QMarkSessionBean implements IGenreManager
{
    /** The {@link Logger} used. */
    @Inject
    private transient Logger logger;

    /** The {@link EntityManager} used. */
    @Inject
    private EntityManager genreDatabase;

    /**
     * {@link GenreConverter} used to convert between {@link Genre}s and their
     * IDs.
     */
    private GenreConverter myGenreConverter = new GenreConverter();

    /** A new, empty {@link Genre}. */
    private Genre newGenre = new Genre();

    /** The current {@link UserTransaction}. */
    @Inject
    private UserTransaction utx;

    /*
     * (non-Javadoc)
     * 
     * @see org.qmark.GenreManager#addGenre()
     */
    public String addGenre() throws Exception
    {
        try
        {
            try
            {
                utx.begin();
                genreDatabase.persist(newGenre);
                logger.info("Added " + newGenre);
            }
            finally
            {
                utx.commit();
                resetCache();
            }
        }
        catch(Exception e)
        {
            utx.rollback();
            throw e;
        }
        return "genreAdded";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IGenreManager#deleteGenre(org.tud.qmark.entities
     * .Genre)
     */
    public void deleteGenre(Genre genre) throws Exception
    {
        try
        {
            try
            {
                utx.begin();
                genreDatabase.createQuery("DELETE FROM Genre g WHERE g.nameID=:id")
                        .setParameter("id", genre.getGenreID()).executeUpdate();
            }
            finally
            {
                utx.commit();
                resetCache();
            }
        }
        catch(Exception e)
        {
            utx.rollback();
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qmark.GenreManager#getGenres()
     */
    public Genre getGenre(String name) throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getGenre", name))
        {
            try
            {
                try
                {
                    utx.begin();
                    @SuppressWarnings("unchecked")
                    List<Genre> results = genreDatabase
                            .createQuery("select g from Genre g where g.name=:name")
                            .setParameter("name", name).getResultList();
                    if(results.isEmpty())
                    {
                        cache.putObjectsforMethodandID("getGenre", name, null);
                        return null;
                    }
                    else if(results.size() > 1)
                    {
                        throw new IllegalStateException("Cannot have more than one genre with the same name!");
                    }
                    else
                    {
                        cache.putObjectsforMethodandID("getGenre", name, results.get(0));
                        return results.get(0);
                    }
                }
                finally
                {
                    utx.commit();
                }
            }
            catch(Exception e)
            {
                utx.rollback();
                throw e;
            }
        }
        else
        {
            return (Genre)cache.getObjectsforMethodandID("getGenre", name);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qmark.GenreManager#getGenresFromId()
     */
    public GenreConverter getGenreConverter() throws Exception
    {
        return myGenreConverter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qmark.GenreManager#getGenres()
     */
    @SuppressWarnings("unchecked")
    @Named
    public List<Genre> getGenres() throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getGenres", "1"))
        {
            try
            {
                try
                {
                    utx.begin();
                    List<Genre> l = genreDatabase.createQuery("select g from Genre g order by g.name").getResultList();
                    cache.putObjectsforMethodandID("getGenres", "1", l);
                    return l;
                }
                finally
                {
                    utx.commit();
                }
            }
            catch(Exception e)
            {
                utx.rollback();
                throw e;
            }
        }
        else
        {
            return (List<Genre>)cache.getObjectsforMethodandID("getGenres", "1");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tud.qmark.interfaces.IGenreManager#getGenresByName()
     */
    @SuppressWarnings("unchecked")
    @Named
    @Alternative
    public Map<String, Genre> getGenresByName() throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getGenresByName", "1"))
        {
            try
            {
                try
                {
                    utx.begin();
                    List<Genre> genres = genreDatabase.createQuery("select g from Genre g").getResultList();
                    Map<String, Genre> resultMap = new HashMap<String, Genre>(genres.size());

                    for(Genre genre : genres)
                        resultMap.put(genre.getName(), genre);
                    // end for.

                    cache.putObjectsforMethodandID("getGenresByName", "1", resultMap);
                    return resultMap;
                }
                finally
                {
                    utx.commit();
                }
            }
            catch(Exception e)
            {
                utx.rollback();
                throw e;
            }
        }
        else
        {
            return (Map<String, Genre>)cache.getObjectsforMethodandID("getGenresByName", "1");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qmark.GenreManager#getNewGenre()
     */
    @Named
    public Genre getNewGenre()
    {
        return newGenre;
    }

    protected void resetCache()
    {
        cache.removeObjectforMethod("getGenre");
        cache.removeObjectforMethod("getGenres");
        cache.removeObjectforMethod("getGenresByName");
    }
}
