package fi.uta.cs.weto.actions;

import fi.uta.cs.weto.db.NotificationSetting;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class NotificationActions {
    public static class ViewNotificationSettings extends WetoCourseAction {
        private List<NotificationSetting> settings;

        public List<NotificationSetting> getSettings() {
            return settings;
        }

        public ViewNotificationSettings() {
            super(Tab.MAIN.getBit(), 0, 0, 0);
            settings = null;
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

                    setting.update(courseConnection);
                }
            } catch (Exception e) {
                throw new WetoActionException("Failed to save settings");
            }

            return SUCCESS;
        }
    }
}
