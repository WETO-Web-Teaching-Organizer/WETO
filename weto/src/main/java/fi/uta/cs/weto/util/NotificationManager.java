package fi.uta.cs.weto.util;

import fi.uta.cs.weto.db.*;
import fi.uta.cs.weto.model.*;
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
import java.util.*;
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
        int permissionExpirationCheckInterval = Integer.parseInt(
                WetoUtilities.getPackageResource("notification.permissionExpirationCheckInterval.hours"));
        scheduler.scheduleAtFixedRate(new NotificationEmailTask(), 1, notificationEmailInterval, TimeUnit.MINUTES);

        scheduler.scheduleAtFixedRate(new DeadlineNotificationTask(), 1, permissionExpirationCheckInterval, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }




    private class DeadlineNotificationTask implements Runnable {

        @Override
        public void run() {

            ArrayList<Integer> triggeringPermissions = new ArrayList<>();
            triggeringPermissions.add(PermissionType.SUBMISSION.getValue());
            triggeringPermissions.add(PermissionType.GRADING.getValue());
            triggeringPermissions.add(PermissionType.RESULTS.getValue());
            triggeringPermissions.add(PermissionType.GRADE_CHALLENGE.getValue());

            Connection masterCon = connectionManager.getConnection("master");
            ArrayList<Permission> activeTasks = new ArrayList<>();

            try {
                activeTasks = Permission.selectActive(masterCon);
                logger.debug("loaded " + activeTasks.size() + " active courses");
            } catch (Exception e) {
                logger.error(e);
            }

            ArrayList<Integer> iteratedDatabases = new ArrayList<>();
            for (Permission masterTask : activeTasks) {
                try {

                    int databaseID = CourseImplementation.select1ByMasterTaskId(masterCon,masterTask.getTaskId()).getDatabaseId();

                    //iterate a course database only once
                    if (!iteratedDatabases.contains(databaseID)) {
                        iteratedDatabases.add(databaseID);
                        String databaseName = DatabasePool.select1ById(masterCon, databaseID).getName();
                        Connection courseCon = connectionManager.getConnection(databaseName);
                        ArrayList<Permission> allActiveCoursePermissions = new ArrayList<>();
                        try {
                            allActiveCoursePermissions = Permission.selectActive(courseCon);
                        } catch (Exception e) {
                            logger.debug(e + "  with connection   " + courseCon);
                        }

                        for (Permission permission : allActiveCoursePermissions) {

                            if (permission.getEndDate() == null) {
                                continue;
                            }

                             //Check if the permission expiration is going to happen in notification.permissionExpirationCheckTime.hours
                             //Timespans width should be the same as the interval for checking incoming deadlines to avoid double or missing notifications.
                            try {

                                int timeSpanLocation = Integer.parseInt(
                                        WetoUtilities.getPackageResource("notification.permissionExpirationCheckTime.minutes"));

                                int timeSpanWidth = Integer.parseInt(
                                        WetoUtilities.getPackageResource("notification.permissionExpirationCheckInterval.minutes"));

                                WetoTimeStamp timeSpanStart = new WetoTimeStamp(permission.getEndDate());
                                WetoTimeStamp timeSpanEnd = new WetoTimeStamp(permission.getEndDate());
                                WetoTimeStamp timeNow = new WetoTimeStamp(new GregorianCalendar());

                                timeSpanStart.setHour(timeSpanStart.getHour() - timeSpanLocation); //This should be the windows start point of checking eg. 24h behind
                                timeSpanEnd.setHour(timeSpanStart.getHour() + timeSpanWidth);

                                boolean isAfterStart = timeNow.getTimeStamp() >= timeSpanStart.getTimeStamp();
                                boolean isBeforeEnd = timeNow.getTimeStamp() <= timeSpanEnd.getTimeStamp();
                                if (!(isAfterStart && isBeforeEnd)) continue;
                            } catch (Exception e) {
                                logger.error("Something went wrong while checking permission timespans", e);
                            }

                            //Implement teacher notifications here
                            if (triggeringPermissions.contains(permission.getType())) {
                                int taskId = permission.getTaskId();
                                Task task = Task.select1ById(courseCon, taskId);
                                int rootTaskId = task.getRootTaskId();
                                try {
                                    int masterCourseId = CourseImplementation.select1ByDatabaseIdAndCourseTaskId(masterCon, databaseID, rootTaskId).getMasterTaskId();

                                    // Create notification's message for teachers.
                                    HashMap<String, String> templateValueMap = new HashMap<>();
                                    templateValueMap.put("&permission;", WetoUtilities.getMessageResource(PermissionType.getType(permission.getType()).getProperty()));
                                    templateValueMap.put("&task;", task.getName());
                                    templateValueMap.put("&expirationTime;", new WetoTimeStamp(permission.getEndDate()).toString());

                                    String notificationMessage = Notification.getMessageFromTemplate(Notification.PERMISSION_EXPIRATION, templateValueMap);

                                    // Create link.
                                    String link = WetoUtilities.getPackageResource("weto.appBaseUrl")
                                            + "/viewPermissions.action?taskId=" + taskId
                                            + "&tabId=" + Tab.PERMISSIONS.getValue()
                                            + "&dbId=" + databaseID;


                                    for (UserTaskView teacher : UserTaskView.selectByTaskIdAndClusterType(courseCon, rootTaskId, ClusterType.TEACHERS.getValue())) {
                                        UserAccount courseAccount = UserAccount.select1ById(courseCon,teacher.getUserId());
                                        UserAccount masterAccount = UserAccount.select1ByLoginName(masterCon,courseAccount.getLoginName());
                                        Notification notification = new Notification(masterAccount.getId(), masterCourseId, Notification.PERMISSION_EXPIRATION, link);
                                        notification.setMessage(notificationMessage);
                                        notification.createNotification(masterCon, courseCon);

                                    }
                                }
                                catch (Exception ignored) {
                                }
                            }

                            // Check if permission is submission permission
                            if (permission.getType() == 1) {

                                //Store students which haven't completed the assignment to a set
                                HashSet<Integer> notCompletedSubmissionStudents = new HashSet<>();

                                int assignmentTaskID = permission.getTaskId();
                                Task assignment = Task.select1ById(courseCon,assignmentTaskID);

                                //Submissions tab should be visible to be able to return a submission
                                if (!assignment.getHasSubmissions()) {
                                    continue;
                                }

                                boolean isAllUsersPermission = permission.getUserRefId() == null;
                                //Get all students on task to set
                                if (isAllUsersPermission) {

                                    try {
                                        for (UserTaskView student : UserTaskView.selectByTaskIdAndClusterType(courseCon, assignment.getId(), ClusterType.STUDENTS.getValue())) {
                                            notCompletedSubmissionStudents.add(student.getUserId());
                                        }
                                    } catch (Exception e) {
                                        logger.error(e);
                                    }
                                }

                                ArrayList<Submission> taskSubmissions = Submission.selectByTaskId(courseCon, assignmentTaskID);

                                //Remove users with completed submissions
                                for (Submission sub : taskSubmissions) {
                                    int status = sub.getStatus();


                                    //submission status 2 == accepted
                                    if (SubmissionStatus.getStatus(status).equals(SubmissionStatus.ACCEPTED) && isAllUsersPermission) {
                                        try {
                                            notCompletedSubmissionStudents.remove(sub.getUserId());
                                        } catch (Exception e) {
                                            logger.debug("There was no id to remove");
                                        }
                                    }
                                    //In case the permission is for a single user
                                    if (status != 2 && !isAllUsersPermission) {
                                        notCompletedSubmissionStudents.add(sub.getUserId());
                                    }
                                }

                                for (int student : notCompletedSubmissionStudents) {


                                    UserAccount courseAccount2 = UserAccount.select1ById(courseCon,student);
                                    UserAccount masterAccount2 = UserAccount.select1ByLoginName(masterCon,courseAccount2.getLoginName());


                                    int masterCourseID = CourseImplementation.select1ByDatabaseIdAndCourseTaskId(masterCon, databaseID, assignment.getRootTaskId()).getMasterTaskId();
                                    HashMap<String, String> valueMap = new HashMap<>();

                                    valueMap.put("&task;", assignment.getName());
                                    valueMap.put("&expirationTime;", new WetoTimeStamp(permission.getEndDate()).toString());
                                    String message = Notification.getMessageFromTemplate(Notification.SUBMISSION_DEADLINE, valueMap);

                                    // Create link.
                                    String notificationLink = WetoUtilities.getPackageResource("weto.appBaseUrl")
                                            + "/viewSubmissions.action?taskId=" + assignment.getId()
                                            + "&tabId=" + Tab.SUBMISSIONS.getValue()
                                            + "&dbId=" + databaseID;

                                    Notification notification = new Notification((masterAccount2.getId()), masterCourseID, Notification.SUBMISSION_DEADLINE, notificationLink);
                                    notification.setMessage(message);
                                    notification.createNotification(masterCon, courseCon);
                                }
                            }
                        }
                        connectionManager.freeConnection(courseCon);
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
            }
            connectionManager.freeConnection(masterCon);
        }

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
                if(notification.isReadByUser())
                    continue;

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
