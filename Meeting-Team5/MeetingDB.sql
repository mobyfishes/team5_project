# -----------------------------------------------
# SQL script to create the UsersVideosDB database
# tables and populate the PublicVideo table.
# Created by Zihao Cai
# -----------------------------------------------

/*
Tables to be dropped must be listed in a logical order based on dependency.
UserVideo and UserPhoto depend on User. Therefore, they must be dropped before User.
*/
DROP TABLE IF EXISTS Meeting, UserPhoto, User;

/* The User table contains attributes of interest of a User. */
CREATE TABLE User
(
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    username VARCHAR(32) NOT NULL,
    password VARCHAR(256) NOT NULL,          /* To store Salted and Hashed Password Parts */
    first_name VARCHAR(32) NOT NULL,
    middle_name VARCHAR(32),
    last_name VARCHAR(32) NOT NULL,
    address1 VARCHAR(128) NOT NULL,
    address2 VARCHAR(128),
    city VARCHAR(64) NOT NULL,
    state VARCHAR(2) NOT NULL,
    zipcode VARCHAR(10) NOT NULL,            /* e.g., 24060-1804 */
    security_question_number INT NOT NULL,   /* Refers to the number of the selected security question */
    security_answer VARCHAR(128) NOT NULL,
    email VARCHAR(128) NOT NULL,      
    PRIMARY KEY (id)
);

/* The UserPhoto table contains attributes of interest of a user's photo. */
CREATE TABLE UserPhoto
(
       id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
       extension ENUM('jpeg', 'jpg', 'png', 'gif') NOT NULL,
       user_id INT UNSIGNED,
       FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

/* The Meeting table contains attributes of interest of meeting. */
CREATE TABLE Meeting
(
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
	user_id INT UNSIGNED,
	meeting_id INT UNSIGNED,
    title VARCHAR (256) NOT NULL,
    description VARCHAR (512) NOT NULL,
	location VARCHAR (512) NOT NULL,
    video_link VARCHAR (128) NOT NULL,
    meet_date DATETIME NOT NULL,
	meet_time DATETIME NOT NULL,
    hostname VARCHAR (512)  NOT NULL,
	status VARCHAR (16) NOT NULL, /* pending or accepted */
	duration VARCHAR (8) NOT NULL,
	attendant MEDIUMTEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

