package org.tud.qmark.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.tud.qmark.entities.EnergyModel;
import org.tud.qmark.entities.PowerRate;
import org.tud.qmark.entities.TestCase;
import org.tud.qmark.entities.TestRun;
import org.tud.qmark.entities.Type;
import org.tud.qmark.entities.Version;
import org.tud.qmark.interfaces.IEnergyModelManager;
import org.tud.qmark.interfaces.ITestSuiteManager;
import org.tud.qmark.interfaces.ITypeManager;
import org.tud.qmark.interfaces.QMarkSessionBean;

/**
 * EJB manager implementation of {@link IEnergyModelManager}.
 * 
 * @author Claas Wilke
 */
@Named("energyModelManager")
@Stateless
@Remote(IEnergyModelManager.class)
public class EJBEnergyModelManager extends QMarkSessionBean implements IEnergyModelManager
{

    /** The {@link Logger} used. */
    @Inject
    private transient Logger logger;

    /** The {@link EntityManager} used. */
    @Inject
    private EntityManager entityManager;

    @EJB
    private ITypeManager typeManager;
    @EJB
    private ITestSuiteManager testSuiteManager;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IEnergyModelManager#clearEnergyModel(org.tud
     * .qmark.entities.Version)
     */
    public void clearEnergyModel(Version version) throws Exception
    {

        EnergyModel energyModel = version.getEnergyModel();
        if(null != energyModel)
        {

            Set<PowerRate> powerRates = energyModel.getPowerRates();

            if(null != powerRates)
            {
                for(PowerRate rate : powerRates)
                {
                    // Power rates should be deleted instead.
                    if(null != rate.getPowerRateID()) rate.setValue(null);
                    entityManager.merge(rate);
                    // no else.
                }
                // end for.

                powerRates.clear();
            }

            else
            {
                powerRates = new HashSet<PowerRate>();
            }

            energyModel.setPowerRates(powerRates);

            if(null != energyModel.getEnergyModelID())
                entityManager.merge(energyModel);
            else
                entityManager.persist(energyModel);
        }
        // no else.

        entityManager.merge(version);

        logger.info("Updated " + version);
        resetCache();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IEnergyModelManager#getTestCases(org.tud.qmark
     * .entities.TestRun)
     */
    public List<TestCase> getTestCases(TestRun testRun) throws Exception
    {
        return testSuiteManager.getTestCases(testRun);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tud.qmark.interfaces.IEnergyModelManager#getTestRun(long)
     */
    public TestRun getTestRun(long testRunID) throws Exception
    {
        return testSuiteManager.getTestRun(testRunID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IEnergyModelManager#getType(java.lang.String)
     */
    public Type getType(String name) throws Exception
    {
        return typeManager.getType(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IEnergyModelManager#updateTestRun(org.tud.qmark
     * .entities.TestRun)
     */
    public void updateTestRun(TestRun testRun) throws Exception
    {

        entityManager.merge(testRun);
        resetCache();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IEnergyModelManager#updateVersion(org.tud.qmark
     * .entities.Version)
     */
    public void updateVersion(Version version) throws Exception
    {

        EnergyModel energyModel = version.getEnergyModel();
        if(null != energyModel)
        {

            for(PowerRate rate : energyModel.getPowerRates())
            {
                if(null != rate.getPowerRateID())
                    entityManager.merge(rate);
                else
                    entityManager.persist(rate);
            }
            // end for.

            if(null != energyModel.getEnergyModelID())
                entityManager.merge(energyModel);
            else
                entityManager.persist(energyModel);
        }
        // no else.

        entityManager.merge(version);

        logger.info("Updated " + version);
        resetCache();
    }

    protected void resetCache()
    {
        cache.removeObjectforMethod("getGenre");
        cache.removeObjectforMethod("getGenres");
        cache.removeObjectforMethod("getGenresByName");
    }
}
