package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlAssignableObject;

import java.sql.*;
import java.util.ArrayList;

public class NotificationSetting extends SqlAssignableObject implements Cloneable {
    private int id;
    private int userId;
    private int courseId;
    private String type;
    private boolean notifications;
    private boolean emailNotifications;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public NotificationSetting() {
        this.type = null;
    }

    public void insert(Connection con) throws SQLException {
        int rows = 0;

        String sqlStatement = "INSERT INTO NotificationSetting (userId, courseId, type, notifications, emailNotifications) values (?, ?, ?, ?, ?);";
        try (PreparedStatement ps = con.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            ps.setString(3, type);
            ps.setBoolean(4, notifications);
            ps.setBoolean(5, emailNotifications);

            rows = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()) {
                this.id = rs.getInt(1);
            }
            rs.close();
        }

        if( rows != 1 ) throw new SQLException("Insert did not return a row");
    }

    public void update(Connection con) throws SQLException, InvalidValueException {
        int rows = 0;

        String sqlStatement = "UPDATE NotificationSetting SET id = ?, userId = ?, courseId = ?, type = ?, notifications = ?, emailNotifications = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.setInt(3, courseId);
            ps.setString(4, type);
            ps.setBoolean(5, notifications);
            ps.setBoolean(6, emailNotifications);

            ps.setInt(7, id);

            rows = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()) {
                setFromResultSet(rs, 0);
            }
            rs.close();
        }

        if( rows != 1 ) throw new SQLException("Update did not return a row");
    }

    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "SELECT id, userId, courseId, type, notifications, emailNotifications FROM NotificationSetting WHERE id = ?";

        ResultSet rs = null;
        try (PreparedStatement ps = con.prepareStatement(prepareString)) {
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                setFromResultSet(rs,0);
            } else {
                throw new NoSuchItemException();
            }
        } finally {
            if(rs != null) {
                rs.close();
            }
        }
    }

    public static ArrayList<NotificationSetting> selectByUserAndCourse(Connection con, int userId, int courseId) throws SQLException {
        String prepareString = "SELECT id, userId, courseId, type, notifications, emailNotifications FROM NotificationSetting WHERE userId = ? AND courseId = ?";

        ResultSet rs = null;
        ArrayList<NotificationSetting> settings = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(prepareString)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);

            rs = ps.executeQuery();
            while (rs.next()) {
                settings.add(initFromResultSet(rs, 0));
            }
        } finally {
            if(rs != null) {
                rs.close();
            }
        }

        return settings;
    }

    @Override
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        this.id = resultSet.getInt(baseIndex + 1);
        this.userId = resultSet.getInt(baseIndex + 2);
        this.courseId = resultSet.getInt(baseIndex + 3);
        this.type = resultSet.getString(baseIndex + 4);
        this.notifications = resultSet.getBoolean(baseIndex + 5);
        this.emailNotifications = resultSet.getBoolean(baseIndex + 6);
    }

    public static NotificationSetting initFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException {
        NotificationSetting result = new NotificationSetting();

        result.setId(resultSet.getInt(baseIndex + 1));
        result.setUserId(resultSet.getInt(baseIndex + 2));
        result.setCourseId(resultSet.getInt(baseIndex + 3));
        result.setType(resultSet.getString(baseIndex + 4));
        result.setNotifications(resultSet.getBoolean(baseIndex + 5));
        result.setEmailNotifications(resultSet.getBoolean(baseIndex + 6));

        return result;
    }

    public static ArrayList<NotificationSetting> createSettings(Connection connection, int userId, int courseId) throws SQLException {
        ArrayList<NotificationSetting> settings = new ArrayList<>();
        ArrayList<NotificationSetting> existingSettings = selectByUserAndCourse(connection, userId, courseId);

        for(String notificationType : Notification.notificationTypes) {
            // Check existing settings
            boolean alreadyExists = false;
            for(NotificationSetting setting : existingSettings) {
                if(setting.getType().equals(notificationType)) {
                    settings.add(setting);
                    alreadyExists = true;
                    break;
                }
            }
            if(alreadyExists) {
                break;
            }

            NotificationSetting newSetting = new NotificationSetting();

            newSetting.setUserId(userId);
            newSetting.setCourseId(courseId);
            newSetting.setType(notificationType);
            newSetting.setNotifications(true);
            newSetting.setEmailNotifications(true);

            newSetting.insert(connection);
            settings.add(newSetting);
        }

        return settings;
    }
}
