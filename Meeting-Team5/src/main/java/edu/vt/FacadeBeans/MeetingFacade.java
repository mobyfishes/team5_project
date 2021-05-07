/*
 * Created by Zihao Cai on 2021.3.21
 * Copyright © 2021 Osman Balci. All rights reserved.
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.Meeting;
import edu.vt.EntityBeans.User;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class MeetingFacade extends AbstractFacade<Meeting> {

    /*
    Annotating 'private EntityManager em;' with '@PersistenceContext(unitName = "Videos-CaiPU")'
    implies that the EntityManager instance pointed to by 'em' is associated with the 
    'Videos-CaiPU' persistence context. The persistence context is a set of entity (PublicVideo)
    instances in which for any persistent entity (PublicVideo) identity, there is a unique entity (PublicVideo)
    instance. Within the persistence context, the entity (PublicVideo) instances and their life cycle are
    managed. The EntityManager API is used to create and remove persistent entity (PublicVideo) instances,
    to find entities by their primary key, and to query over entities (PublicVideo).
     */
    @PersistenceContext(unitName = "Meeting-SchedulerPU")
    private EntityManager em;

    // @Override annotation indicates that the super class AbstractFacade's 
    // getEntityManager() method is overridden.
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /*
    This constructor method invokes the parent abstract class AbstractFacade.java's 
    constructor method, which in turn initializes its entityClass instance variable
    with the Country class object reference returned by the Country.class. 
     */
    public MeetingFacade() {
        // Invokes super's AbstractFacade constructor method by passing
        // Country.class, which is the object reference of the Country class.
        super(Meeting.class);
    }


    /*
    --------------------------------------
    Search Category: Public Video Title
    --------------------------------------
     */
    /**
     * Searches UsersVideosDB for PublicVideo where PublicVideo title contains the searchString entered by the user.
     *
     * @param searchString contains the search string the user entered for searching public video title
     * @return A list of PublicVideo object references as the search results
     */
    // This method is called in the getSearchItems() method in PublicVideoController.java
    public List<Meeting> nameQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the public video title
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery("SELECT c FROM Meeting c WHERE c.title LIKE :searchString").setParameter("searchString", searchString).getResultList();
    }


    /*
    --------------------------------------
    Search Category: Public Description
    --------------------------------------
     */
    /**
     * Searches UsersVideosDB for PublicVideo where PublicVideo's description contains the searchString entered by the user.
     *
     * @param searchString contains the search string the user entered for searching stock tickers
     * @return A list of PublicVideo object references as the search results
     */
    // This method is called in the getSearchItems() method in PublicVideoController.java
    public List<Meeting> DescriptionQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the description
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery("SELECT c FROM Meeting c WHERE c.location LIKE :searchString").setParameter("searchString", searchString).getResultList();
    }

        /*
    -------------------------------------
    Search Category: Public Video Category
    -------------------------------------
     */
    /**
     * Searches UsersVideosDB for PublicVideo where PublicVideo category contains the searchString entered by the user.
     *
     * @param searchString contains the search string the user entered for searching business Sector names
     * @return A list of PublicVideo object references as the search results
     */
    // This method is called in the getSearchItems() method in PublicVideoController.java
    public List<Meeting> HostnameQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the business Sector name
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery("SELECT c FROM Meeting c WHERE c.hostname LIKE :searchString").setParameter("searchString", searchString).getResultList();
    }

    /*
    --------------------
    Search Category: ALL
    --------------------
     */
    /**
     * Searches UsersVideosDB for PublicVideo where title, description, and category contains the searchString entered by the user.
     *
     * @param searchString contains the search string the user entered for title, description, and category
     * @return A list of PublicVideo object references as the search results
     */
    // This method is called in the getSearchItems() method in PublicVideoController.java
    public List<Meeting> allQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in company name, ticker, or sector name
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery("SELECT c FROM Meeting c WHERE c.title LIKE :searchString OR c.description LIKE :searchString OR c.hostname LIKE :searchString").setParameter("searchString", searchString).getResultList();
    }

    /**
     * Find all surveys that belong to a User whose database primary key is dbPrimaryKey
     *
     * @param dbPrimaryKey is the Primary Key of the user entity in the database
     * @return a list of object references of UserSurvey that belong to the user whose database Primary Key = dbPrimaryKey
     */
    public List<Meeting> findMeetingByUserPrimaryKey(Integer dbPrimaryKey) {

        List<Meeting> userMeetings = em.createNamedQuery("Meeting.findMeetingByUserPrimaryKey")
                .setParameter("primaryKey", dbPrimaryKey)
                .getResultList();

        return userMeetings;
    }


    /**
     * Find all surveys that belong to a User whose database primary key is dbPrimaryKey
     *
     * @param dbPrimaryKey is the Primary Key of the user entity in the database
     * @return a list of object references of UserSurvey that belong to the user whose database Primary Key = dbPrimaryKey
     */
    public List<Meeting> findMeetingByHostname(String hostname) {

        List<Meeting> userMeetings = em.createNamedQuery("Meeting.findByHostname")
                .setParameter("hostname", hostname)
                .getResultList();

        return userMeetings;
    }

    public Meeting findMeetingByID(Integer id){
        if (em.createQuery("SELECT c FROM Meeting c WHERE c.id = :id")
                .setParameter("id", id)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (Meeting) (em.createQuery("SELECT c FROM Meeting c WHERE c.id = :id")
                    .setParameter("id", id)
                    .getSingleResult());
        }
    }

    public List<Meeting> findMeetingByMeetID(Integer meeting_id) {

        List<Meeting> userMeetings = em.createNamedQuery("Meeting.findByMeetId")
                .setParameter("meeting_id", meeting_id)
                .getResultList();

        return userMeetings;
    }

}
