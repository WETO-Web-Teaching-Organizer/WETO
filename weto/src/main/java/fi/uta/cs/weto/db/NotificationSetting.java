package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlAssignableObject;
import fi.uta.cs.weto.model.ClusterType;

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
                setFromResultSet(rs);
            } else {
                throw new NoSuchItemException();
            }
        } finally {
            if(rs != null) {
                rs.close();
            }
        }
    }

    /**
     * Retrieves a list of settings by user id and course id
     * @param connection Connection to the course database
     * @param userId The user id in the course database
     * @param courseId The course id in the course database
     * @return List of settings
     * @throws SQLException In the case the retrieval fails
     */
    public static ArrayList<NotificationSetting> selectByUserAndCourse(Connection connection, int userId, int courseId) throws SQLException {
        String prepareString = "SELECT id, userId, courseId, type, notifications, emailNotifications FROM NotificationSetting WHERE userId = ? AND courseId = ?";

        ResultSet rs = null;
        ArrayList<NotificationSetting> settings = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(prepareString)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);

            rs = ps.executeQuery();
            while (rs.next()) {
                settings.add(initFromResultSet(rs));
            }
        } finally {
            if(rs != null) {
                rs.close();
            }
        }

        return settings;
    }

    /**
     * Retrieves a setting by user, course and type
     * @param connection Connection to the course database
     * @param userId The user id in the course database
     * @param courseId The course id in the course database
     * @param type Type of the setting
     * @return Instance of the corresponding setting
     * @throws SQLException In the case the retrieval fails
     * @throws NoSuchItemException In the case no/too many corresponding database rows were found
     */
    public static NotificationSetting select1ByUserCourseAndType(Connection connection, int userId, int courseId, String type) throws SQLException, NoSuchItemException {
        String prepareString = "SELECT id, userId, courseId, type, notifications, emailNotifications FROM NotificationSetting WHERE userId = ? AND courseId = ? AND type = ?";

        ResultSet rs = null;
        try (PreparedStatement ps = connection.prepareStatement(prepareString)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            ps.setString(3, type);

            rs = ps.executeQuery();
            if(rs.next()) {
                return initFromResultSet(rs);
            } else {
                throw new NoSuchItemException();
            }
        } finally {
            if(rs != null) {
                rs.close();
            }
        }
    }

    /**
     * Deletes notification settings by course id
     * @param connection Connection to the course database
     * @param courseId Id of the course in the course database
     * @throws SQLException If the deletion fails
     */
    public static void deleteByCourseId(Connection connection, int courseId) throws SQLException {
        String prepareString = "DELETE FROM NotificationSetting WHERE courseId = ?";

        try (PreparedStatement ps = connection.prepareStatement(prepareString)) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }

    /**
     * Populates the object instance with values in the ResultSet
     * @param resultSet ResultSet of the SQL query
     * @param baseIndex Integer offset used to retrieve columns
     * @throws SQLException In the case that a value couldn't be retrieved from the ResultSet
     * @throws InvalidValueException In the case that the value type didn't correspond to what it was supposed to be
     */
    @Override
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        this.id = resultSet.getInt(baseIndex + 1);
        this.userId = resultSet.getInt(baseIndex + 2);
        this.courseId = resultSet.getInt(baseIndex + 3);
        this.type = resultSet.getString(baseIndex + 4);
        this.notifications = resultSet.getBoolean(baseIndex + 5);
        this.emailNotifications = resultSet.getBoolean(baseIndex + 6);
    }

    /**
     * Populates the object instance with values in the ResultSet, utilizes column names in retrieval
     * @param resultSet ResultSet of the SQL query
     * @throws SQLException In the case that a value couldn't be retrieved from the ResultSet
     * @throws InvalidValueException In the case that the value type didn't correspond to what it was supposed to be
     */
    public void setFromResultSet(ResultSet resultSet) throws SQLException, InvalidValueException {
        this.id = resultSet.getInt("id");
        this.userId = resultSet.getInt("userId");
        this.courseId = resultSet.getInt("courseId");
        this.type = resultSet.getString("type");
        this.notifications = resultSet.getBoolean("notifications");
        this.emailNotifications = resultSet.getBoolean("emailNotifications");
    }

    /**
     * Populates a new object instance from given ResultSet
     * @param resultSet ResultSet of the SQL query
     * @return Populated object instance
     * @throws SQLException In the case the object couldn't be populated
     */
    public static NotificationSetting initFromResultSet(ResultSet resultSet) throws SQLException {
        NotificationSetting result = new NotificationSetting();

        try {
            result.setFromResultSet(resultSet);
        } catch (InvalidValueException e) {
            throw new SQLException(e);
        }

        return result;
    }

    /**
     * Retrieves existing settings, and in the case a setting doesn't exist for a certain Notification type,
     * it creates the missing settings
     * @param connection Connection to the course database
     * @param userId Id of the user (in the course database)
     * @param courseId Id of the course  (in the course database)
     * @return List of all settings for the user by given course
     * @throws SQLException In the case retrieval or creation of the settings fail
     */
    public static ArrayList<NotificationSetting> createSettings(Connection connection, int userId, int courseId) throws SQLException {
        ArrayList<NotificationSetting> settings = new ArrayList<>();
        ArrayList<NotificationSetting> existingSettings = selectByUserAndCourse(connection, userId, courseId);

        for(String notificationType : Notification.notificationTypes) {

            boolean isStudent = UserTaskView.selectByUserIdAndClusterType(connection, userId, ClusterType.TEACHERS.getValue()).isEmpty();

            if(notificationType.equals(Notification.PERMISSION_EXPIRATION) && isStudent) {
                continue;

            }
            if (notificationType.equals(Notification.SUBMISSION_DEADLINE) && !isStudent) {
                continue;

            }


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
                continue;
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
