package fi.uta.cs.weto.util;

import com.opensymphony.xwork2.ActionContext;
import fi.uta.cs.weto.db.*;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoTimeStamp;
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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
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
               WetoUtilities.getPackageResource("notification.permissionExpirationCheckInterval.minutes"));
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
            //Lets find all currently active tasks and their databases
            ArrayList<Integer> databases = new ArrayList<>();
            for (Permission masterTask : activeTasks) {
                try {

                    int databaseID = CourseImplementation.select1ByMasterTaskId(masterCon,masterTask.getTaskId()).getDatabaseId();

                    //iterate a course database only once
                    if (!databases.contains(databaseID)) {
                        databases.add(databaseID);
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
                             try {
                                 WetoTimeStamp permissionStamp = new WetoTimeStamp(permission.getEndDate());
                                 WetoTimeStamp deadlineStamp = new WetoTimeStamp(permission.getEndDate());
                                 deadlineStamp.setHour(deadlineStamp.getHour() - 4);
                                 WetoTimeStamp nowStamp = new WetoTimeStamp(new GregorianCalendar());
                                 boolean isAfterTime = nowStamp.getTimeStamp() >= deadlineStamp.getTimeStamp();
                                 boolean isBeforeDeadline = nowStamp.getTimeStamp() <= permissionStamp.getTimeStamp();
                                 if (!(isAfterTime && isBeforeDeadline)) continue;
                             } catch (Exception e) {
                                 logger.debug("Something went wrong: " + e);
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
                                         Notification notification = new Notification(teacher.getUserId(), masterCourseId, Notification.PERMISSION_EXPIRATION, link);
                                         notification.setMessage(notificationMessage);
                                         notification.createNotification(masterCon, courseCon);

                                     }
                                 }
                                 catch (Exception ignored) {
                                 }
                             }

                            //Check if permission is submission permission
                            if (permission.getType() == 1) {
                                HashSet<Integer> notSubmittedStudents = new HashSet<>();
                                int assignmentTaskID = permission.getTaskId();

                                Task permissionsTask = Task.select1ById(courseCon,assignmentTaskID);
                                if (!permissionsTask.getHasSubmissions()) {
                                    continue;
                                }

                                boolean isAllUsersPermission = permission.getUserRefId() == null;
                                if (isAllUsersPermission) {
                                    try {
                                        //Task temp = Task.select1ById(courseCon, permission.getTaskId());
                                        //ArrayList<ClusterMember> memberList = ClusterMember.selectByClusterId(courseCon, temp.getRootTaskId());
                                        for (UserTaskView student : UserTaskView.selectByTaskIdAndClusterType(courseCon, permissionsTask.getId(), ClusterType.STUDENTS.getValue())) {
                                            notSubmittedStudents.add(student.getUserId());
                                        }

                                        //for (ClusterMember member : memberList) {
                                            //notSubmittedStudents.add(member.getUserId());
                                        //}
                                    } catch (Exception e) {
                                        logger.error(e);
                                    }
                                }

                                //one tasks submissions
                                ArrayList<Submission> taskSubmissions = Submission.selectByTaskId(courseCon, assignmentTaskID);
                                for (Submission sub : taskSubmissions) {
                                    int status = sub.getStatus();
                                    //submission status 2 == accepted
                                    if (status == 2 && isAllUsersPermission) {
                                        try {
                                            notSubmittedStudents.remove(sub.getUserId());
                                        } catch (Exception e) {
                                            logger.debug("There was no id to remove");
                                        }
                                    }
                                    if (status != 2 && !isAllUsersPermission) {
                                        notSubmittedStudents.add(sub.getUserId());
                                    }
                                }

                                    for (int student : notSubmittedStudents) {
                                        UserAccount user = UserAccount.select1ById(courseCon, student);
                                        UserAccount masterUser = UserAccount.select1ByLoginName(masterCon, user.getLoginName());
                                        //int taskID = permission.getTaskId();

                                        int masterCourseID = CourseImplementation.select1ByDatabaseIdAndCourseTaskId(masterCon, databaseID, permissionsTask.getRootTaskId()).getMasterTaskId();

                                        // Create notification's message for students who has not made submission.
                                        HashMap<String, String> valueMap = new HashMap<>();
                                        valueMap.put("&task;", permissionsTask.getName());
                                        valueMap.put("&expirationTime;", new WetoTimeStamp(permission.getEndDate()).toString());

                                        String message = Notification.getMessageFromTemplate(Notification.SUBMISSION_DEADLINE, valueMap);

                                        // Create link.
                                        String notificationLink = WetoUtilities.getPackageResource("weto.appBaseUrl")
                                                + "/viewSubmissions.action?taskId=" + permissionsTask.getId()
                                                + "&tabId=" + Tab.SUBMISSIONS.getValue()
                                                + "&dbId=" + databaseID;

                                        Notification notification = new Notification(student, masterCourseID, Notification.SUBMISSION_DEADLINE, notificationLink);
                                        notification.setMessage(message);
                                        notification.createNotification(masterCon, courseCon);


                                        //StringBuilder link = new StringBuilder();
                                        //link.append("/weto5/viewSubmissions.action?taskId=").append(taskID).append("&tabId=4&dbId=").append(databaseID);

                                        ///ActionContext.getContext().getName()
                                        ////weto5/viewForumTopic.action?taskId=8&tabId=5&dbId=1&topicId=43
                                        //get courses ID in the master database
                                        //int masterTaskID = CourseImplementation.select1ByDatabaseIdAndCourseTaskId(masterCon,databaseID,courseID).getMasterTaskId();
                                        //try {
                                            //Notification notification = new Notification(masterUser.getId(), masterTaskID, Notification.SUBMISSION_DEADLINE, link.toString());
                                            //notification.setMessage(permissionsTask.getName());
                                            //notification.createNotification(masterCon, courseCon);
                                        //} catch (Exception e) {
                                            //logger.error(e);
                                        //}


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
                int userId = notification.getUserId();

                if(notificationMap.get(userId) == null) {
                    notificationMap.put(userId, new ArrayList<Notification>());
                }
                notificationMap.get(userId).add(notification);
            }

            // Setup Velocity templates for the email
            final String templateName = "NotificationEmailTemplate.vm";

            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
            velocityEngine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
            velocityEngine.setProperty("runtime.log.logsystem.log4j.category", "velocity");
            velocityEngine.setProperty("runtime.log.logsystem.log4j.logger", "velocity");

            Template template;
            try {
                velocityEngine.init();
                template = velocityEngine.getTemplate(templateName, "UTF-8");
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

                try (StringWriter stringWriter = new StringWriter()) {
                    String emailSubject = String.format("WETO: %s new notification(s)", notifications.size());
                    String emailMessage;

                    // Set up the context for velocity and evaluate the template
                    VelocityContext velocityContext = new VelocityContext();
                    velocityContext.put("emailTitle", emailSubject);
                    velocityContext.put("courseNames", courseNameMap);
                    velocityContext.put("notifications", notifications);
                    velocityContext.put("notificationTypeMap", Notification.getTypeDisplayMap());

                    template.merge(velocityContext, stringWriter);

                    stringWriter.flush();
                    emailMessage = stringWriter.toString();

                    Email.sendMail(userAccount.getEmail(), emailSubject, emailMessage);
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
