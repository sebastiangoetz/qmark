package org.tud.qmark.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.tud.qmark.entities.App;
import org.tud.qmark.entities.Genre;
import org.tud.qmark.entities.Member;
import org.tud.qmark.entities.Transition;
import org.tud.qmark.interfaces.IAppManager;
import org.tud.qmark.interfaces.QMarkSessionBean;
import org.tud.qmark.util.AppOrder;

/**
 * EJB manager implementation of {@link IAppManager}.
 * 
 * @author Claas Wilke
 */
@Named("appManager")
@Local(IAppManager.class)
@RequestScoped
public class EJBAppManager extends QMarkSessionBean implements IAppManager
{

    /** The {@link Logger} used. */
    @Inject
    private transient Logger logger;

    /** The {@link EntityManager} used. */
    @Inject
    private EntityManager appDatabase;

    /** The current {@link UserTransaction}. */
    @Inject
    private UserTransaction utx;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IAppManager#addApp(org.tud.qmark.entities.App)
     */
    public String addApp(App app) throws Exception
    {
        try
        {
            try
            {
                utx.begin();
                appDatabase.persist(app);
                logger.info("Added " + app);
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
        resetCache();
        return "appAdded";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qmark.AppManager#getApps()
     */
    @SuppressWarnings("unchecked")
    @Named
    public List<App> getApps() throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getApps", "1"))
        {
            try
            {
                try
                {
                    utx.begin();
                    List<App> apps = appDatabase.createQuery("select a from App a").getResultList();
                    cache.putObjectsforMethodandID("getApps", "1", apps);
                    return apps;
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
            return (List<App>)cache.getObjectsforMethodandID("getApps", "1");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qmark.AppManager#getAppsOfGenreByConsumption(org.qmark.Genre,
     * java.util.Map, org.qmark.AppOrder, int, int)
     */
    @SuppressWarnings("unchecked")
    public List<App> getAppsOfGenreByConsumption(Genre genre, Map<Transition, Integer> weights,
            AppOrder order, int start, int limit) throws Exception
    {
        if(null == order) order = AppOrder.ENERGY_ASC;

        String key = "";
        for(Transition transtion : weights.keySet())
        {
            key = key + transtion.getTransitionID().toString() + "+" + weights.get(transtion).toString()
                    + "+";
        }
        if(!cache.existsObjectsforMethodandID("getAppsOfGenreByConsumption", genre.getGenreID().toString()
                + key + order.toString() + start + limit))
        {
            try
            {
                try
                {
                    utx.begin();

                    if(start < 0) start = 0;
                    // no else.

                    if(limit < 0) limit = 0;
                    // no else.

                    StringBuffer query = new StringBuffer();
                    query.append("SELECT * FROM (\n");
                    query.append("SELECT a.appID,\n");

                    if(weights.keySet().size() > 0)
                    {
                        query.append("    SUM(p.value * CASE\n");
                        for(Transition transtion : weights.keySet())
                        {
                            query.append("        WHEN p.transitionID = " + transtion.getTransitionID()
                                    + " THEN " + weights.get(transtion).intValue() + "\n");
                        }
                        // end for.
                        query.append("        ELSE 0\n");
                        query.append("    END) AS powerrate\n");
                    }
                    else
                    {
                        query.append("    SUM(p.value) AS powerrate\n");
                    }

                    query.append("FROM app a, genre g, energymodel e, energymodeltopowerrate ep, powerrate p, version v,\n");
                    query.append("    (\n");
                    query.append("        SELECT max(versionID) as versionID, appID\n");
                    query.append("        FROM version v, type t\n");
                    query.append("        WHERE v.visibilityTypeID = t.typeID\n");
                    query.append("            AND t.name = 'versionReleased'\n");
                    query.append("        GROUP BY appID\n");
                    query.append("    ) newestVersion\n");
                    query.append("WHERE a.genreID = g.genreID\n");
                    query.append("    AND g.genreID = " + genre.getGenreID() + "\n");
                    query.append("    AND a.appID = newestVersion.appID\n");
                    query.append("    AND newestVersion.versionID = v.versionID\n");
                    query.append("    AND v.energyModelID = e.energyModelID\n");
                    query.append("    AND e.energyModelID = ep.energyModelID\n");
                    query.append("    AND ep.powerRateID = p.powerRateID\n");
                    query.append("GROUP BY a.appID\n");
                    query.append(") innertable\n");

                    switch(order)
                    {
                        case ENERGY_ASC:
                            query.append("ORDER BY (CASE WHEN (powerrate IS NULL) THEN 65000 ELSE powerrate END) ASC\n");
                            break;
                        case ENERGY_DESC:
                            query.append("ORDER BY (CASE WHEN (powerrate IS NULL) THEN 65000 ELSE powerrate END) DESC\n");
                            break;
                        case RATING_ASC:
                            query.append("ORDER BY rating ASC\n");
                            break;
                        case RATING_DESC:
                            query.append("ORDER BY rating DESC\n");
                            break;
                    // no default.
                    }

                    if(limit > 0)
                    {
                        query.append("OFFSET " + start + " ROWS\n");
                        query.append("FETCH NEXT " + limit + " ROWS ONLY");
                    }
                    // no else.
                    List<Object[]> results = appDatabase.createNativeQuery(query.toString()).getResultList();

                    if(results.size() > 0)
                    {

                        List<App> result = new ArrayList<App>(results.size());

                        for(Object[] anObject : results)
                        {
                            App anApp = appDatabase.find(App.class, ((BigInteger)anObject[0]).longValue());

                            if(null != anApp) result.add(anApp);
                            // no else.
                        }
                        // end for.
                        cache.putObjectsforMethodandID("getAppsOfGenreByConsumption", genre.getGenreID()
                                .toString() + key + order.toString() + start + limit, result);
                        return result;
                    }

                    else
                        return Collections.emptyList();
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
            return (List<App>)cache.getObjectsforMethodandID("getAppsOfGenreByConsumption", genre
                    .getGenreID().toString() + key + order.toString() + start + limit);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qmark.AppManager#getEnergyRankOfApp(org.qmark.App,
     * org.qmark.Genre, java.util.Map)
     */
    public int getEnergyRankOfApp(App app, Genre genre, Map<Transition, Integer> weights) throws Exception
    {
        String key = "";
        for(Transition transtion : weights.keySet())
        {
            key = key + transtion.getTransitionID().toString() + "+" + weights.get(transtion).toString()
                    + "+";
        }
        if(!cache.existsObjectsforMethodandID("getEnergyRankOfApp", app.getAppID().toString() + "+"
                + genre.getGenreID().toString() + key))
        {
            try
            {
                try
                {
                    utx.begin();

                    StringBuffer query = new StringBuffer();
                    query.append("SELECT COUNT(*) + 1\n");
                    query.append("FROM\n");
                    query.append("\n");
                    query.append("    (SELECT a.name,\n");

                    if(weights.keySet().size() > 0)
                    {
                        query.append("        SUM(p.value * \n");
                        query.append("            CASE\n");
                        for(Transition transtion : weights.keySet())
                        {
                            query.append("                WHEN p.transitionID = "
                                    + transtion.getTransitionID() + "         THEN "
                                    + weights.get(transtion).intValue() + "\n");
                        }
                        // end for.
                        query.append("                ELSE 0\n");
                        query.append("            END) / SUM(\n");
                        query.append("            CASE\n");
                        for(Transition transtion : weights.keySet())
                        {
                            query.append("                WHEN p.transitionID = "
                                    + transtion.getTransitionID() + "         THEN "
                                    + weights.get(transtion).intValue() + "\n");
                        }
                        // end for.
                        query.append("                ELSE 0\n");
                        query.append("            END) AS powerrate\n");
                    }
                    else
                    {
                        query.append("        SUM(p.value) AS powerrate\n");
                    }

                    query.append("    FROM app a, genre g, energymodel e, energymodeltopowerrate ep, powerrate p, version v,\n");
                    query.append("        (\n");
                    query.append("            SELECT max(versionID) as versionID, appID\n");
                    query.append("            FROM version v, type t\n");
                    query.append("            WHERE v.visibilityTypeID = t.typeID\n");
                    query.append("                AND t.name = 'versionReleased'\n");
                    query.append("            GROUP BY appID\n");
                    query.append("        ) newestVersion\n");
                    query.append("    WHERE a.genreID = g.genreID\n");
                    query.append("        AND g.genreID = " + genre.getGenreID() + "\n");
                    query.append("        AND a.appID = newestVersion.appID\n");
                    query.append("        AND newestVersion.versionID = v.versionID\n");
                    query.append("        AND v.energyModelID = e.energyModelID\n");
                    query.append("        AND e.energyModelID = ep.energyModelID\n");
                    query.append("        AND ep.powerRateID = p.powerRateID\n");
                    query.append("    GROUP BY a.appID\n");
                    query.append("    ORDER BY powerrate ASC) powerrates,\n");
                    query.append("\n");
                    query.append("    (SELECT \n");

                    if(weights.keySet().size() > 0)
                    {
                        query.append("        SUM(p.value * \n");
                        query.append("            CASE\n");
                        for(Transition transtion : weights.keySet())
                        {
                            query.append("                WHEN p.transitionID = "
                                    + transtion.getTransitionID() + "         THEN "
                                    + weights.get(transtion).intValue() + "\n");
                        }
                        // end for.
                        query.append("                ELSE 0\n");
                        query.append("            END) / SUM(\n");
                        query.append("            CASE\n");
                        for(Transition transtion : weights.keySet())
                        {
                            query.append("                WHEN p.transitionID = "
                                    + transtion.getTransitionID() + "         THEN "
                                    + weights.get(transtion).intValue() + "\n");
                        }
                        // end for.
                        query.append("                ELSE 0\n");
                        query.append("            END) AS powerrate\n");
                    }
                    else
                    {
                        query.append("        SUM(p.value) AS powerrate\n");
                    }

                    query.append("    FROM app a, energymodel e, energymodeltopowerrate ep, powerrate p, version v,\n");
                    query.append("        (\n");
                    query.append("            SELECT max(versionID) as versionID, appID\n");
                    query.append("            FROM version v, type t\n");
                    query.append("            WHERE v.visibilityTypeID = t.typeID\n");
                    query.append("                AND t.name = 'versionReleased'\n");
                    query.append("            GROUP BY appID\n");
                    query.append("        ) newestVersion\n");
                    query.append("    WHERE a.name =  '" + app.getName() + "'\n");
                    query.append("        AND a.appID = newestVersion.appID\n");
                    query.append("        AND newestVersion.versionID = v.versionID\n");
                    query.append("        AND v.energyModelID = e.energyModelID\n");
                    query.append("        AND e.energyModelID = ep.energyModelID\n");
                    query.append("        AND ep.powerRateID = p.powerRateID\n");
                    query.append("    GROUP BY a.appID) apprate\n");
                    query.append("\n");
                    query.append("WHERE powerrates.powerrate < apprate.powerrate");

                    @SuppressWarnings("unchecked")
                    List<BigInteger> results = appDatabase.createNativeQuery(query.toString())
                            .getResultList();

                    if(results.size() > 0)
                    {
                        cache.putObjectsforMethodandID("getEnergyRankOfApp", app.getAppID().toString() + "+"
                                + genre.getGenreID().toString() + key, results.get(0));
                        return results.get(0).intValue();
                    }
                    else
                    {
                        cache.putObjectsforMethodandID("getEnergyRankOfApp", app.getAppID().toString() + "+"
                                + genre.getGenreID().toString() + key, new Integer(-1));
                        return -1;
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
            return ((Integer)cache.getObjectsforMethodandID("getEnergyRankOfApp", app.getAppID().toString()
                    + "+" + genre.getGenreID().toString() + key)).intValue();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IAppManager#getMembers(org.tud.qmark.entities
     * .App)
     */
    @SuppressWarnings("unchecked")
    public List<Member> getMembers(App app) throws Exception
    {
        if(null == app) return Collections.emptyList();
        // no else.

        if(!cache.existsObjectsforMethodandID("getMembers", app.getAppID().toString()))
        {
            try
            {
                try
                {
                    utx.begin();
                    List<Member> results = appDatabase.createQuery("select m from Member m where m.app=:app")
                            .setParameter("app", app).getResultList();
                    cache.putObjectsforMethodandID("getMembers", app.getAppID().toString(), results);
                    return results;
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
            return (List<Member>)cache.getObjectsforMethodandID("getMembers", app.getAppID().toString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IAppManager#getNumberOfAppsInGenre(org.tud.qmark
     * .entities.Genre)
     */
    @SuppressWarnings("unchecked")
    public int getNumberOfAppsInGenre(Genre genre) throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getNumberOfAppsInGenre", genre.getGenreID().toString()))
        {
            try
            {
                try
                {
                    utx.begin();

                    List<BigInteger> results = appDatabase
                            .createNativeQuery(
                                    "SELECT COUNT(*) FROM app a, version v, type t WHERE v.appID = a.appID AND v.visibilityTypeID = t.typeID AND v.energyModelID IS NOT NULL AND t.name = 'versionReleased' AND genreID = '"
                                            + genre.getGenreID() + "'").getResultList();

                    if(results.size() > 0)
                    {
                        cache.putObjectsforMethodandID("getNumberOfAppsInGenre", genre.getGenreID()
                                .toString(), results.get(0));
                        return results.get(0).intValue();
                    }
                    else
                    {
                        cache.putObjectsforMethodandID("getNumberOfAppsInGenre", genre.getGenreID()
                                .toString(), new BigInteger("0"));
                        return -0;
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
            return ((BigInteger)cache.getObjectsforMethodandID("getNumberOfAppsInGenre", genre.getGenreID()
                    .toString())).intValue();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qmark.AppManager#getPowerRateOfApp(org.qmark.App, java.util.Map)
     */
    @Override
    public double getPowerRateOfApp(App app, Map<Transition, Integer> weights) throws Exception
    {
        try
        {
            try
            {
                utx.begin();

                StringBuffer query = new StringBuffer();
                query.append("SELECT ");

                if(weights.keySet().size() > 0)
                {
                    query.append("SUM(p.value * \n");
                    query.append("    CASE\n");
                    for(Transition transtion : weights.keySet())
                    {
                        query.append("        WHEN p.transitionID = " + transtion.getTransitionID()
                                + " THEN " + weights.get(transtion).intValue() + "\n");
                    }
                    // end for.
                    query.append("        ELSE 0\n");
                    query.append("    END) / SUM(\n");
                    query.append("    CASE\n");
                    for(Transition transtion : weights.keySet())
                    {
                        query.append("        WHEN p.transitionID = " + transtion.getTransitionID()
                                + " THEN " + weights.get(transtion).intValue() + "\n");
                    }
                    // end for.
                    query.append("        ELSE 0\n");
                    query.append("    END) AS powerrate\n");
                }
                else
                {
                    query.append("SUM(p.value) AS powerrate\n");
                }

                query.append("FROM app a, energymodel e, energymodeltopowerrate ep, powerrate p, version v,\n");
                query.append("    (\n");
                query.append("        SELECT max(versionID) as versionID, appID\n");
                query.append("        FROM version v, type t\n");
                query.append("        WHERE v.visibilityTypeID = t.typeID\n");
                query.append("            AND t.name = 'versionReleased'\n");
                query.append("        GROUP BY appID\n");
                query.append("    ) newestVersion\n");
                query.append("WHERE a.name =  '" + app.getName() + "'\n");
                query.append("    AND a.appID = newestVersion.appID\n");
                query.append("    AND newestVersion.versionID = v.versionID\n");
                query.append("    AND v.energyModelID = e.energyModelID\n");
                query.append("    AND e.energyModelID = ep.energyModelID\n");
                query.append("    AND ep.powerRateID = p.powerRateID\n");
                query.append("GROUP BY a.appID\n");

                @SuppressWarnings("unchecked")
                List<Double> results = appDatabase.createNativeQuery(query.toString()).getResultList();

                if(results.size() > 0)
                    return results.get(0);
                else
                    return -1;
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IAppManager#updateApp(org.tud.qmark.entities
     * .App)
     */
    public void updateApp(App app) throws Exception
    {
        try
        {
            try
            {
                utx.begin();
                appDatabase.merge(app);
                logger.info("Updated " + app);
            }
            finally
            {
                resetCache();
                utx.commit();
            }
        }
        catch(Exception e)
        {
            utx.rollback();
            throw e;
        }
    }

    protected void resetCache()
    {
        cache.removeObjectforMethod("getDevices");
        cache.removeObjectforMethod("getDevice");
    }
}
