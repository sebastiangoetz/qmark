package org.tud.qmark.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.tud.qmark.entities.App;
import org.tud.qmark.entities.Member;
import org.tud.qmark.entities.Type;
import org.tud.qmark.entities.User;
import org.tud.qmark.interfaces.IMemberManager;
import org.tud.qmark.interfaces.QMarkSessionBean;

/**
 * EJB manager implementation of {@link IMemberManager}.
 * 
 * @author Claas Wilke
 */
@Named("memberManager")
@Stateful
@RequestScoped
public class EJBMemberManager extends QMarkSessionBean implements IMemberManager
{

    /** The {@link Logger} used. */
    @Inject
    private transient Logger logger;

    /** The {@link EntityManager} used. */
    @Inject
    private EntityManager memberDatabase;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IMemberManager#addMember(org.tud.qmark.entities
     * .Member)
     */
    public String addMember(Member member) throws Exception
    {
        memberDatabase.persist(member);
        logger.info("Added " + member);
        return "memberAdded";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IMemberManager#isMemberOfType(org.tud.qmark.
     * entities.App, org.tud.qmark.entities.User, org.tud.qmark.entities.Type)
     */
    @SuppressWarnings("unchecked")
    public boolean isMemberOfType(App app, User user, Type type)
    {
        if(!cache.existsObjectsforMethodandID("isMemberOfType", app.getAppID().toString()
                + user.getUserID().toString() + type.getTypeID().toString()))
        {
            List<Member> matchingMembers = memberDatabase
                    .createQuery("select m from Member m where m.user=:user and m.app=:app and m.type=:type")
                    .setParameter("user", user).setParameter("app", app).setParameter("type", type)
                    .getResultList();

            boolean b = (matchingMembers.size() > 0);
            cache.putObjectsforMethodandID("isMemberOfType", app.getAppID().toString()
                    + user.getUserID().toString() + type.getTypeID().toString(), new Boolean(b));
            return b;
        }
        else
        {
            return ((Boolean)cache.getObjectsforMethodandID("isMemberOfType", app.getAppID().toString()
                    + user.getUserID().toString() + type.getTypeID().toString())).booleanValue();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IMemberManager#isMember(org.tud.qmark.entities
     * .App, org.tud.qmark.entities.User)
     */
    @SuppressWarnings("unchecked")
    public boolean isMember(App app, User user)
    {
        if(!cache.existsObjectsforMethodandID("isMember", app.getAppID().toString()
                + user.getUserID().toString()))
        {
            List<Member> matchingMembers = memberDatabase
                    .createQuery("select m from Member m where m.user=:user and m.app=:app")
                    .setParameter("user", user).setParameter("app", app).getResultList();

            boolean b = (matchingMembers.size() > 0);
            cache.putObjectsforMethodandID("isMember", app.getAppID().toString()
                    + user.getUserID().toString(), new Boolean(b));
            return b;
        }
        else
        {
            return ((Boolean)cache.getObjectsforMethodandID("isMember", app.getAppID().toString()
                    + user.getUserID().toString())).booleanValue();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tud.qmark.interfaces.IMemberManager#updateMember(org.tud.qmark.entities
     * .Member)
     */
    public String updateMember(Member member) throws Exception
    {
        memberDatabase.merge(member);
        logger.info("Updated " + member);
        resetCache();
        return "memberUpdated";
    }

    protected void resetCache()
    {
        cache.removeObjectforMethod("isMemberOfType");
        cache.removeObjectforMethod("isMember");
    }
}
