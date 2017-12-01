package org.tud.qmark.interfaces;

public interface ICacheManager
{
    
    public String JNDI_REMOTE_NAME = IBinaryManager.class.getSimpleName();
    public String JNDI_BEAN_NAME = "EJBCacheManager";
    
    public abstract void resetCache();

    public abstract boolean existsObjectforID(String id);
    
    public abstract void putObjectforID(String id, Object obj);

    public abstract Object getObjectforID(String id);

    public abstract void removeObjectforID(String id);

    public abstract boolean existsObjectsforMethodandID(String method, String id);
    
    public abstract void putObjectsforMethodandID(String method, String id, Object obj);

    public abstract Object getObjectsforMethodandID(String method, String id);

    public abstract void removeObjectforMethod(String method);

}