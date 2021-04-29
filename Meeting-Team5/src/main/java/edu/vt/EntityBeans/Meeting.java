/*
 * Created by Zihao Cai on 2021.3.21
 * Copyright © 2021 Osman Balci. All rights reserved.
 */
package edu.vt.EntityBeans;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
//import javax.xml.bind.annotation.XmlRootElement;

/*
The @Entity annotation designates this class as a JPA Entity POJO class
representing the Video table in the VideosDB database.
 */
@Entity

// Name of the table in the VideosDB database
@Table(name = "Meeting")

//@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Meeting.findAll", query = "SELECT c FROM Meeting c")
        , @NamedQuery(name = "Meeting.findById", query = "SELECT c FROM Meeting c WHERE c.id = :id")
        , @NamedQuery(name = "Meeting.findByMeetId", query = "SELECT c FROM Meeting c WHERE c.meeting_id = :meeting_id")
        , @NamedQuery(name = "Meeting.findByTitle", query = "SELECT c FROM Meeting c WHERE c.title = :title")
        , @NamedQuery(name = "Meeting.findByHostname", query = "SELECT c FROM Meeting c WHERE c.hostname = :hostname")
        , @NamedQuery(name = "Meeting.findMeetingByUserPrimaryKey", query = "SELECT c FROM Meeting c WHERE c.userId.id = :primaryKey")
})

public class Meeting implements Serializable {

    /*
    ========================================================
    Instance variables representing the attributes (columns)
    of the PublicVideo table in the UsersVideosDB database.
    ========================================================
     */
    // Primary Key id is internally used; not shown to the user
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "meeting_id")
    private Integer meeting_id;

    // Public Video title
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "title")
    private String title;

    // Public Video description
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 512)
    @Column(name = "description")
    private String description;

    // Public Video YouTube id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "location")
    private String location;

    // Public Video YouTube id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "video_link")
    private String video_link;

    // Public Video date published
    @Basic(optional = false)
    @NotNull
    @Column(name = "meet_date")
    private Date  meet_date;

    // Public Video date published
    @Basic(optional = false)
    @NotNull
    @Column(name = "meet_time")
    private Date meet_time;


    // Public Video category
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "hostname")
    private String hostname;

    // Public Video category
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "status")
    private String status;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "duration")
    private String duration;

    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 16777215)
    @Column(name = "attendant")
    private String attendant;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User userId;


    /*
    =================================================================
    Class constructors for instantiating a Public Video entity / object to
    represent a row in the PublicVideo table in the UsersVideosDB database.
    =================================================================
     */
    public Meeting() {
    }

    public Meeting(Integer id) {
        this.id = id;
    }

    public Meeting(Integer id, String title, String description, String video_link, String location,
                   Timestamp meet_date, String hostname, String status, User userId, String duration,
                   String attendant,Integer meeting_id, Date meet_time) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.video_link = video_link;
        this.location = location;
        this.meet_date = meet_date;
        this.meet_time = meet_time;
        this.hostname = hostname;
        this.status = status;
        this.userId = userId;
        this.duration = duration;
        this.attendant = attendant;
        this.meeting_id = meeting_id;
    }

    /*
    ======================================================
    Getter and Setter methods for the attributes (columns)
    of the PublicVideo table in the UsersVideosDB database.
    ======================================================
     */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVideo_link() {
        return video_link;
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }

    public Date getMeet_date() {
        return meet_date;
    }

    public void setMeet_date(Date meet_date) {
        this.meet_date = meet_date;
    }

    public Date getMeet_time() {
        return meet_time;
    }

    public void setMeet_time(Date meet_time) {
        this.meet_time = meet_time;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Integer getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(Integer meeting_id) {
        this.meeting_id = meeting_id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAttendant() {
        return attendant;
    }

    public void setAttendant(String attendant) {
        this.attendant = attendant;
    }

    public String getDateByFormate(){
        if (this.getStatus().equals("canceled")){
            return "Meeting was canceled by the Host";
        }
        else{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String strDate= formatter.format(this.meet_date);

            SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
            String strTime= formatter2.format(this.meet_time);

            String datetime = strDate + " " + strTime;

            return datetime;
        }

    }

    /*
    ================
    Instance Methods
    ================
     */

    /**
     *
     * @return the String representation of the Country's database primary key
     */
    @Override
    public String toString() {
        // Convert the Country object's database primary key (Integer) to String type and return it.
        return id.toString();
    }

        /*
    ================
    Instance Methods
    ================
     */
    /**
     * @return Generates and returns a hash code value for the object with id
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * Checks if the UserFile object identified by 'object' is the same as the UserFile object identified by 'id'
     *
     * @param object The UserFile object identified by 'object'
     * @return True if the UserFile 'object' and 'id' are the same; otherwise, return False
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Meeting)) {
            return false;
        }
        Meeting other = (Meeting) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
