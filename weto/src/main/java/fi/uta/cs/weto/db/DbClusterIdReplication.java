package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for table ClusterIdReplication.
 * 
 */
public class DbClusterIdReplication extends SqlAssignableObject implements Cloneable {
    private SqlInteger masterDbClusterIdData;
    private SqlInteger masterDbClusterIdKC;
    private SqlInteger courseDbClusterIdData;
    private SqlInteger courseDbClusterIdKC;
    private SqlInteger timeStampData;

    /**
     * Default constructor.
     */
    public DbClusterIdReplication() {
        super();
        masterDbClusterIdData = new SqlInteger();
        masterDbClusterIdKC = new SqlInteger();
        courseDbClusterIdData = new SqlInteger();
        courseDbClusterIdKC = new SqlInteger();
        timeStampData = new SqlInteger();
        masterDbClusterIdData.setPrime(true);
        courseDbClusterIdData.setPrime(true);
    }

    /**
     * Updates the data from the given ResultSet object.
     * @param resultSet ResultSet object containing the data.
     * @param baseIndex Base index of the columns in the ResultSet (exclusive).
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     */
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        masterDbClusterIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+1) );
        masterDbClusterIdKC.jdbcSetValue(masterDbClusterIdData.jdbcGetValue());
        courseDbClusterIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        courseDbClusterIdKC.jdbcSetValue(courseDbClusterIdData.jdbcGetValue());
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ClusterIdReplication\n");
        sb.append("masterDbClusterId:" +  masterDbClusterIdData.toString() + "\n");
        sb.append("courseDbClusterId:" +  courseDbClusterIdData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbClusterIdReplication) ) return false;
        DbClusterIdReplication dbObj = (DbClusterIdReplication)obj;
        if( !masterDbClusterIdData.equals( dbObj.masterDbClusterIdData ) ) return false;
        if( !courseDbClusterIdData.equals( dbObj.courseDbClusterIdData ) ) return false;
        if( !timeStampData.equals( dbObj.timeStampData ) ) return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Gets the raw data object for masterDbClusterId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getMasterDbClusterIdData() {
        return masterDbClusterIdData;
    }

    /**
     * Gets the raw data object for courseDbClusterId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getCourseDbClusterIdData() {
        return courseDbClusterIdData;
    }

    /**
     * Gets the raw data object for timeStamp attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTimeStampData() {
        return timeStampData;
    }

    /**
     * Inserts this object to the database table ClusterIdReplication.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     */
    public void insert(Connection con) throws SQLException, ObjectNotValidException {
        if( !masterDbClusterIdData.isValid() ) throw new ObjectNotValidException("masterDbClusterId");
        if( !courseDbClusterIdData.isValid() ) throw new ObjectNotValidException("courseDbClusterId");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "insert into ClusterIdReplication (masterDbClusterId, courseDbClusterId, timeStamp) values (?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterDbClusterIdData.jdbcGetValue());
        ps.setObject(2, courseDbClusterIdData.jdbcGetValue());
        ps.setObject(3, timeStampData.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows != 1 ) throw new SQLException("Insert did not return 1 row");
    }

    /**
     * Updates the row of this object in the database table ClusterIdReplication.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be updated does not exist or is not unique.
     */
    public void update(Connection con) throws SQLException, ObjectNotValidException, NoSuchItemException {
        if( !masterDbClusterIdData.isValid() ) throw new ObjectNotValidException("masterDbClusterId");
        if( !courseDbClusterIdData.isValid() ) throw new ObjectNotValidException("courseDbClusterId");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "update ClusterIdReplication set masterDbClusterId = ?, courseDbClusterId = ?, timeStamp = ? where masterDbClusterId = ? AND courseDbClusterId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterDbClusterIdData.jdbcGetValue());
        ps.setObject(2, courseDbClusterIdData.jdbcGetValue());
        ps.setObject(3, timeStampData.jdbcGetValue());
        ps.setObject(4, masterDbClusterIdKC.jdbcGetValue());
        ps.setObject(5, courseDbClusterIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        try {
            masterDbClusterIdKC.jdbcSetValue(masterDbClusterIdData.jdbcGetValue());
            courseDbClusterIdKC.jdbcSetValue(courseDbClusterIdData.jdbcGetValue());
        } catch( InvalidValueException e ) {
            // Ignored (isValid already called)
        }
        if( rows != 1 ) throw new NoSuchItemException();
    }

    /**
     * Deletes the row of this object from the database table ClusterIdReplication.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws TooManyItemsException if the row to be deleted is not unique.
     * @throws NoSuchItemException if the row to be deleted does not exist.
     */
    public void delete(Connection con) throws SQLException, TooManyItemsException, NoSuchItemException {
        String prepareString = "delete from ClusterIdReplication where masterDbClusterId = ? AND courseDbClusterId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterDbClusterIdKC.jdbcGetValue());
        ps.setObject(2, courseDbClusterIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows > 1 ) throw new TooManyItemsException();
        if( rows < 1 ) throw new NoSuchItemException();
    }

    /**
     * Selects the row of this object from the database table ClusterIdReplication and updates the attributes accordingly.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from ClusterIdReplication where masterDbClusterId = ? AND courseDbClusterId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterDbClusterIdData.jdbcGetValue());
        ps.setObject(2, courseDbClusterIdData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database table ClusterIdReplication.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type ClusterIdReplication. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from ClusterIdReplication";
        else
            prepareString = "select * from ClusterIdReplication where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, ClusterIdReplication.class );
    }
}

// End of file.
