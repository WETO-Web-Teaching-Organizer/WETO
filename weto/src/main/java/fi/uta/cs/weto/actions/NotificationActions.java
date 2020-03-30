package fi.uta.cs.weto.actions;

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
            } catch (SQLException e) {
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

                    setting.setNotifications(Boolean.parseBoolean(settingsMap.get(setting.getType() + "_notifications")));
                    setting.setEmailNotifications(Boolean.parseBoolean(settingsMap.get(setting.getType() + "_emailNotifications")));

                    // Make sure that notifications are on if email notifications are checked
                    if(setting.isEmailNotifications()) {
                        setting.setNotifications(true);
                    }

                    setting.update(courseConnection);
                }
            } catch (Exception e) {
                throw new WetoActionException("Failed to save settings");
            }

            return SUCCESS;
        }
    }
}
