package org.tud.qmark.interfaces;


public interface IClientManager
{
    public String JNDI_REMOTE_NAME = IClientManager.class.getSimpleName();
    public String JNDI_BEAN_NAME = "EJBClientManager";

    public String getInfo2();
    public String getInfo();
}
