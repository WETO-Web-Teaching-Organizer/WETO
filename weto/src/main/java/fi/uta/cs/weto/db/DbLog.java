package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for table Log.
 * 
 */
public class DbLog extends SqlAssignableObject implements Cloneable {
    private SqlInteger taskIdData;
    private SqlInteger userIdData;
    private SqlInteger eventData;
    private SqlInteger par1Data;
    private SqlInteger par2Data;
    private SqlLongvarchar addressData;
    private SqlInteger timeStampData;
    private SqlInteger courseTaskIdData;

    /**
     * Default constructor.
     */
    public DbLog() {
        super();
        taskIdData = new SqlInteger();
        userIdData = new SqlInteger();
        eventData = new SqlInteger();
        par1Data = new SqlInteger();
        par2Data = new SqlInteger();
        addressData = new SqlLongvarchar();
        timeStampData = new SqlInteger();
        courseTaskIdData = new SqlInteger();
    }

    /**
     * Updates the data from the given ResultSet object.
     * @param resultSet ResultSet object containing the data.
     * @param baseIndex Base index of the columns in the ResultSet (exclusive).
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     */
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        taskIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+1) );
        userIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        eventData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
        par1Data.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+4) );
        par2Data.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+5) );
        addressData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+6) );
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+7) );
        courseTaskIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+8) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Log\n");
        sb.append("taskId:" +  taskIdData.toString() + "\n");
        sb.append("userId:" +  userIdData.toString() + "\n");
        sb.append("event:" +  eventData.toString() + "\n");
        sb.append("par1:" +  par1Data.toString() + "\n");
        sb.append("par2:" +  par2Data.toString() + "\n");
        sb.append("address:" +  addressData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("courseTaskId:" +  courseTaskIdData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbLog) ) return false;
        DbLog dbObj = (DbLog)obj;
        if( !taskIdData.equals( dbObj.taskIdData ) ) return false;
        if( !userIdData.equals( dbObj.userIdData ) ) return false;
        if( !eventData.equals( dbObj.eventData ) ) return false;
        if( !par1Data.equals( dbObj.par1Data ) ) return false;
        if( !par2Data.equals( dbObj.par2Data ) ) return false;
        if( !addressData.equals( dbObj.addressData ) ) return false;
        if( !timeStampData.equals( dbObj.timeStampData ) ) return false;
        if( !courseTaskIdData.equals( dbObj.courseTaskIdData ) ) return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Gets the raw data object for taskId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTaskIdData() {
        return taskIdData;
    }

    /**
     * Gets the raw data object for userId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getUserIdData() {
        return userIdData;
    }

    /**
     * Gets the raw data object for event attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getEventData() {
        return eventData;
    }

    /**
     * Gets the raw data object for par1 attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getPar1Data() {
        return par1Data;
    }

    /**
     * Gets the raw data object for par2 attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getPar2Data() {
        return par2Data;
    }

    /**
     * Gets the raw data object for address attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getAddressData() {
        return addressData;
    }

    /**
     * Gets the raw data object for timeStamp attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTimeStampData() {
        return timeStampData;
    }

    /**
     * Gets the raw data object for courseTaskId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getCourseTaskIdData() {
        return courseTaskIdData;
    }

    /**
     * Inserts this object to the database table Log.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     */
    public void insert(Connection con) throws SQLException, ObjectNotValidException {
        if( !taskIdData.isValid() ) throw new ObjectNotValidException("taskId");
        if( !userIdData.isValid() ) throw new ObjectNotValidException("userId");
        if( !eventData.isValid() ) throw new ObjectNotValidException("event");
        if( !par1Data.isValid() ) throw new ObjectNotValidException("par1");
        if( !par2Data.isValid() ) throw new ObjectNotValidException("par2");
        if( !addressData.isValid() ) throw new ObjectNotValidException("address");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        if( !courseTaskIdData.isValid() ) throw new ObjectNotValidException("courseTaskId");
        String prepareString = "insert into Log (taskId, userId, event, par1, par2, address, timeStamp, courseTaskId) values (?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, taskIdData.jdbcGetValue());
        ps.setObject(2, userIdData.jdbcGetValue());
        ps.setObject(3, eventData.jdbcGetValue());
        ps.setObject(4, par1Data.jdbcGetValue());
        ps.setObject(5, par2Data.jdbcGetValue());
        ps.setObject(6, addressData.jdbcGetValue());
        ps.setObject(7, timeStampData.jdbcGetValue());
        ps.setObject(8, courseTaskIdData.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows != 1 ) throw new SQLException("Insert did not return 1 row");
    }

    // The relation has no primary key. "update" method can not be generated.

    // The relation has no primary key. "delete" method can not be generated.

    // The relation has no primary key. "select" method can not be generated.

    /**
     * Constructs and returns a selection iterator for rows in database table Log.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type Log. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from Log";
        else
            prepareString = "select * from Log where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, Log.class );
    }
}

// End of file.
