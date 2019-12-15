package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for table CourseImplementation.
 * Describes the various courses
 */
public class DbCourseImplementation extends SqlAssignableObject implements Cloneable {
    private SqlInteger masterTaskIdData;
    private SqlInteger masterTaskIdKC;
    private SqlInteger subjectIdData;
    private SqlInteger databaseIdData;
    private SqlInteger statusData;
    private SqlInteger timeStampData;
    private SqlInteger courseTaskIdData;

    /**
     * Default constructor.
     */
    public DbCourseImplementation() {
        super();
        masterTaskIdData = new SqlInteger();
        masterTaskIdKC = new SqlInteger();
        subjectIdData = new SqlInteger();
    // The database where the course is stored.
        databaseIdData = new SqlInteger();
        statusData = new SqlInteger();
        timeStampData = new SqlInteger();
        courseTaskIdData = new SqlInteger();
        masterTaskIdData.setPrime(true);
    }

    /**
     * Updates the data from the given ResultSet object.
     * @param resultSet ResultSet object containing the data.
     * @param baseIndex Base index of the columns in the ResultSet (exclusive).
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     */
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        masterTaskIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+1) );
        masterTaskIdKC.jdbcSetValue(masterTaskIdData.jdbcGetValue());
        subjectIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        databaseIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
        statusData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+4) );
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+5) );
        courseTaskIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+6) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CourseImplementation\n");
        sb.append("masterTaskId:" +  masterTaskIdData.toString() + "\n");
        sb.append("subjectId:" +  subjectIdData.toString() + "\n");
        sb.append("databaseId:" +  databaseIdData.toString() + "\n");
        sb.append("status:" +  statusData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("courseTaskId:" +  courseTaskIdData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbCourseImplementation) ) return false;
        DbCourseImplementation dbObj = (DbCourseImplementation)obj;
        if( !masterTaskIdData.equals( dbObj.masterTaskIdData ) ) return false;
        if( !subjectIdData.equals( dbObj.subjectIdData ) ) return false;
        if( !databaseIdData.equals( dbObj.databaseIdData ) ) return false;
        if( !statusData.equals( dbObj.statusData ) ) return false;
        if( !timeStampData.equals( dbObj.timeStampData ) ) return false;
        if( !courseTaskIdData.equals( dbObj.courseTaskIdData ) ) return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Gets the raw data object for masterTaskId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getMasterTaskIdData() {
        return masterTaskIdData;
    }

    /**
     * Gets the raw data object for subjectId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getSubjectIdData() {
        return subjectIdData;
    }

    /**
     * Gets the raw data object for databaseId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getDatabaseIdData() {
        return databaseIdData;
    }

    /**
     * Gets the raw data object for status attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getStatusData() {
        return statusData;
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
     * Inserts this object to the database table CourseImplementation.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     */
    public void insert(Connection con) throws SQLException, ObjectNotValidException {
        if( !masterTaskIdData.isValid() ) throw new ObjectNotValidException("masterTaskId");
        if( !subjectIdData.isValid() ) throw new ObjectNotValidException("subjectId");
        if( !databaseIdData.isValid() ) throw new ObjectNotValidException("databaseId");
        if( !statusData.isValid() ) throw new ObjectNotValidException("status");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        if( !courseTaskIdData.isValid() ) throw new ObjectNotValidException("courseTaskId");
        String prepareString = "insert into CourseImplementation (masterTaskId, subjectId, databaseId, status, timeStamp, courseTaskId) values (?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterTaskIdData.jdbcGetValue());
        ps.setObject(2, subjectIdData.jdbcGetValue());
        ps.setObject(3, databaseIdData.jdbcGetValue());
        ps.setObject(4, statusData.jdbcGetValue());
        ps.setObject(5, timeStampData.jdbcGetValue());
        ps.setObject(6, courseTaskIdData.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows != 1 ) throw new SQLException("Insert did not return 1 row");
    }

    /**
     * Updates the row of this object in the database table CourseImplementation.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be updated does not exist or is not unique.
     */
    public void update(Connection con) throws SQLException, ObjectNotValidException, NoSuchItemException {
        if( !masterTaskIdData.isValid() ) throw new ObjectNotValidException("masterTaskId");
        if( !subjectIdData.isValid() ) throw new ObjectNotValidException("subjectId");
        if( !databaseIdData.isValid() ) throw new ObjectNotValidException("databaseId");
        if( !statusData.isValid() ) throw new ObjectNotValidException("status");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        if( !courseTaskIdData.isValid() ) throw new ObjectNotValidException("courseTaskId");
        String prepareString = "update CourseImplementation set masterTaskId = ?, subjectId = ?, databaseId = ?, status = ?, timeStamp = ?, courseTaskId = ? where masterTaskId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterTaskIdData.jdbcGetValue());
        ps.setObject(2, subjectIdData.jdbcGetValue());
        ps.setObject(3, databaseIdData.jdbcGetValue());
        ps.setObject(4, statusData.jdbcGetValue());
        ps.setObject(5, timeStampData.jdbcGetValue());
        ps.setObject(6, courseTaskIdData.jdbcGetValue());
        ps.setObject(7, masterTaskIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        try {
            masterTaskIdKC.jdbcSetValue(masterTaskIdData.jdbcGetValue());
        } catch( InvalidValueException e ) {
            // Ignored (isValid already called)
        }
        if( rows != 1 ) throw new NoSuchItemException();
    }

    /**
     * Deletes the row of this object from the database table CourseImplementation.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws TooManyItemsException if the row to be deleted is not unique.
     * @throws NoSuchItemException if the row to be deleted does not exist.
     */
    public void delete(Connection con) throws SQLException, TooManyItemsException, NoSuchItemException {
        String prepareString = "delete from CourseImplementation where masterTaskId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterTaskIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows > 1 ) throw new TooManyItemsException();
        if( rows < 1 ) throw new NoSuchItemException();
    }

    /**
     * Selects the row of this object from the database table CourseImplementation and updates the attributes accordingly.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from CourseImplementation where masterTaskId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterTaskIdData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database table CourseImplementation.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type CourseImplementation. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from CourseImplementation";
        else
            prepareString = "select * from CourseImplementation where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, CourseImplementation.class );
    }
}

// End of file.
