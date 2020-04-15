package fi.uta.cs.weto.util;

import fi.uta.cs.weto.db.Notification;
import fi.uta.cs.weto.db.UserAccount;
import org.apache.log4j.Logger;

import javax.mail.MessagingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener()
public class NotificationManager implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(NotificationManager.class);

    private ScheduledExecutorService scheduler;

    private DbConnectionManager connectionManager;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        connectionManager = DbConnectionManager.getInstance();

        scheduler = Executors.newSingleThreadScheduledExecutor();

        int notificationEmailInterval = Integer.parseInt(
                WetoUtilities.getPackageResource("notification.emailInterval.minutes"));
        scheduler.scheduleAtFixedRate(new NotificationEmailTask(), 1, notificationEmailInterval, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }

    private class NotificationEmailTask implements Runnable {
        @Override
        public void run() {
            Connection masterConnection = connectionManager.getConnection("master");

            // Map notifications by user id into lists
            HashMap<Integer, ArrayList<Notification>> notificationMap = new HashMap<>();
            ArrayList<Notification> unsentNotifications;
            try {
                unsentNotifications = Notification.getNotificationsNotSentByEmail(masterConnection);
            } catch (Exception e) {
                logger.error("Failed to fetch unsent notifications", e);
                return;
            }
            for (Notification notification : unsentNotifications) {
                int userId = notification.getUserId();

                if(notificationMap.get(userId) == null) {
                    notificationMap.put(userId, new ArrayList<Notification>());
                }
                notificationMap.get(userId).add(notification);
            }

            // Form emails for each user
            for (int userId : notificationMap.keySet()) {
                ArrayList<Notification> notifications = notificationMap.get(userId);

                UserAccount userAccount;
                try {
                    userAccount = UserAccount.select1ById(masterConnection, userId);
                } catch (Exception e) {
                    logger.error("Failed to fetch user account", e);
                    break;
                }

                String emailSubject = String.format("WETO: %s new notifications", notifications.size());
                StringBuilder emailMessage = new StringBuilder(String.format("New notifications (%s):%n", notifications.size()));
                for(Notification notification : notifications) {
                    emailMessage.append(notification.getMessage())
                            .append("\n");
                }

                try {
                    Email.sendMail(userAccount.getEmail(), emailSubject, emailMessage.toString());
                } catch (MessagingException e) {
                    logger.error("Failed to send automated notification email", e);
                    break;
                }

                for(Notification notification : notifications) {
                    try {
                        notification.setSentByEmail(true);
                        notification.update(masterConnection);
                    } catch (Exception e) {
                        logger.error("Failed to save notification state during automated emailing", e);
                    }
                }
            }
        }
    }
}
