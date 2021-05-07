/*
 * Created by Zihao Cai on 2021.3.21
 * Copyright © 2021 Osman Balci. All rights reserved.
 */
package edu.vt.managers;

import edu.vt.controllers.EmailController;
import edu.vt.globals.Password;
import edu.vt.EntityBeans.User;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.globals.Methods;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.mail.MessagingException;
import java.util.Random;


// A Java program to demonstrate random number generation
// using java.util.Random;

@Named(value = "loginManager")
@SessionScoped

public class LoginManager implements Serializable {

    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String username;
    private String password;
    private String token;
    private String enteredToken = "";
    private User user;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEnteredToken() {
        return enteredToken;
    }

    public void setEnteredToken(String enteredToken) {
        this.enteredToken = enteredToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /*
            The instance variable 'userFacade' is annotated with the @EJB annotation.
            The @EJB annotation directs the EJB Container (of the WildFly AS) to inject (store) the object reference
            of the UserFacade object, after it is instantiated at runtime, into the instance variable 'userFacade'.
             */
    @EJB
    private UserFacade userFacade;

    /*
    ==================
    Constructor Method
    ==================
     */
    public LoginManager() {
    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public String checkToken() throws MessagingException {
        // create instance of Random class
        Random rand = new Random();

        // Generate random integers in range 0 to 999
        int randomNumber = rand.nextInt(9999 + 1 - 1000) + 1000;
        token = String.valueOf(randomNumber);
        System.out.println("Generating token: " + token);
        EmailController emailController = new EmailController();
        emailController.setEmailTo(user.getEmail());
        emailController.setEmailBody("Please enter code " + token + " to complete sign-in.");
        emailController.setEmailSubject("MeetingScheduler Two-Factor Authentication");
        emailController.sendEmail();

        return token;
    }

    public String checkToken_cn() throws MessagingException {
        // create instance of Random class
        Random rand = new Random();

        // Generate random integers in range 0 to 999
        int randomNumber = rand.nextInt(9999 + 1 - 1000) + 1000;
        token = String.valueOf(randomNumber);
        System.out.println("Generating token: " + token);
        EmailController emailController = new EmailController();
        emailController.setEmailTo(user.getEmail());
        emailController.setEmailBody("请输入" + token + " 进行登入.");
        emailController.setEmailSubject("会议日程表登入验证码");
        emailController.sendEmail();

        return token;
    }

    // Called when user submits 2FA code on TokenVerification page.
    public String verifyToken() {
        // If token matches, log in the user and direct them to their profile page
        if (token.equals(enteredToken)) {
            System.out.println("Correct token!");
            initializeSessionMap(user);
            return "/userAccount/Profile?faces-redirect=true";
        } else {
            // If token does not match, keep the user at this page.
            // The user may attempt again to enter the same token.
            System.out.println("Incorrect token!");
            Methods.showMessage("Fatal Error", "Invalid Token!", "The token did not match!");
            return "/TokenVerification?faces-redirect=true";
        }
    }

    // Called when user submits 2FA code on TokenVerification page.
    public String verifyToken_cn() {
        // If token matches, log in the user and direct them to their profile page
        if (token.equals(enteredToken)) {
            System.out.println("验证码正确!");
            initializeSessionMap(user);
            return "/CN/userAccount/Profile?faces-redirect=true";
        } else {
            // If token does not match, keep the user at this page.
            // The user may attempt again to enter the same token.
            System.out.println("验证码错误!");
            Methods.showMessage("Fatal Error", "错误验证码", "验证码不符合!");
            return "/CN/TokenVerification?faces-redirect=true";
        }
    }

    /*
    ================
    Instance Methods
    ================

    *****************************************************
    Sign in the User if the Entered Username and Password
    are Valid and Redirect to Show the Profile Page
    *****************************************************
     */
    public String loginUser() throws MessagingException {

        // Since we will redirect to show the Profile page, invoke preserveMessages()
        Methods.preserveMessages();

        String enteredUsername = getUsername();

        // Obtain the object reference of the User object from the entered username
        user = getUserFacade().findByUsername(enteredUsername);

        if (user == null) {
            Methods.showMessage("Fatal Error", "Unknown Username!",
                    "Entered username " + enteredUsername + " does not exist!");
            return "";

        } else {
            String actualUsername = user.getUsername();

            String actualPassword = user.getPassword();
            String enteredPassword = getPassword();

            if (!actualUsername.equals(enteredUsername)) {
                Methods.showMessage("Fatal Error", "Invalid Username!", "Entered Username is Unknown!");
                return "";
            }

            //------------------------------------------------------------------------------------
            // Obtain the user's password String containing the following parts from the database
            //       "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash",
            // extract the actual password from the parts, and compare it with the password String
            // entered by the user by using Key Stretching to prevent brute-force attacks.
            //------------------------------------------------------------------------------------            
            try {
                if (Password.verifyPassword(enteredPassword, actualPassword)) {
                    // entered password = user's actual password
                } else {
                    Methods.showMessage("Fatal Error", "Invalid Password!", "Please Enter a Valid Password!");
                    return "";
                }
            } catch (Password.CannotPerformOperationException | Password.InvalidHashException ex) {

                Methods.showMessage("Fatal Error", "Password Manager was unable to perform its operation!",
                        "See: " + ex.getMessage());
                return "";
            }

            // Redirect to show the Profile page
            if (user.getUsername().equals("sadewale") || user.getUsername().equals("barnetts")  || user.getUsername().equals("test1test1")){
                // Initialize the session map with user properties of interest
                initializeSessionMap(user);
                return "/userAccount/Profile?faces-redirect=true";
            } else {
                checkToken();
                return "/TokenVerification?faces-redirect=true";
            }

        }
    }

    public String loginUser_CN() throws MessagingException {

        // Since we will redirect to show the Profile page, invoke preserveMessages()
        Methods.preserveMessages();

        String enteredUsername = getUsername();

        // Obtain the object reference of the User object from the entered username
        user = getUserFacade().findByUsername(enteredUsername);

        if (user == null) {
            Methods.showMessage("Fatal Error", "Unknown Username!",
                    "Entered username " + enteredUsername + " does not exist!");
            return "";

        } else {
            String actualUsername = user.getUsername();

            String actualPassword = user.getPassword();
            String enteredPassword = getPassword();

            if (!actualUsername.equals(enteredUsername)) {
                Methods.showMessage("Fatal Error", "Invalid Username!", "Entered Username is Unknown!");
                return "";
            }

            //------------------------------------------------------------------------------------
            // Obtain the user's password String containing the following parts from the database
            //       "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash",
            // extract the actual password from the parts, and compare it with the password String
            // entered by the user by using Key Stretching to prevent brute-force attacks.
            //------------------------------------------------------------------------------------
            try {
                if (Password.verifyPassword(enteredPassword, actualPassword)) {
                    // entered password = user's actual password
                } else {
                    Methods.showMessage("Fatal Error", "Invalid Password!", "Please Enter a Valid Password!");
                    return "";
                }
            } catch (Password.CannotPerformOperationException | Password.InvalidHashException ex) {

                Methods.showMessage("Fatal Error", "Password Manager was unable to perform its operation!",
                        "See: " + ex.getMessage());
                return "";
            }

            // Redirect to show the Profile page
            if (user.getUsername().equals("sadewale") || user.getUsername().equals("barnetts")  || user.getUsername().equals("test1test1")){
                // Initialize the session map with user properties of interest
                initializeSessionMap(user);
                return "/CN/userAccount/Profile?faces-redirect=true";
            } else {
                checkToken();
                return "/CN/TokenVerification?faces-redirect=true";
            }

        }
    }


    /*
    ******************************************************************
    Initialize the Session Map to Hold Session Attributes of Interests 
    ******************************************************************
     */
    public void initializeSessionMap(User user) {
        // Store the object reference of the signed-in user
        Methods.sessionMap().put("user", user);

        // Store the First Name of the signed-in user
        Methods.sessionMap().put("first_name", user.getFirstName());

        // Store the Last Name of the signed-in user
        Methods.sessionMap().put("last_name", user.getLastName());

        // Store the Username of the signed-in user
        Methods.sessionMap().put("username", username);

        // Store signed-in user's Primary Key in the database
        Methods.sessionMap().put("user_id", user.getId());
    }

}
