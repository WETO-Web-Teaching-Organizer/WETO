package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlAssignableObject;
import fi.uta.cs.weto.model.NotificationTemplate;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import fi.uta.cs.weto.util.WetoUtilities;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

public class Notification extends SqlAssignableObject implements Cloneable {
    public static final String FORUM_POST = "forum_post";
    public static final String FORUM_TOPIC= "forum_topic";
    public static final String MASS_NOTIFICATION = "mass_notification";

    // Create a list of the different types
    public static final List<String> notificationTypes = Collections.unmodifiableList(Arrays.asList(FORUM_POST, FORUM_TOPIC, MASS_NOTIFICATION));

    private static final Logger logger = Logger.getLogger(Notification.class);

    private int id;
    private int userId;
    private int courseId;
    private String type;
    private String message;
    private int timestamp;
    private boolean readByUser;
    private boolean sentByEmail;
    private String link;

    public Notification() {
        super();
        message = null;
        readByUser = false;
        sentByEmail = false;
        link = null;
    }

    public Notification(int userId, int courseId, String type, String link) {
        super();

        this.userId = userId;
        this.courseId = courseId;
        this.type = type;
        this.message = null;
        this.readByUser = false;
        this.sentByEmail = false;
        this.link = link;
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

    public WetoTimeStamp getTimestampAsObject() {
        try {
            return new WetoTimeStamp(timestamp);
        } catch (WetoTimeStampException e) {
            return null;
        }
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setMessageFromTemplate(HashMap<String, String> valueMap) throws NoSuchItemException {
        this.message = getMessageFromTemplate(this.type, valueMap);
    }

    /**
     *
     * @param notificationType String Type of the notification. Used to retrieve the correct template.
     * @param valueMap Map of values to replace into the string. Key is the string that's searched from
     *                 the message template.
     * @return String Notification message in its final form
     * @throws NoSuchItemException
     */
    public static String getMessageFromTemplate(String notificationType, HashMap<String, String> valueMap) throws NoSuchItemException {
        String template = NotificationTemplate.getTemplateFromResource(notificationType).getTemplate();

        for(String key : valueMap.keySet()) {
            template = template.replaceAll(key, valueMap.get(key));
        }

        return template;
    }

    /**
     * Function used to create notifications (with the stored values of the object) and save them to the database.
     * @param masterConnection Connection to the master database
     * @param courseConnection Connection to the course database in question
     * @throws WetoTimeStampException In the case something goes wrong with timestamp creation
     * @throws SQLException In the case that the notification cannot be saved to the database
     * @throws NoSuchItemException In the case that no notification settings could be created/found for the given type
     */
    public void createNotification(Connection masterConnection, Connection courseConnection) throws WetoTimeStampException, SQLException, NoSuchItemException {
        try {
            // Check the user notification settings
            int courseDbTaskId = CourseImplementation.select1ByMasterTaskId(masterConnection, courseId)
                    .getCourseTaskId();
            int courseDbUserId = UserIdReplication.select1ByMasterDbUserId(courseConnection, userId)
                    .getCourseDbUserId();

            ArrayList<NotificationSetting> userSettings = NotificationSetting.createSettings(courseConnection, courseDbUserId, courseDbTaskId);
            NotificationSetting userSetting = null;
            for(NotificationSetting setting : userSettings) {
                if(setting.getType().equals(type)) {
                    userSetting = setting;
                }
            }

            if(userSetting == null) {
                throw new NoSuchItemException("User setting missing");
            }


            if(!userSetting.isNotifications()) {
                return;
            }

            if(!userSetting.isEmailNotifications()) {
                this.setSentByEmail(true); // Mark notification as sent to avoid sending it later
            }

            this.timestamp = new WetoTimeStamp().getTimeStamp();
            insert(masterConnection);
        }
        catch (Exception e) {
            logger.error("Failed to create notification", e);
            throw e;
        }
    }

    /**
     * Retrieves unsent notifications for all users
     * @param masterConnection Connection to the master database
     * @return List of unsent notifications
     * @throws SQLException In the case something goes wrong with the SQL query
     */
    public static ArrayList<Notification> getNotificationsNotSentByEmail(Connection masterConnection) throws SQLException {
        ArrayList<Notification> notifications = new ArrayList<>();

        String sqlStatement = "SELECT id, userId, courseId, type, message, timestamp, readByUser, sentByEmail, link FROM Notification WHERE sentByEmail = false";
        try (PreparedStatement preparedStatement = masterConnection.prepareStatement(sqlStatement)) {
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                notifications.add(initFromResultSet(rs));
            }
            rs.close();
        }

        return notifications;
    }

    /**
     * Inserts the values stored in the object instance to the SQL database
     * @param con Connection to the master database
     * @throws SQLException In the case the insertion could not be executed
     */
    public void insert(Connection con) throws SQLException {
        int rows = 0;

        String sqlStatement = "INSERT INTO Notification (userId, courseId, type, message, timestamp, readByUser, sentByEmail, link) values (?, ?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = con.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            ps.setString(3, type);
            ps.setString(4, message);
            ps.setInt(5, timestamp);
            ps.setBoolean(6, readByUser);
            ps.setBoolean(7, sentByEmail);
            ps.setString(8, link);

            rows = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()) {
                this.id = rs.getInt(1);
            }
            rs.close();
        }

        if( rows != 1 ) throw new SQLException("Insert did not return a row");
    }

    /**
     * Updates the values stored in the object instance to the database
     * @param con Connection to the master database
     * @throws SQLException In the case the update execution fails
     */
    public void update(Connection con) throws SQLException {
        int rows;

        String sqlStatement = "UPDATE Notification SET userId = ?, courseId = ?, type = ?, message = ?, timestamp = ?, readByUser = ?, sentByEmail = ?, link = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlStatement)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            ps.setString(3, type);
            ps.setString(4, message);
            ps.setInt(5, timestamp);
            ps.setBoolean(6, readByUser);
            ps.setBoolean(7, sentByEmail);
            ps.setString(8, link);

            ps.setInt(9, id);

            rows = ps.executeUpdate();
        }

        if( rows != 1 ) throw new SQLException("Update did not return a row");
    }

    /**
     * Retrieves values from the database by the id set in the object instance
     * @param con Connection to the master database
     * @throws SQLException In the case something goes wrong with the query execution
     * @throws InvalidValueException In the case setting the object instance values fail
     * @throws NoSuchItemException In the case a corresponding row could not be found from the database
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "SELECT id, userId, courseId, type, message, timestamp, readByUser, sentByEmail, link FROM Notification WHERE id = ?";

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
     * Deletes notification corresponding the object instance
     * @param con Connection to the master database
     * @throws SQLException In case query execution fails
     * @throws NoSuchItemException In case a corresponding notification couldn't be found in the database
     */
    public void delete(Connection con) throws SQLException, NoSuchItemException {
        int rows = 0;
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM Notification WHERE id = ?")) {
            ps.setInt(1, id);

            rows = ps.executeUpdate();

            if(rows != 1) {
                throw new NoSuchItemException("No notifications with specified id");
            }
        }
    }

    public static int getCountOfUnreadNotificationsByUser(Connection connection, int userId) {
        String query = "SELECT COUNT(*) AS count FROM Notification WHERE userId = ? AND readByUser = FALSE";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    return resultSet.getInt("count");
                } else {
                    return 0;
                }
            }
        }
        catch (SQLException e) {
            return 0;
        }
    }

    public static ArrayList<Notification> getNotificationsByFilters(Connection connection, int userId, Integer courseId, String notificationType, boolean dateDesc, boolean markAsRead) throws SQLException, InvalidValueException, NoSuchItemException, CloneNotSupportedException {
        ArrayList<Notification> notifications = new ArrayList<>();
        String orderByDate;

        if (dateDesc) {
            orderByDate = "DESC";
        }
        else {
            orderByDate = "ASC";
        }

        // Build the query
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Notification WHERE userId = ?");
        if(courseId != null)
            queryBuilder.append(" AND courseId = ?");
        if(notificationType != null)
            queryBuilder.append(" AND type = ?");
        queryBuilder.append(" ORDER BY timestamp ").append(orderByDate);

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
            // Set values
            preparedStatement.setInt(1, userId);
            int statementIndex = 2;
            if(courseId != null) {
                preparedStatement.setInt(statementIndex, courseId);
                statementIndex++;
            }
            if(notificationType != null) {
                preparedStatement.setString(statementIndex, notificationType);
            }

            // Execute query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    Notification notification = Notification.initFromResultSet(resultSet);
                    notifications.add(notification);
                    if (markAsRead && !notification.readByUser) {
                        // Save as read to the db
                        notification.setReadByUser(true);
                        notification.update(connection);
                        notification.setReadByUser(false);
                    }
                }

                if(notifications.size() == 0) {
                    throw new NoSuchItemException();
                }
            }
        }

        return notifications;
    }

    /**
     * Deletes all notifications by given course id
     * @param connection Connection to the master database
     * @param courseId Integer id of the course
     * @throws SQLException In the case the query execution fails
     */
    public static void deleteByCourseId(Connection connection, int courseId) throws SQLException {
        String prepareString = "DELETE FROM Notification WHERE courseId = ?";

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
        id = resultSet.getInt(baseIndex+1);
        userId = resultSet.getInt(baseIndex+2);
        courseId = resultSet.getInt(baseIndex+3);
        type = resultSet.getString(baseIndex+4);
        message = resultSet.getString(baseIndex+5);
        timestamp = resultSet.getInt(baseIndex+6);
        readByUser = resultSet.getBoolean(baseIndex+7);
        sentByEmail = resultSet.getBoolean(baseIndex+8);
        link = resultSet.getString(baseIndex+9);
    }

    /**
     * Populates the object instance with values in the ResultSet, utilizes column names in retrieval
     * @param resultSet ResultSet of the SQL query
     * @throws SQLException In the case that a value couldn't be retrieved from the ResultSet
     * @throws InvalidValueException In the case that the value type didn't correspond to what it was supposed to be
     */
    public void setFromResultSet(ResultSet resultSet) throws SQLException, InvalidValueException {
        id = resultSet.getInt("id");
        userId = resultSet.getInt("userId");
        courseId = resultSet.getInt("courseId");
        type = resultSet.getString("type");
        message = resultSet.getString("message");
        timestamp = resultSet.getInt("timestamp");
        readByUser = resultSet.getBoolean("readByUser");
        sentByEmail = resultSet.getBoolean("sentByEmail");
        link = resultSet.getString("link");
    }

    /**
     * Populates a new object instance from given ResultSet
     * @param resultSet ResultSet of the SQL query
     * @return Populated object instance
     * @throws SQLException In the case the object couldn't be populated
     */
    public static Notification initFromResultSet(ResultSet resultSet) throws SQLException {
        try {
            Notification result = new Notification();

            result.setFromResultSet(resultSet);

            return result;
        } catch (InvalidValueException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Maps the list of notification types to displayable strings specified in the message resources
     * @return Map with the notification type being the key and displayable string being the value.
     */
    public static HashMap<String, String> getTypeDisplayMap() {
        HashMap<String, String> typeMap = new HashMap<>();

        for(String type : notificationTypes) {
            typeMap.put(type, WetoUtilities.getMessageResource("notification." + type));
        }

        return typeMap;
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
                "link:" + link + "\n" +
                "\n");
    }
}
