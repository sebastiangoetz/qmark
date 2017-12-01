/*
 * Created on 24.10.2012
 */

package org.tud.qmark.impl;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.tud.qmark.entities.CompanyMember;
import org.tud.qmark.entities.Member;
import org.tud.qmark.entities.Type;
import org.tud.qmark.entities.User;
import org.tud.qmark.entities.UserMetaData;
import org.tud.qmark.interfaces.ITypeManager;
import org.tud.qmark.interfaces.IUserManager;
import org.tud.qmark.interfaces.QMarkSessionBean;
import org.tud.qmark.util.HashUtil;
import org.tud.qmark.util.PasswordHash;
import org.tud.qmark.util.TypeConstants;

/**
 * EJB manager implementation for {@link IUserManager}.
 * 
 * @author Sebastian Richy - maquiz@googlemail.com
 * @author Claas Wilke
 * @author Sebastian Götz - sebastian.goetz@acm.org 
 */
@Named("userManager")
@Stateful
@RequestScoped
public class EJBUserManager extends QMarkSessionBean implements IUserManager
{

    /** The {@link Logger} used. */
    @Inject
    private transient Logger logger;

    /** The {@link EntityManager} used. */
    @Inject
    private EntityManager userDatabase;
    
    @Inject
    private ITypeManager typeManager;

    /*
     * (non-Javadoc)
     * 
     * @see org.tud.qmark.interfaces.IUserManager#findUser(java.lang.String,
     * java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public User findUser(String login, String password) throws Exception
    {

        List<User> results = null;
        if(!cache.existsObjectsforMethodandID("findUser", login))
        {
            results = userDatabase.createQuery("select u from User u where u.login=:login")
                    .setParameter("login", login).getResultList();
            cache.putObjectsforMethodandID("findUser", login, results);
        }
        else
        {
            results = (List<User>)cache.getObjectsforMethodandID("findUser", login);
        }
        if(results.isEmpty())
        {
            return null;
        }
        else if(results.size() > 1)
        {
            throw new IllegalStateException("Cannot have more than one user with the same username!");
        }
        else
        {
            User user = results.get(0);
            if(HashUtil.checkHash(password, user.getSalt(), user.getPassword()))
                return results.get(0);
            else
                return null;
        }
    }

    /*
     * (non-Javadoc) Documentation for right salting:
     * https://crackstation.net/hashing-security.htm
     * 
     * @see
     * org.tud.qmark.interfaces.IUserManager#addUser(org.tud.qmark.entities.
     * User)
     */
    public String addUser(User user) throws Exception
    {
        userDatabase.persist(user.getUserMetaData());
        PasswordHash ph = HashUtil.generateHash(user.getPassword());
        user.setPassword(ph.hash);
        user.setSalt(ph.salt);
        user.setStatus(typeManager.getType(TypeConstants.USER_TYPE_REGISTERED));
        userDatabase.persist(user);
        logger.info("Added " + user);
        resetCache();
        return "userAdded";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IUserManager#getAppMembers(org.tud.qmark.entities
     * .User)
     */
    @SuppressWarnings("unchecked")
    public List<Member> getAppMembers(User user) throws Exception
    {

        if(null == user) return Collections.emptyList();
        List<Member> results = null;
        if(!cache.existsObjectsforMethodandID("getAppMembers", user.getLogin()))
        {
            results = userDatabase
                    .createQuery(
                            "select m from User u, Member m, App a where u.userID=:id and m.user = u and m.app = a order by a.name")
                    .setParameter("id", user.getUserID()).getResultList();
            cache.putObjectsforMethodandID("getAppMembers", user.getLogin(), results);
        }
        else
        {
            results = (List<Member>)cache.getObjectsforMethodandID("getAppMembers", user.getLogin());
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IUserManager#getCompanyMembers(org.tud.qmark
     * .entities.User)
     */
    @SuppressWarnings("unchecked")
    public List<CompanyMember> getCompanyMembers(User user) throws Exception
    {

        if(null == user) return Collections.emptyList();
        // no else.
        List<CompanyMember> results = null;
        if(!cache.existsObjectsforMethodandID("getCompanyMembers", user.getLogin()))
        {
            results = userDatabase
                    .createQuery(
                            "select m from User u, CompanyMember m where u.userID=:id and u.userMetaData = m.userMetaData")
                    .setParameter("id", user.getUserID()).getResultList();
            cache.putObjectsforMethodandID("getCompanyMembers", user.getLogin(), results);
        }
        else
        {
            results = (List<CompanyMember>)cache.getObjectsforMethodandID("getCompanyMembers",
                    user.getLogin());
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tud.qmark.interfaces.IUserManager#getUser(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public User getUser(String mail) throws Exception
    {
        if(mail == null) return null;
        if(!cache.existsObjectsforMethodandID("getUser", mail))
        {
            List<User> results = userDatabase.createQuery("select u from User u where u.emailaddress=:mail")
                    .setParameter("mail", mail).getResultList();
            if(results.isEmpty())
            {
                cache.putObjectsforMethodandID("getUser", mail, null);
                return null;
            }
            else if(results.size() > 1)
            {
                throw new IllegalStateException("Cannot have more than one user with the same mail address!");
            }
            else
            {
                cache.putObjectsforMethodandID("getUser", mail, results.get(0));
                return results.get(0);
            }
        }
        else
        {
            return (User)cache.getObjectsforMethodandID("getUser", mail);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.tud.qmark.interfaces.IUserManager#getUser(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<User> getUserRegistrations() throws Exception
    {
            List<User> results = userDatabase.createQuery("select u from User u, Type t where u.status = t.typeID and t.name = 'registered'")
                    .getResultList();
            if(results.isEmpty())
            {
                return null;
            }
            else
            {
                return results;
            }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IUserManager#getUser(org.tud.qmark.entities.
     * UserMetaData)
     */
    @SuppressWarnings("unchecked")
    public User getUserFromMetaData(UserMetaData metaData) throws Exception
    {
        if(!cache.existsObjectsforMethodandID("getUserFromMetaData", metaData.getUserMetaDataID()+""))
        {
            List<User> results = userDatabase
                    .createQuery("select u from User u where u.userMetaData=:metaData")
                    .setParameter("metaData", metaData).getResultList();
            if(results.isEmpty())
            {
                cache.putObjectsforMethodandID("getUserFromMetaData", metaData.getUserMetaDataID()+"", null);
                return null;
            }
            else if(results.size() > 1)
            {
                throw new IllegalStateException("Cannot have more than one user with the same metaData!");
            }
            else
            {
                cache.putObjectsforMethodandID("getUserFromMetaData", metaData.getUserMetaDataID()+"", results.get(0));                
                return results.get(0);
            }
        }
        else
        {
            return (User)cache.getObjectsforMethodandID("getUserFromMetaData", metaData.getUserMetaDataID()+"");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IUserManager#updateUser(org.tud.qmark.entities
     * .User)
     */
    public String updateUser(User user) throws Exception
    {
        PasswordHash ph = HashUtil.generateHash(user.getPassword());
        user.setPassword(ph.hash);
        user.setSalt(ph.salt);
        userDatabase.merge(user);
        userDatabase.merge(user.getUserMetaData());
        userDatabase.merge(user.getStatus());
        logger.info("Updated " + user+" ("+user.getStatus().getName()+")");
        resetCache();
        return "userUpdated";
    }
    
    protected void resetCache()
    {
        cache.removeObjectforMethod("findUser");
        cache.removeObjectforMethod("getAppMembers");
        cache.removeObjectforMethod("getCompanyMembers");
        cache.removeObjectforMethod("getUser");
        cache.removeObjectforMethod("getUserFromMetaData");
    }
}
