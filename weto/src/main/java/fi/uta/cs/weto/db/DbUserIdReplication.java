package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for table UserIdReplication.
 * 
 */
public class DbUserIdReplication extends SqlAssignableObject implements Cloneable {
    private SqlInteger masterDbUserIdData;
    private SqlInteger masterDbUserIdKC;
    private SqlInteger courseDbUserIdData;
    private SqlInteger courseDbUserIdKC;
    private SqlInteger timeStampData;

    /**
     * Default constructor.
     */
    public DbUserIdReplication() {
        super();
        masterDbUserIdData = new SqlInteger();
        masterDbUserIdKC = new SqlInteger();
        courseDbUserIdData = new SqlInteger();
        courseDbUserIdKC = new SqlInteger();
        timeStampData = new SqlInteger();
        masterDbUserIdData.setPrime(true);
        courseDbUserIdData.setPrime(true);
    }

    /**
     * Updates the data from the given ResultSet object.
     * @param resultSet ResultSet object containing the data.
     * @param baseIndex Base index of the columns in the ResultSet (exclusive).
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     */
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        masterDbUserIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+1) );
        masterDbUserIdKC.jdbcSetValue(masterDbUserIdData.jdbcGetValue());
        courseDbUserIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        courseDbUserIdKC.jdbcSetValue(courseDbUserIdData.jdbcGetValue());
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("UserIdReplication\n");
        sb.append("masterDbUserId:" +  masterDbUserIdData.toString() + "\n");
        sb.append("courseDbUserId:" +  courseDbUserIdData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbUserIdReplication) ) return false;
        DbUserIdReplication dbObj = (DbUserIdReplication)obj;
        if( !masterDbUserIdData.equals( dbObj.masterDbUserIdData ) ) return false;
        if( !courseDbUserIdData.equals( dbObj.courseDbUserIdData ) ) return false;
        if( !timeStampData.equals( dbObj.timeStampData ) ) return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Gets the raw data object for masterDbUserId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getMasterDbUserIdData() {
        return masterDbUserIdData;
    }

    /**
     * Gets the raw data object for courseDbUserId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getCourseDbUserIdData() {
        return courseDbUserIdData;
    }

    /**
     * Gets the raw data object for timeStamp attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTimeStampData() {
        return timeStampData;
    }

    /**
     * Inserts this object to the database table UserIdReplication.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     */
    public void insert(Connection con) throws SQLException, ObjectNotValidException {
        if( !masterDbUserIdData.isValid() ) throw new ObjectNotValidException("masterDbUserId");
        if( !courseDbUserIdData.isValid() ) throw new ObjectNotValidException("courseDbUserId");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "insert into UserIdReplication (masterDbUserId, courseDbUserId, timeStamp) values (?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterDbUserIdData.jdbcGetValue());
        ps.setObject(2, courseDbUserIdData.jdbcGetValue());
        ps.setObject(3, timeStampData.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows != 1 ) throw new SQLException("Insert did not return 1 row");
    }

    /**
     * Updates the row of this object in the database table UserIdReplication.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be updated does not exist or is not unique.
     */
    public void update(Connection con) throws SQLException, ObjectNotValidException, NoSuchItemException {
        if( !masterDbUserIdData.isValid() ) throw new ObjectNotValidException("masterDbUserId");
        if( !courseDbUserIdData.isValid() ) throw new ObjectNotValidException("courseDbUserId");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "update UserIdReplication set masterDbUserId = ?, courseDbUserId = ?, timeStamp = ? where masterDbUserId = ? AND courseDbUserId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterDbUserIdData.jdbcGetValue());
        ps.setObject(2, courseDbUserIdData.jdbcGetValue());
        ps.setObject(3, timeStampData.jdbcGetValue());
        ps.setObject(4, masterDbUserIdKC.jdbcGetValue());
        ps.setObject(5, courseDbUserIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        try {
            masterDbUserIdKC.jdbcSetValue(masterDbUserIdData.jdbcGetValue());
            courseDbUserIdKC.jdbcSetValue(courseDbUserIdData.jdbcGetValue());
        } catch( InvalidValueException e ) {
            // Ignored (isValid already called)
        }
        if( rows != 1 ) throw new NoSuchItemException();
    }

    /**
     * Deletes the row of this object from the database table UserIdReplication.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws TooManyItemsException if the row to be deleted is not unique.
     * @throws NoSuchItemException if the row to be deleted does not exist.
     */
    public void delete(Connection con) throws SQLException, TooManyItemsException, NoSuchItemException {
        String prepareString = "delete from UserIdReplication where masterDbUserId = ? AND courseDbUserId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterDbUserIdKC.jdbcGetValue());
        ps.setObject(2, courseDbUserIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows > 1 ) throw new TooManyItemsException();
        if( rows < 1 ) throw new NoSuchItemException();
    }

    /**
     * Selects the row of this object from the database table UserIdReplication and updates the attributes accordingly.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from UserIdReplication where masterDbUserId = ? AND courseDbUserId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterDbUserIdData.jdbcGetValue());
        ps.setObject(2, courseDbUserIdData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database table UserIdReplication.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type UserIdReplication. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from UserIdReplication";
        else
            prepareString = "select * from UserIdReplication where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, UserIdReplication.class );
    }
}

// End of file.
