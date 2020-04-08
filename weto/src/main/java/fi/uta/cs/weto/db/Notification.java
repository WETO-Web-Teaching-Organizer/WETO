package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlAssignableObject;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import fi.uta.cs.weto.util.Email;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Notification extends SqlAssignableObject implements Cloneable {
    public static final String FORUM_POST = "forum_post";

    // Create a list of the different types
    public static final List<String> notificationTypes;
    static {
        List<String> typeList = new ArrayList<>();
        typeList.add(FORUM_POST);
        notificationTypes = Collections.unmodifiableList(typeList);
    }

    private static final Logger logger = Logger.getLogger(Notification.class);

    private int id;
    private int userId;
    private int courseId;
    private String type;
    private String message;
    private int timestamp;
    private boolean readByUser;
    private boolean sentByEmail;

    public Notification() {
        super();
        message = null;
        readByUser = false;
        sentByEmail = false;
    }

    public Notification(int userId, int courseId, String type) {
        super();

        this.userId = userId;
        this.courseId = courseId;
        this.type = type;
        this.message = null;
        this.readByUser = false;
        this.sentByEmail = false;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isReadByUser() {
        return readByUser;
    }

    public void setReadByUser(boolean readByUser) {
        this.readByUser = readByUser;
    }

    public boolean isSentByEmail() {
        return sentByEmail;
    }

    public void setSentByEmail(boolean sentByEmail) {
        this.sentByEmail = sentByEmail;
    }

    public void setMessageFromTemplate(Connection connection, HashMap<String, String> valueMap) throws InvalidValueException, SQLException, NoSuchItemException {
        String template = NotificationTemplate.selectByType(connection, getType()).getTemplate();

        for(String key : valueMap.keySet()) {
            template = template.replaceAll(key, valueMap.get(key));
        }

        this.message = template;
    }

    public void createNotification(Connection masterConnection, Connection courseConnection) {
        try {
            // Check the user notification settings
            NotificationSetting userSettings = NotificationSetting.select1ByUserCourseAndType(courseConnection, userId, courseId, type);
            if(!userSettings.isNotifications()) {
                return;
            }

            this.timestamp = new WetoTimeStamp().getTimeStamp();
            insert(masterConnection);

            // Schedule email
            if(userSettings.isEmailNotifications()) {
                UserAccount user = UserAccount.select1ById(masterConnection, userId);
                Email.scheduleEmail(user.getLoginName(), String.valueOf(getId()), user.getEmail(), "WETO Notification", getMessage());
                this.sentByEmail = true;
                update(masterConnection);
            }
        }
        catch (Exception e) {
            logger.error("Failed to create notification", e);
        }
    }

    public void insert(Connection con) throws SQLException, WetoTimeStampException {
        int rows = 0;

        String sqlStatement = "INSERT INTO Notification (userId, courseId, type, message, timestamp, readByUser, sentByEmail) values (?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = con.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            ps.setString(3, type);
            ps.setString(4, message);
            ps.setInt(5, timestamp);
            ps.setBoolean(6, readByUser);
            ps.setBoolean(7, sentByEmail);

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

        String sqlStatement = "UPDATE Notification SET id = ?, userId = ?, courseId = ?, type = ?, message = ?, readByUser = ?, sentByEmail = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.setInt(3, courseId);
            ps.setString(4, type);
            ps.setString(5, message);
            ps.setBoolean(6, readByUser);
            ps.setBoolean(7, sentByEmail);

            ps.setInt(8, id);

            rows = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()) {
                setFromResultSet(rs);
            }
            rs.close();
        }

        if( rows != 1 ) throw new SQLException("Update did not return a row");
    }

    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "SELECT id, userId, courseId, type, message, timestamp, readByUser, sentByEmail FROM Notification WHERE id = ?";

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

    @Override
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        id = resultSet.getInt(baseIndex+1);
        userId = resultSet.getInt(baseIndex+2);
        courseId = resultSet.getInt(baseIndex+3);
        type = resultSet.getString(baseIndex+4);
        message = resultSet.getString(baseIndex+5);
        timestamp = resultSet.getInt(baseIndex+6);
        readByUser = resultSet.getBoolean(baseIndex+7);
        sentByEmail = resultSet.getBoolean(baseIndex+8);
    }

    public void setFromResultSet(ResultSet resultSet) throws SQLException, InvalidValueException {
        id = resultSet.getInt("id");
        userId = resultSet.getInt("userId");
        courseId = resultSet.getInt("courseId");
        type = resultSet.getString("type");
        message = resultSet.getString("message");
        timestamp = resultSet.getInt("timestamp");
        readByUser = resultSet.getBoolean("readByUser");
        sentByEmail = resultSet.getBoolean("sentByEmail");
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return("Notification\n" +
                "id:" + id + "\n" +
                "userId:" + userId + "\n" +
                "courseId:" + courseId + "\n" +
                "type: " + type + "\n" +
                "message:" + message + "\n" +
                "timestamp:" + timestamp + "\n" +
                "readByUser:" + readByUser + "\n" +
                "sentByEmail:" + sentByEmail + "\n" +
                "\n");
    }
}
