package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for view SubtaskView.
 * 
 */
public class DbSubtaskView extends SqlAssignableObject implements Cloneable {
    private SqlInteger rankData;
    private SqlInteger idData;
    private SqlInteger containerIdData;
    private SqlLongvarchar nameData;
    private SqlLongvarchar textData;
    private SqlInteger statusData;
    private SqlInteger componentBitsData;
    private SqlBoolean showTextInParentData;
    private SqlInteger rootTaskIdData;

    /**
     * Default constructor.
     */
    public DbSubtaskView() {
        super();
        rankData = new SqlInteger();
        idData = new SqlInteger();
        containerIdData = new SqlInteger();
        nameData = new SqlLongvarchar();
        textData = new SqlLongvarchar();
        statusData = new SqlInteger();
        componentBitsData = new SqlInteger();
        showTextInParentData = new SqlBoolean();
        rootTaskIdData = new SqlInteger();
    }

    /**
     * Updates the data from the given ResultSet object.
     * @param resultSet ResultSet object containing the data.
     * @param baseIndex Base index of the columns in the ResultSet (exclusive).
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     */
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        rankData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+1) );
        idData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        containerIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
        nameData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+4) );
        textData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+5) );
        statusData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+6) );
        componentBitsData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+7) );
        showTextInParentData.jdbcSetValue( (java.lang.Boolean) resultSet.getObject(baseIndex+8) );
        rootTaskIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+9) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SubtaskView\n");
        sb.append("rank:" +  rankData.toString() + "\n");
        sb.append("id:" +  idData.toString() + "\n");
        sb.append("containerId:" +  containerIdData.toString() + "\n");
        sb.append("name:" +  nameData.toString() + "\n");
        sb.append("text:" +  textData.toString() + "\n");
        sb.append("status:" +  statusData.toString() + "\n");
        sb.append("componentBits:" +  componentBitsData.toString() + "\n");
        sb.append("showTextInParent:" +  showTextInParentData.toString() + "\n");
        sb.append("rootTaskId:" +  rootTaskIdData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbSubtaskView) ) return false;
        DbSubtaskView dbObj = (DbSubtaskView)obj;
        if( !rankData.equals( dbObj.rankData ) ) return false;
        if( !idData.equals( dbObj.idData ) ) return false;
        if( !containerIdData.equals( dbObj.containerIdData ) ) return false;
        if( !nameData.equals( dbObj.nameData ) ) return false;
        if( !textData.equals( dbObj.textData ) ) return false;
        if( !statusData.equals( dbObj.statusData ) ) return false;
        if( !componentBitsData.equals( dbObj.componentBitsData ) ) return false;
        if( !showTextInParentData.equals( dbObj.showTextInParentData ) ) return false;
        if( !rootTaskIdData.equals( dbObj.rootTaskIdData ) ) return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Gets the raw data object for rank attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getRankData() {
        return rankData;
    }

    /**
     * Gets the raw data object for id attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getIdData() {
        return idData;
    }

    /**
     * Gets the raw data object for containerId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getContainerIdData() {
        return containerIdData;
    }

    /**
     * Gets the raw data object for name attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getNameData() {
        return nameData;
    }

    /**
     * Gets the raw data object for text attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getTextData() {
        return textData;
    }

    /**
     * Gets the raw data object for status attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getStatusData() {
        return statusData;
    }

    /**
     * Gets the raw data object for componentBits attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getComponentBitsData() {
        return componentBitsData;
    }

    /**
     * Gets the raw data object for showTextInParent attribute.
     * @return Data object as SqlBoolean.
     */
    public SqlBoolean getShowTextInParentData() {
        return showTextInParentData;
    }

    /**
     * Gets the raw data object for rootTaskId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getRootTaskIdData() {
        return rootTaskIdData;
    }

    /**
     * Selects the row of this object from the database view SubtaskView and updates the attributes accordingly.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException, ObjectNotValidException {
        String prepareString = "select * from SubtaskView where rank = ? AND id = ? AND containerId = ? AND name = ? AND text = ? AND status = ? AND componentBits = ? AND showTextInParent = ? AND rootTaskId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, rankData.jdbcGetValue());
        ps.setObject(2, idData.jdbcGetValue());
        ps.setObject(3, containerIdData.jdbcGetValue());
        ps.setObject(4, nameData.jdbcGetValue());
        ps.setObject(5, textData.jdbcGetValue());
        ps.setObject(6, statusData.jdbcGetValue());
        ps.setObject(7, componentBitsData.jdbcGetValue());
        ps.setObject(8, showTextInParentData.jdbcGetValue());
        ps.setObject(9, rootTaskIdData.jdbcGetValue());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            setFromResultSet(rs,0);
            rs.close();
            ps.close();
        } else {
            rs.close();
            ps.close();
            throw new NoSuchItemException();
        }
    }

    /**
     * Constructs and returns a selection iterator for rows in database view SubtaskView.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type SubtaskView. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from SubtaskView";
        else
            prepareString = "select * from SubtaskView where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, SubtaskView.class );
    }
}

// End of file.
