package edu.vt.controllers;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import edu.vt.EntityBeans.Meeting;
import edu.vt.globals.Methods;

import javax.inject.Named;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.awt.Desktop;
import java.net.URI;

@Named("googleCalController")

public class GoogleCalController {
    private static final String APPLICATION_NAME = "Meeting-Scheduler";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens/";

    /**
     * Global instance of the scopes required by this application.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException, URISyntaxException {
        // Load client secrets.
        InputStream in = GoogleCalController.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        // Checks if authorizations log is empty for this user
        if (flow.getCredentialDataStore().isEmpty()) {
            // If user hasn't already authorized with Google, we need to direct them to a login page
            String accessURL = flow.getAuthorizationServerEncodedUrl() + "?access_type=offline&client_id=" + flow.getClientId()
                    + "&redirect_uri=http://" + receiver.getHost() + ":" + receiver.getPort() + receiver.getCallbackPath()
                    + "&response_type=code&scope=" + flow.getScopesAsString();

            // Detect user's operating system and execute the corresponding command to load authorization page
            String os = System.getProperty("os.name").toLowerCase();
            String url = accessURL;
            if (os.indexOf("mac") >= 0) {
                Runtime rt = Runtime.getRuntime();
                rt.exec("open " + accessURL);
            } else if (os.indexOf("win") >= 0) {
                Runtime rt = Runtime.getRuntime();
                rt.exec("rundll32 url.dll,FileProtocolHandler " + accessURL);
            } else if (os.indexOf("nix") >=0 || os.indexOf("nux") >=0) {
                Runtime rt = Runtime.getRuntime();
                String[] browsers = { "epiphany", "firefox", "mozilla", "konqueror",
                        "netscape", "opera", "links", "lynx" };

                StringBuffer cmd = new StringBuffer();
                for (int i = 0; i < browsers.length; i++)
                    if(i == 0)
                        cmd.append(String.format(    "%s \"%s\"", browsers[i], accessURL));
                    else
                        cmd.append(String.format(" || %s \"%s\"", browsers[i], accessURL));
                // If the first didn't work, try the next browser
                rt.exec(new String[] { "sh", "-c", cmd.toString() });
            }
        }
        // Return authorization
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void checkConflicts(Meeting meeting) throws IOException, GeneralSecurityException, URISyntaxException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Reference the start and end times for the selected meeting
        // The Date and Time are stored separately and need to be merged into a single DateTime string. Currently, all times are ET
        String startTime = meeting.getMeet_date().toString().split(" ")[0] + "T" + meeting.getMeet_time().toString().split(" ")[1] + "00-04:00";
        DateTime start = new DateTime(startTime);
        // The Meeting duration can be converted into milliseconds and added to the start DateTime to calculate the end time
        String[] durationArr = meeting.getDuration().split(":");
        int milliseconds = (60 * Integer.parseInt(durationArr[0]) + Integer.parseInt(durationArr[1])) * 60000;
        DateTime end = new DateTime(start.getValue() + milliseconds);

        // Instantiate an Empty list of FreeBusyRequestItems
        // A FreeBusyRequestItem is needed for each Google Calendar we will query (a single user can subscribe to several)
        List<FreeBusyRequestItem> requestItems = new LinkedList<>();

        // Iterate through all of the user's subscribed calendars
        String pageToken = null;
        do {
            CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : items) {
                // Create a FreeBusyRequestItem using the ID of each subscribed calendar
                requestItems.add(new FreeBusyRequestItem().setId(calendarListEntry.getId()));
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        // Send Google the start time, end time, and calendars we would like to reference
        FreeBusyRequest request = new FreeBusyRequest().setTimeMin(start).setTimeMax(end).setItems(requestItems);
        FreeBusyResponse response = service.freebusy().query(request).execute();

        // Iterate through all of the calendars in the response
        for (FreeBusyRequestItem freeBusyRequestItem : requestItems) {
            // A calendar with no conflicts will return an empty set for getBusy
            // A calendar with conflicts will return the start and end times of the conflicting meetings
            // Check to see if the set is empty. If it is not, there is a conflict. Exit the loop
            if (response.getCalendars().get(freeBusyRequestItem.getId()).getBusy().listIterator().hasNext()) {
                System.out.println("You have another meeting at this time");
                Methods.showMessage("Error", "You have another meeting at this time!", "");
                return;
            }
        }
        // If we have not exited, this means we have no conflict.
        System.out.println("You have no other meetings at this time");
        Methods.showMessage("Information", "You have no other meetings at this time!", "");
    }

    public static String goToGoogle(String url) {
        return url;
    }
}
