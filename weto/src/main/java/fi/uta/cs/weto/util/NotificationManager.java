package fi.uta.cs.weto.util;

import fi.uta.cs.weto.db.Notification;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserAccount;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import javax.mail.MessagingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
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

            // Setup Velocity templates for the email
            final String htmlTemplateName = "NotificationEmailTemplate.vm";
            final String textTemplateName = "NotificationEmailTemplateText.vm";

            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
            velocityEngine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
            velocityEngine.setProperty("runtime.log.logsystem.log4j.category", "velocity");
            velocityEngine.setProperty("runtime.log.logsystem.log4j.logger", "velocity");

            Template htmlTemplate;
            Template textTemplate;
            try {
                velocityEngine.init();
                htmlTemplate = velocityEngine.getTemplate(htmlTemplateName, "UTF-8");
                textTemplate = velocityEngine.getTemplate(textTemplateName, "UTF-8");
            } catch (Exception e) {
                logger.error("Failed to initialize velocity engine", e);
                return;
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

                HashMap<Integer, String> courseNameMap = new HashMap<>();
                try {
                    for(Notification notification : notifications) {
                        int courseId = notification.getCourseId();
                        courseNameMap.put(courseId, Task.select1ById(masterConnection, courseId).getName());
                    }
                } catch (Exception e) {
                    logger.error("Failed to fetch course name", e);
                    break;
                }

                try (StringWriter htmlStringWriter = new StringWriter();
                    StringWriter textStringWriter = new StringWriter()) {
                    String emailSubject = String.format("WETO: %s new notification(s)", notifications.size());
                    String htmlMessage, textMessage;

                    // Set up the context for velocity and evaluate the template
                    VelocityContext velocityContext = new VelocityContext();
                    velocityContext.put("emailTitle", emailSubject);
                    velocityContext.put("courseNames", courseNameMap);
                    velocityContext.put("notifications", notifications);
                    velocityContext.put("notificationTypeMap", Notification.getTypeDisplayMap());

                    htmlTemplate.merge(velocityContext, htmlStringWriter);
                    htmlStringWriter.flush();
                    htmlMessage = htmlStringWriter.toString();

                    textTemplate.merge(velocityContext, textStringWriter);
                    textStringWriter.flush();
                    textMessage = textStringWriter.toString();

                    Email.sendHtmlEmail(userAccount.getEmail(), emailSubject, htmlMessage, textMessage);
                } catch (IOException e) {
                    logger.error("Failed to create a html from the email template", e);
                    break;
                } catch (MessagingException e) {
                    logger.error("Failed to send automated notification email", e);
                    break;
                }

                // Save the notifications as sent
                for(Notification notification : notifications) {
                    try {
                        notification.setSentByEmail(true);
                        notification.update(masterConnection);
                    } catch (Exception e) {
                        logger.error("Failed to save notification state during automated emailing", e);
                    }
                }
            }

            try {
                masterConnection.commit();
            } catch (Exception e) {
                try {
                    masterConnection.rollback();
                } catch (SQLException ignored) {
                }
            }

            connectionManager.freeConnection(masterConnection);
        }
    }
}