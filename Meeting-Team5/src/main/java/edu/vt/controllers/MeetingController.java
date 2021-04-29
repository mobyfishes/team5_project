/*
 * Created by Zihao Cai on 2021.3.22
 * Copyright © 2021 Osman Balci. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.Meeting;
import edu.vt.EntityBeans.User;
import edu.vt.FacadeBeans.MeetingFacade;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.globals.Methods;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.mail.MessagingException;
import java.util.Calendar;

/*
---------------------------------------------------------------------------
The @Named (javax.inject.Named) annotation indicates that the objects
instantiated from this class will be managed by the Contexts and Dependency
Injection (CDI) container. The name "countryController" is used within
Expression Language (EL) expressions in JSF (XHTML) facelets pages to
access the properties and invoke methods of this class.
---------------------------------------------------------------------------
 */
@Named("meetingController")

/*
The @SessionScoped annotation preserves the values of the RecipeController
object's instance variables across multiple HTTP request-response cycles
as long as the user's established HTTP session is alive.
 */
@SessionScoped

/*
-----------------------------------------------------------------------------
Marking the PublicVideoController class as "implements Serializable" implies that
instances of the class can be automatically serialized and deserialized. 

Serialization is the process of converting a class instance (object)
from memory into a suitable format for storage in a file or memory buffer, 
or for transmission across a network connection link.

Deserialization is the process of recreating a class instance (object)
in memory from the format under which it was stored.
-----------------------------------------------------------------------------
 */
public class MeetingController implements Serializable {

    // searchField refers to Video Title, Video Description, Video Category or All
    private String searchField;

    // searchString contains the character string the user entered for searching the selected searchField
    private String searchString;
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
    The @EJB annotation directs the storage (injection) of the object 
    reference of the JPA RecipeFacade class object into the instance
    variable countryFacade below after it is instantiated at runtime.
     */
    @EJB
    private MeetingFacade meetFacade;

    @EJB
    private UserFacade userFacade;

    private EmailController emailController;

    //List of Videos that satisfied the search requirements by users
    private List<Meeting> searchItems = null;

    private Boolean meetDataChanged;
    //List of all Public Videos in Database.
    private List<Meeting> items = null;
    private Meeting selected;
    private String invited_username;

    /*
    ==================
    Constructor Method
    ==================
     */
    public MeetingController() {

    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public void setItems(List<Meeting> items) {
        this.items = items;
    }

    public MeetingFacade getMeetingFacade() {
        return meetFacade;
    }

    public void setMeetingFacade(MeetingFacade meetFacade) {
        this.meetFacade = meetFacade;
    }

    public Boolean getVideoDataChanged() {
        return meetDataChanged;
    }

    public void setVideoDataChanged(Boolean videoDataChanged) {
        this.meetDataChanged = videoDataChanged;
    }

    public MeetingFacade getMeetFacade() {
        return meetFacade;
    }

    public void setMeetFacade(MeetingFacade meetFacade) {
        this.meetFacade = meetFacade;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public Boolean getMeetDataChanged() {
        return meetDataChanged;
    }

    public void setMeetDataChanged(Boolean meetDataChanged) {
        this.meetDataChanged = meetDataChanged;
    }

    public String getInvited_username() {
        return invited_username;
    }

    public void setInvited_username(String invited_username) {
        this.invited_username = invited_username;
    }


    public List<Meeting> getItems() {
        if (items == null) {
            int userPrimaryKey = (int) Methods.sessionMap().get("user_id");
            items = getMeetingFacade().findMeetingByUserPrimaryKey(userPrimaryKey);
        }
        return items;
    }

    public void setSearchItems(List<Meeting> searchItems) {
        this.searchItems = searchItems;
    }

    public Meeting getSelected() {
        return selected;
    }

    public void setSelected(Meeting selected) {
        this.selected = selected;
    }

    /*
        ================
        Instance Methods
        ================

        ***************************************************
        Are All New Public Video Entered by the User?
        ***************************************************
    */
    public boolean allMeetingAttributesEntered(){

        if (selected != null) {
            return (!selected.getTitle().isEmpty()
                    && !selected.getDescription().isEmpty()
                    && !selected.getVideo_link().isEmpty()
                    && !selected.getMeet_date().equals(null)
                    && !selected.getHostname().isEmpty()
                    && !selected.getLocation().isEmpty())
                    && !selected.getDuration().isEmpty();
        } else {
            return false;
        }
    }

    /*
    *******************************
    Prepare to Create a New Public Video
    *******************************
     */
    public Meeting prepareCreate() {

        // Create a PublicVideo object as selected
        selected = new Meeting();

        // Initialize newly created PublicVideo object
        selected.setTitle("");
        selected.setDescription("");
        selected.setMeet_date(null);
        selected.setMeet_time(null);
        selected.setVideo_link("NoVideo");
        selected.setLocation("");
        User signedInUser = (User) Methods.sessionMap().get("user");
        selected.setHostname(signedInUser.getUsername());
        selected.setStatus("accepted");
        selected.setUserId(signedInUser);
        selected.setDuration("");
        selected.setAttendant(signedInUser.getUsername());
        selected.setMeeting_id(0);
        return selected;
    }

    /*
     ****************
     *   Unselect   *
     ****************
     */
    public void unselect() {
        selected = null;
    }

    /*
     ******************************************************
     *   Cancel to Display List.xhtml JSF Facelets Page   *
     ******************************************************
     */
    public String cancel() {
        // Unselect previously selected public video if any
        selected = null;
        return "/userMeeting/List?faces-redirect=true";
    }

    /*
     ********************************************
     *   Display List.xhtml JSF Facelets Page   *
     ********************************************
     */
    public String goBackToList() {
        // Unselect a public video selected in search results if any before showing the List page
        selected = null;
        return "/userMeeting/List?faces-redirect=true";
    }


    /*
    ************************************
    CREATE a New PublicVideo in the Database
    ************************************
    */
    public void create() {

        //Check all attributes are entered by user for creating a new public video
        if (!allMeetingAttributesEntered()) {
            selected = null;
            return;
        }

        /*
         ------------------------------------
         It is okay to create the new public video
         ------------------------------------

         We need to preserve the messages since we will redirect to show a
         different JSF page after successful creation of the country.
         */
        Methods.preserveMessages();
        /*
        Prevent displaying the same msg twice, one for Summary and one for Detail, by setting the
        message Detail to "" in the addSuccessMessage(String msg) method in the jsfUtil.java file.
         */
        persist(PersistAction.CREATE, "Meeting was successfully created.");
        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The CREATE operation is successfully performed.
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
            meetDataChanged = true;
        }
    }

    /*
    ***************************************
    UPDATE Selected PublicVideo in the Database
    ***************************************
     */
    public void update() {
        Methods.preserveMessages();
        persist(PersistAction.UPDATE, "Meeting was successfully updated.");
        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The UPDATE operation is successfully performed.
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
            meetDataChanged = true;
        }
    }

    /*
      *****************************************
      DELETE Selected PublicVideo from the Database
      *****************************************
       */
    public void destroy() {
        Methods.preserveMessages();
        Integer meetid = selected.getMeeting_id();
        User signedInUser = (User) Methods.sessionMap().get("user");
        if(signedInUser.getUsername().equals(selected.getHostname())){
           List<Meeting> canceledMeeting = getMeetingFacade().findMeetingByMeetID(meetid);
           for (int i = 0; i < canceledMeeting.size(); i++){
               Meeting temp = canceledMeeting.get(i);
               temp.setStatus("canceled");
               getMeetingFacade().edit(temp);
           }
        }

        persist(PersistAction.DELETE, "Meeting was successfully deleted.");
        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The DELETE operation is successfully performed.
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
            meetDataChanged = true;
        }
    }

    /*
     ****************************************************************************
     *   Perform CREATE, EDIT (UPDATE), and DELETE Operations in the Database   *
     ****************************************************************************
     */
    /**
     * @param persistAction refers to CREATE, UPDATE (Edit) or DELETE action
     * @param successMessage displayed to inform the user about the result
     */
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != PersistAction.DELETE) {
                    /*
                     -------------------------------------------------
                     Perform CREATE or EDIT operation in the database.
                     -------------------------------------------------
                     The edit(selected) method performs the SAVE (STORE) operation of the "selected"
                     object in the database regardless of whether the object is a newly
                     created object (CREATE) or an edited (updated) object (EDIT or UPDATE).

                     CountryFacade inherits the edit(selected) method from the AbstractFacade class.
                     */
                    getMeetingFacade().edit(selected);
                } else {
                    /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove method performs the DELETE operation in the database.

                     CountryFacade inherits the remove(selected) method from the AbstractFacade class.
                     */
                    getMeetingFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, "A persistence error occurred!");
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, "A persistence error occurred");
            }
        }
    }

    /**
     * Returns the object reference of the Company whose primary key = id
     *
     * @param id PublicVideo's id (primary key)
     * @return the object reference of the PublicVideo found
     */
    public Meeting getMeeting(Long id) {
        // PublicVideoFacade inherits the findAll() method from the AbstractFacade class
        return getMeetingFacade().find(id);
    }

    /*
    Obtain the object references of all of the public videos in the database
    and return them in an array (list).
     */
    public List<Meeting> getItemsAvailableSelectMany() {
        return getMeetingFacade().findAll();
    }

    /*
    Obtain the object references of all of the public videos in the database
    and return them in an array (list).
     */
    public List<Meeting> getItemsAvailableSelectOne() {
        return getMeetingFacade().findAll();
    }

    /*
        *************************************************************************
        *   Search searchString in searchField and Return the Search Results    *
        *************************************************************************
        Return the list of object references of all those public videos where the search
        string 'searchString' entered by the user is contained in the searchField.
        */
    public List<Meeting> getSearchItems() {
        /*
        =============================================================================================
        You must construct and return the search results List "searchItems" ONLY IF the List is null.
        Any List provided to p:dataTable to display must be returned ONLY IF the List is null
        ===> in order for the column-sort to work. <===
        =============================================================================================
         */
        if (searchItems == null) {
            switch (searchField) {
                case "Meeting Title":
                    // Return the list of object references of all those public videos where
                    //  title contains the search string 'searchString' entered by the user.
                    searchItems = getMeetingFacade().nameQuery(searchString);
                    break;
                case "Location":
                    // Return the list of object references of all those public videos where
                    // descriptions contains the search string 'searchString' entered by the user.
                    searchItems = getMeetingFacade().DescriptionQuery(searchString);
                    break;
                case "Host Name":
                    // Return the list of object references of all those public videos where
                    // category contains the search string 'searchString' entered by the user.
                    searchItems = getMeetingFacade().HostnameQuery(searchString);
                    break;
                default:
                    // Return the list of object references of all those companies where company title,
                    // descriptions, or category contains the search string 'searchString' entered by the user.
                    searchItems = getMeetingFacade().allQuery(searchString);
            }
        }
        return searchItems;
    }

    /*
     ********************************************
     *   Display the SearchResults.xhtml Page   *
     ********************************************
     */
    public String search() {
        // Unselect previously selected public video if any before showing the search results
        selected = null;
        // Invalidate list of search items to trigger re-query.
        searchItems = null;
        return "/userMeeting/SearchResults?faces-redirect=true";
    }

    public boolean checkVideoLink(){
        if(selected.getVideo_link().equals("NoVideo")){
            return true;
        }
        return false;
    }

    public String getAttendantList(){
        User signedInUser = (User) Methods.sessionMap().get("user");
        if (selected.getHostname().equals(signedInUser.getUsername())){
            String attendant_list = selected.getAttendant().replaceAll(" ", ", ");
            return attendant_list;
        }
        else{
            Methods.showMessage("Information", "Permission dined", "Only host can see the list of attendance!");
            return "No data!";
        }
    }


    public void addAttendant(){
        User signedInUser = (User) Methods.sessionMap().get("user");

        Integer meet_id = selected.getMeeting_id();
        if (meet_id == 0){
            meet_id = selected.getId();
        }
        selected = getMeetFacade().findMeetingByID(meet_id);
        String namelist = selected.getAttendant() + " " + signedInUser.getUsername();
        selected.setAttendant(namelist);
        persist(PersistAction.UPDATE, "New attendance.");

    }

    public void acceptPublicMeeting(){
        User signedInUser = (User) Methods.sessionMap().get("user");

        Meeting meeting = new Meeting();
        meeting.setTitle(selected.getTitle());
        meeting.setMeet_date(selected.getMeet_date());
        meeting.setMeet_time(selected.getMeet_time());
        meeting.setDescription(selected.getDescription());
        meeting.setUserId(signedInUser);
        meeting.setVideo_link(selected.getVideo_link());
        meeting.setLocation(selected.getLocation());
        meeting.setHostname(selected.getHostname());
        meeting.setStatus("accepted");
        meeting.setDuration(selected.getDuration());
        meeting.setAttendant(selected.getHostname());
        meeting.setMeeting_id(selected.getId());

        selected = meeting;
        create();
    }


    public void accept(){
        selected.setStatus("accepted");
        persist(PersistAction.UPDATE, "Meeting accepted.");
        addAttendant();
    }


    public void prepareInvite(){
        User invitedUser = getUserFacade().findByUsername(invited_username);
        if (invitedUser == null){
            Methods.showMessage("Error", "Username incorrect or the user not exist!", "");
        }
        else {
            selected.setMeeting_id(selected.getId());
            Methods.preserveMessages();
            persist(PersistAction.UPDATE,"Invitations send!");

            Meeting meeting = new Meeting();
            meeting.setTitle(selected.getTitle());
            meeting.setMeet_date(selected.getMeet_date());
            meeting.setMeet_time(selected.getMeet_time());
            meeting.setDescription(selected.getDescription());
            meeting.setUserId(invitedUser);
            meeting.setVideo_link(selected.getVideo_link());
            meeting.setLocation(selected.getLocation());
            meeting.setHostname(selected.getHostname());
            meeting.setStatus("pending");
            meeting.setDuration(selected.getDuration());
            meeting.setAttendant(selected.getHostname());
            meeting.setMeeting_id(selected.getId());

            emailController = new EmailController();
            emailController.setEmailTo(invitedUser.getEmail());
            String notification = "You are invited to the " + meeting.getTitle() + " by " + meeting.getHostname() + "at " + meeting.getDateByFormate()  + " .";
            emailController.setEmailBody(notification);
            emailController.setEmailSubject("You got a meeting invitation!");
            try {
                emailController.sendEmail();
            } catch (MessagingException e) {
                Methods.showMessage("Fatal Error",
                        "Email Messaging Exception Occurred! Internet Connection Required!",
                        "See: " + e.getMessage());
            }

            selected = meeting;
            create();
        }

    }

    public List<Meeting> getPublicMeetings(){
        List<Meeting> allmeetings = getMeetingFacade().findAll();
        List<Meeting> publicMeetings = new ArrayList<>();

        for (int i = 0; i < allmeetings.size(); i++){
            Meeting temp = allmeetings.get(i);
            if(temp.getStatus().equals("public")){
                publicMeetings.add(temp);
            }
        }

        return publicMeetings;
    }

    public List<Meeting> getAccpetedMeetings(){
        List<Meeting> allmeetings = getItems();
        List<Meeting> acceptedMeetings = new ArrayList<>();

        for (int i = 0; i < allmeetings.size(); i++){
            Meeting temp = allmeetings.get(i);
            if(!temp.getStatus().equals("pending")){
                acceptedMeetings.add(temp);
            }
        }

        return acceptedMeetings;
    }

    public List<Meeting> getPendingMeetings(){
        List<Meeting> allmeetings = getItems();
        List<Meeting> pendingMeetings = new ArrayList<>();

        for (int i = 0; i < allmeetings.size(); i++){
            Meeting temp = allmeetings.get(i);
            if(temp.getStatus().equals("pending")){
                pendingMeetings.add(temp);
            }
        }

        return pendingMeetings;
    }


}
