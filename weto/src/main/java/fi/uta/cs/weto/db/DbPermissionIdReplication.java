package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for table PermissionIdReplication.
 * 
 */
public class DbPermissionIdReplication extends SqlAssignableObject implements Cloneable {
    private SqlInteger masterDbPermissionIdData;
    private SqlInteger masterDbPermissionIdKC;
    private SqlInteger courseDbPermissionIdData;
    private SqlInteger courseDbPermissionIdKC;
    private SqlInteger timeStampData;

    /**
     * Default constructor.
     */
    public DbPermissionIdReplication() {
        super();
        masterDbPermissionIdData = new SqlInteger();
        masterDbPermissionIdKC = new SqlInteger();
        courseDbPermissionIdData = new SqlInteger();
        courseDbPermissionIdKC = new SqlInteger();
        timeStampData = new SqlInteger();
        masterDbPermissionIdData.setPrime(true);
        courseDbPermissionIdData.setPrime(true);
    }

    /**
     * Updates the data from the given ResultSet object.
     * @param resultSet ResultSet object containing the data.
     * @param baseIndex Base index of the columns in the ResultSet (exclusive).
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     */
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        masterDbPermissionIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+1) );
        masterDbPermissionIdKC.jdbcSetValue(masterDbPermissionIdData.jdbcGetValue());
        courseDbPermissionIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        courseDbPermissionIdKC.jdbcSetValue(courseDbPermissionIdData.jdbcGetValue());
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("PermissionIdReplication\n");
        sb.append("masterDbPermissionId:" +  masterDbPermissionIdData.toString() + "\n");
        sb.append("courseDbPermissionId:" +  courseDbPermissionIdData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbPermissionIdReplication) ) return false;
        DbPermissionIdReplication dbObj = (DbPermissionIdReplication)obj;
        if( !masterDbPermissionIdData.equals( dbObj.masterDbPermissionIdData ) ) return false;
        if( !courseDbPermissionIdData.equals( dbObj.courseDbPermissionIdData ) ) return false;
        if( !timeStampData.equals( dbObj.timeStampData ) ) return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Gets the raw data object for masterDbPermissionId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getMasterDbPermissionIdData() {
        return masterDbPermissionIdData;
    }

    /**
     * Gets the raw data object for courseDbPermissionId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getCourseDbPermissionIdData() {
        return courseDbPermissionIdData;
    }

    /**
     * Gets the raw data object for timeStamp attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTimeStampData() {
        return timeStampData;
    }

    /**
     * Inserts this object to the database table PermissionIdReplication.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     */
    public void insert(Connection con) throws SQLException, ObjectNotValidException {
        if( !masterDbPermissionIdData.isValid() ) throw new ObjectNotValidException("masterDbPermissionId");
        if( !courseDbPermissionIdData.isValid() ) throw new ObjectNotValidException("courseDbPermissionId");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "insert into PermissionIdReplication (masterDbPermissionId, courseDbPermissionId, timeStamp) values (?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterDbPermissionIdData.jdbcGetValue());
        ps.setObject(2, courseDbPermissionIdData.jdbcGetValue());
        ps.setObject(3, timeStampData.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows != 1 ) throw new SQLException("Insert did not return 1 row");
    }

    /**
     * Updates the row of this object in the database table PermissionIdReplication.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be updated does not exist or is not unique.
     */
    public void update(Connection con) throws SQLException, ObjectNotValidException, NoSuchItemException {
        if( !masterDbPermissionIdData.isValid() ) throw new ObjectNotValidException("masterDbPermissionId");
        if( !courseDbPermissionIdData.isValid() ) throw new ObjectNotValidException("courseDbPermissionId");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "update PermissionIdReplication set masterDbPermissionId = ?, courseDbPermissionId = ?, timeStamp = ? where masterDbPermissionId = ? AND courseDbPermissionId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterDbPermissionIdData.jdbcGetValue());
        ps.setObject(2, courseDbPermissionIdData.jdbcGetValue());
        ps.setObject(3, timeStampData.jdbcGetValue());
        ps.setObject(4, masterDbPermissionIdKC.jdbcGetValue());
        ps.setObject(5, courseDbPermissionIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        try {
            masterDbPermissionIdKC.jdbcSetValue(masterDbPermissionIdData.jdbcGetValue());
            courseDbPermissionIdKC.jdbcSetValue(courseDbPermissionIdData.jdbcGetValue());
        } catch( InvalidValueException e ) {
            // Ignored (isValid already called)
        }
        if( rows != 1 ) throw new NoSuchItemException();
    }

    /**
     * Deletes the row of this object from the database table PermissionIdReplication.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws TooManyItemsException if the row to be deleted is not unique.
     * @throws NoSuchItemException if the row to be deleted does not exist.
     */
    public void delete(Connection con) throws SQLException, TooManyItemsException, NoSuchItemException {
        String prepareString = "delete from PermissionIdReplication where masterDbPermissionId = ? AND courseDbPermissionId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterDbPermissionIdKC.jdbcGetValue());
        ps.setObject(2, courseDbPermissionIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows > 1 ) throw new TooManyItemsException();
        if( rows < 1 ) throw new NoSuchItemException();
    }

    /**
     * Selects the row of this object from the database table PermissionIdReplication and updates the attributes accordingly.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from PermissionIdReplication where masterDbPermissionId = ? AND courseDbPermissionId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterDbPermissionIdData.jdbcGetValue());
        ps.setObject(2, courseDbPermissionIdData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database table PermissionIdReplication.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type PermissionIdReplication. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from PermissionIdReplication";
        else
            prepareString = "select * from PermissionIdReplication where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, PermissionIdReplication.class );
    }
}

// End of file.
