package fi.uta.cs.weto.actions.notifications;

import fi.uta.cs.weto.db.Notification;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationActions {
    public static class NotificationCenter extends WetoCourseAction {
        private ArrayList<Notification> notifications;
        private List<String> notificationTypes;
        private HashMap<Integer, String> courseNames;

        private String type;
        private Integer courseId;
        private String date;

        // Tarkista rakentaja.
        public NotificationCenter() {
            super(Tab.MAIN.getBit(), 0, 0, 0);
            notifications = null;
            notificationTypes = null;
        }

        public ArrayList<Notification> getNotifications() {
            return notifications;
        }

        public void setNotifications(ArrayList<Notification> notifications) {
            this.notifications = notifications;
        }

        public List<String> getNotificationTypes() {
            return notificationTypes;
        }

        public void setNotificationTypes(List<String> notificationTypes) {
            this.notificationTypes = notificationTypes;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getCourseId() {
            return courseId;
        }

        public void setCourseId(Integer courseId) {
            this.courseId = courseId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public String action() throws Exception {
            /*Connection masterConnection = getMasterConnection();

            try {
                int userId = getMasterUserId();
                notifications = Notification.selectNotificationsByUser(masterConnection, userId);
            } catch (Exception e) {
                throw new WetoActionException("Failed to retrieve notifications");
            }*/
            int userId = getMasterUserId();
            notifications = new ArrayList<Notification>();
            notificationTypes = Notification.notificationTypes;
            Notification noti = new Notification(userId, 1, "testi");
            Notification noti2 = new Notification(userId, 2, "testi2");
            notifications.add(noti);
            notifications.add(noti2);

            return SUCCESS;
        }
    }
}
