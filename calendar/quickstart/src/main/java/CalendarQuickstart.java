import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CalendarQuickstart {
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static Calendar getCalendarService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Initialize Safari WebDriver
        WebDriver driver = new SafariDriver();

        ConfigLoader configLoader = new ConfigLoader("config.properties");
      
        String navSite = configLoader.getProperty("website");
        String usernameStr = configLoader.getProperty("username");
        String passwordStr = configLoader.getProperty("password");

        try {
            // Open the webpage
            driver.get(navSite);

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement username = driver.findElement(By.id("txtUserName"));
            WebElement password = driver.findElement(By.id("txtPassword"));

            username.sendKeys(usernameStr);
            password.sendKeys(passwordStr);

            WebElement signOnBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("btnSignIn")));
            signOnBtn.click();

            WebElement listBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("btnListViewMode")));
            listBtn.click();

            WebElement calendarTable = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tblListView")));
            List<WebElement> dateCells = calendarTable.findElements(By.cssSelector("td"));

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy @ h:mm a");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

            String startTime = null;
            Calendar service = getCalendarService();

            for (WebElement cell : dateCells) {
                String cellText = cell.getText().trim();
                if (!cellText.isEmpty()) {
                    if (cellText.matches("\\w+ \\d+, \\d+ @ \\d{1,2}:\\d{2} [APM]{2}")) {
                        LocalDateTime dateTime = LocalDateTime.parse(cellText, inputFormatter);
                        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
                        String isoDateTime = zonedDateTime.format(outputFormatter);

                        if (startTime == null) {
                            startTime = isoDateTime;
                        } else {
                            createEvent(service, startTime, isoDateTime);
                            startTime = null;
                        }
                    }
                }
            }

            if (startTime != null) {
                createEvent(service, startTime, "No end time available");
            }

        } finally {
            driver.quit();
        }
    }

    private static void createEvent(Calendar service, String startTime, String endTime) throws IOException {
        Event event = new Event()
                .setSummary("Tim Horton's Shift")
                .setLocation("910 Columbia St W, Kamloops, BC V2C 1L2, Canada")
                .setDescription("Shift event created via Selenium and Google Calendar API.");

        DateTime startDateTime = new DateTime(startTime);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        DateTime endDateTime = new DateTime(endTime);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setEnd(end);

        String calendarId = "primary";
        event = service.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
    }
}
