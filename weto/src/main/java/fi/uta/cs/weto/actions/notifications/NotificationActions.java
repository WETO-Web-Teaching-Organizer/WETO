package fi.uta.cs.weto.actions.notifications;

import fi.uta.cs.weto.db.NotificationSetting;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class NotificationActions {
    public static class ViewNotificationSettings extends WetoCourseAction {
        private boolean saveFailed;
        private List<NotificationSetting> settings;

        public boolean getSaveFailed() {
            return saveFailed;
        }

        public void setSaveFailed(boolean saveFailed) {
            this.saveFailed = saveFailed;
        }

        public List<NotificationSetting> getSettings() {
            return settings;
        }

        public void setSettings(List<NotificationSetting> settings) {
            this.settings = settings;
        }

        public ViewNotificationSettings() {
            super(Tab.MAIN.getBit(), 0, 0, 0);
            settings = null;
            saveFailed = false;
        }

        @Override
        public String action() throws Exception {
            Connection courseConnection = getCourseConnection();
            int courseId = getCourseTaskId();
            int userId = getCourseUserId();

            try {
                settings = NotificationSetting.createSettings(courseConnection, userId, courseId);
            } catch (Exception e) {
                throw new WetoActionException("Failed to retrieve notification settings");
            }

            return SUCCESS;
        }
    }

    public static class SaveNotificationSettings extends WetoCourseAction {
        private Map<String, String> settingsMap;

        public Map<String, String> getSettingsMap() {
            return settingsMap;
        }

        public void setSettingsMap(Map<String, String> settingsMap) {
            this.settingsMap = settingsMap;
        }

        public SaveNotificationSettings() {
            super(Tab.MAIN.getBit(), 0, 0, 0);
        }

        @Override
        public String action() throws Exception {
            Connection courseConnection = getCourseConnection();
            int userId = getCourseUserId();
            int courseId = getCourseTaskId();

            try {
                List<NotificationSetting> currentSettings = NotificationSetting.selectByUserAndCourse(courseConnection, userId, courseId);
                for(NotificationSetting setting : currentSettings) {

                    try {
                        String notifications = settingsMap.get(setting.getType() + "_notifications");
                        setting.setNotifications(Boolean.parseBoolean(notifications));
                    } catch (NullPointerException e) {
                        setting.setNotifications(false);
                    }

                    try {
                        String emailNotifications = settingsMap.get(setting.getType() + "_emailNotifications");
                        setting.setEmailNotifications(Boolean.parseBoolean(emailNotifications));
                    } catch (NullPointerException e) {
                        setting.setEmailNotifications(false);
                    }

                    // Uncheck email notifications if notifications are off
                    if(!setting.isNotifications()) {
                        setting.setEmailNotifications(false);
                    }

                    setting.update(courseConnection);
                }
            } catch (Exception e) {
                throw new WetoActionException("Failed to save settings");
            }

            return SUCCESS;
        }
    }

    public static class ViewNotificationCenter extends WetoMasterAction {
        private ArrayList<Notification> notifications;
        private HashMap<Integer, String> notificationTypes;
        private HashMap<Integer, String> courseIdsNames;

        private Integer type;
        private Integer courseId;
        private boolean dateDesc;
        private final String ALLCOURSESOPTION = "All courses";
        private final String ALLTYPESOPTION = "allTypes";
        private String pageTitle = "Notification center";

        public ViewNotificationCenter() {
            super();
            notifications = new ArrayList<>();
            courseIdsNames = new HashMap<>();
            notificationTypes = new HashMap<>();
        }

        public ArrayList<Notification> getNotifications() {
            return notifications;
        }

        public void setNotifications(ArrayList<Notification> notifications) {
            this.notifications = notifications;
        }

        public HashMap<Integer, String> getNotificationTypes() {
            return notificationTypes;
        }

        public void setNotificationTypes(HashMap<Integer, String> notificationTypes) {
            this.notificationTypes = notificationTypes;
        }

        public HashMap<Integer, String> getCourseIdsNames() {
            return courseIdsNames;
        }

        public void setCourseIdsNames(HashMap<Integer, String> courseIdsNames) {
            this.courseIdsNames = courseIdsNames;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getCourseId() {
            return courseId;
        }

        public void setCourseId(Integer courseId) {
            this.courseId = courseId;
        }

        public boolean getDateDesc() {
            return dateDesc;
        }

        public void setDateDesc(boolean dateDesc) {
            this.dateDesc = dateDesc;
        }
        
        public String getPageTitle() {
            return pageTitle;
        }

        @Override
        public String action() throws Exception {
            Connection masterConnection = getMasterConnection();
            int userId = getMasterUserId();

            ArrayList<CourseView> courseView = CourseView.selectAll(masterConnection);
            for (int i = 0; i < courseView.size(); i++) {
               try {
                   int masterTaskId = courseView.get(i).getMasterTaskId();
                   UserTaskView.select1ByTaskIdAndUserId(masterConnection, masterTaskId, userId);
                   String name = courseView.get(i).getName();
                   courseIdsNames.put(-1, ALLCOURSESOPTION);
                   courseIdsNames.put(masterTaskId, name);
                }
                // User is not member of the course.
               catch (NoSuchItemException e) {
               }
            }

            notificationTypes.put(-1, ALLTYPESOPTION);
            for  (int i = 0; i < Notification.notificationTypes.size(); i++) {
                notificationTypes.put(i, Notification.notificationTypes.get(i));
            }

            if (courseId != null && courseId < 0) {
                courseId = null;
            }
            if (type != null && type < 0) {
                type = null;
            }

            try {
                notifications = Notification.selectNotificationsAndMarkAsRead(masterConnection, userId, courseId, notificationTypes.get(type), dateDesc);
            }
            //User haven't received any notifications.
            catch (NoSuchItemException e) {
                return SUCCESS;
            }
            catch (Exception e) {
                throw new WetoActionException("Failed to retrieve notifications");
            }

            return SUCCESS;
        }
    }
}
}
