package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlAssignableObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationTemplate extends SqlAssignableObject implements Cloneable {
    private int id;
    private String type;
    private String template;

    public NotificationTemplate() {
        type = null;
        template = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public static NotificationTemplate selectByType(Connection con, String type) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "SELECT id, type, template FROM NotificationTemplate WHERE type = ?";

        ResultSet rs = null;
        try (PreparedStatement ps = con.prepareStatement(prepareString)) {
            ps.setString(1, type);
            rs = ps.executeQuery();
            if (rs.next()) {
                NotificationTemplate template = new NotificationTemplate();
                template.setFromResultSet(rs);
                return template;
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
        type = resultSet.getString(baseIndex+2);
        template = resultSet.getString(baseIndex+3);
    }

    public void setFromResultSet(ResultSet resultSet) throws SQLException, InvalidValueException {
        id = resultSet.getInt("id");
        type = resultSet.getString("type");
        template = resultSet.getString("template");
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return("NotificationTemplate\n" +
                "id:" + id + "\n" +
                "type:" + type + "\n" +
                "template:" + template + "\n" +
                "\n");
    }
}
