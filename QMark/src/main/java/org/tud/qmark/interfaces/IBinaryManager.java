package org.tud.qmark.interfaces;

import java.util.List;

import org.tud.qmark.entities.Binary;

/**
 * @author Sebastian Richy - maquiz@googlemail.com
 * 
 */

public interface IBinaryManager
{
    public String JNDI_REMOTE_NAME = IBinaryManager.class.getSimpleName();
    public String JNDI_BEAN_NAME = "EJBBinaryManager";
    
    public List<Binary> getBinaries() throws Exception;
    
    public Binary getBinaryByID(Long id) throws Exception;
    
    public Binary createBinary(Binary binary) throws Exception;
    
    public Binary updateBinary(Binary binary) throws Exception;
    
    public void deleteBinary(Long id) throws Exception;

}
