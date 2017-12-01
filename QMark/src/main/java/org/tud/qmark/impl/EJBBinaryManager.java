package org.tud.qmark.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.hibernate.engine.jdbc.NonContextualLobCreator;
import org.tud.qmark.entities.Binary;
import org.tud.qmark.interfaces.IBinaryManager;

@Named("binaryManager")
@Stateless
@Remote(IBinaryManager.class)
public class EJBBinaryManager implements IBinaryManager
{

    @Inject
    private transient Logger logger;

    @Inject
    private EntityManager entityManager;

    @Override
    public List<Binary> getBinaries() throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Binary getBinaryByID(Long id) throws Exception
    {
        Binary binary = entityManager.find(Binary.class, id);
        return binary;
    }

    @Override
    public Binary createBinary(Binary binary) throws Exception
    {
        NonContextualLobCreator lobCreator = NonContextualLobCreator.INSTANCE;
        //binary.setContent(lobCreator.createBlob(binary.getRealContentfromBlob()));
        entityManager.persist(binary);
        logger.info("Created " + binary.getBinaryID());
        return binary;
    }

    @Override
    public Binary updateBinary(Binary binary) throws Exception
    {
        Binary oldBinary = entityManager.find(Binary.class, binary.getBinaryID());
        oldBinary.setContent(binary.getContent());
        return oldBinary;
    }

    @Override
    public void deleteBinary(Long id) throws Exception
    {
        Binary binary = getBinaryByID(id);
        if(binary != null)
        {
            entityManager.remove(binary);
        }
    }

}
