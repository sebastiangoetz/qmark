package org.tud.qmark.impl;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Named;

import org.tud.qmark.interfaces.ICacheManager;

@Named("cacheManager")
@Stateless
@Local(ICacheManager.class)
public class EJBCacheManager implements ICacheManager
{
    @Resource(lookup = "java:jboss/infinispan/QCache")
    private org.infinispan.manager.CacheContainer container;
    private org.infinispan.Cache<String, Object> cache;

    @PostConstruct
    public void start()
    {
        this.cache = this.container.getCache();
    }

    /* (non-Javadoc)
     * @see org.tud.qmark.impl.ICacheManager2#resetCache(java.lang.Object)
     */
    @Override
    public void resetCache()
    {
        cache.clear();
        System.out.println("Cache cleared");
    }

    /* (non-Javadoc)
     * @see org.tud.qmark.impl.ICacheManager2#putObjectforID(java.lang.Object, java.lang.String)
     */
    @Override
    public void putObjectforID(String id, Object obj)
    {
        if(cache != null && obj != null && id != null && id.length() > 0)
        {
            if(cache.containsKey(id))
            {
                cache.remove(id);
            }
            cache.put(id, obj);
        }
    }

    /* (non-Javadoc)
     * @see org.tud.qmark.impl.ICacheManager2#getObjectForID(java.lang.String)
     */
    @Override
    public Object getObjectforID(String id)
    {
        if(cache != null && id != null && id.length() > 0)
        {
            Object ret = (Object)cache.get(id);
            return ret;
        }
        else
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.tud.qmark.impl.ICacheManager2#removeObjectForID(java.lang.Object, java.lang.String)
     */
    @Override
    public void removeObjectforID(String id)
    {
        if(cache != null && id != null && id.length() > 0)
        {
            if(cache.containsKey(id))
            {
                cache.remove(id);
            }
            else
            {
                return;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.tud.qmark.impl.ICacheManager2#putObjectsforMethod(java.lang.Object, java.lang.String, java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void putObjectsforMethodandID(String method, String id, Object obj)
    {
        if(cache != null && obj != null && id != null && method != null && id.length() > 0
                && method.length() > 0)
        {
            HashMap<String, Object> methodCache = null;
            if(cache.containsKey(method))
            {
                methodCache = (HashMap<String, Object>)cache.get(method);
                if(methodCache.containsKey(id))
                {
                    methodCache.remove(id);
                }
                methodCache.put(id, obj);
                cache.remove(method);
            }
            else
            {
                methodCache = new HashMap<String, Object>();
                methodCache.put(id, obj);
            }
            cache.put(method, methodCache);
        }
    }

    /* (non-Javadoc)
     * @see org.tud.qmark.impl.ICacheManager2#getObjectForIDandMethod(java.lang.String, java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getObjectsforMethodandID(String method, String id)
    {
        if(cache != null && id != null && method != null && id.length() > 0 && method.length() > 0)
        {
            HashMap<String, Object> methodCache = null;
            if(cache.containsKey(method))
            {
                methodCache = (HashMap<String, Object>)cache.get(method);
                if(methodCache.containsKey(id))
                {
                    return methodCache.get(id);
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.tud.qmark.impl.ICacheManager2#removeObjectforMethod(java.lang.String)
     */
    @Override
    public void removeObjectforMethod(String method)
    {
        if(cache != null)
        {
            if(cache.containsKey(method))
            {
                cache.remove(method);
            }
            else
            {
                return;
            }
        }
    }

    @Override
    public boolean existsObjectforID(String id)
    {
        if(cache != null && id != null && id.length() > 0 && cache.containsKey(id))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean existsObjectsforMethodandID(String method, String id)
    {
        if(cache != null && id != null && method != null && id.length() > 0 && method.length() > 0)
        {
            HashMap<String, Object> methodCache = null;
            if(cache.containsKey(method))
            {
                methodCache = (HashMap<String, Object>)cache.get(method);
                if(methodCache.containsKey(id))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
