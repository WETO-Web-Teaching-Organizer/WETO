package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for table SubtaskLink.
 * Links parent task and subtask.
 */
public class DbSubtaskLink extends SqlAssignableObject implements Cloneable {
    private SqlInteger containerIdData;
    private SqlInteger containerIdKC;
    private SqlInteger subtaskIdData;
    private SqlInteger subtaskIdKC;
    private SqlInteger rankData;
    private SqlInteger timeStampData;

    /**
     * Default constructor.
     */
    public DbSubtaskLink() {
        super();
        containerIdData = new SqlInteger();
        containerIdKC = new SqlInteger();
        subtaskIdData = new SqlInteger();
        subtaskIdKC = new SqlInteger();
        rankData = new SqlInteger();
        timeStampData = new SqlInteger();
        containerIdData.setPrime(true);
        subtaskIdData.setPrime(true);
    }

    /**
     * Updates the data from the given ResultSet object.
     * @param resultSet ResultSet object containing the data.
     * @param baseIndex Base index of the columns in the ResultSet (exclusive).
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     */
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        containerIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+1) );
        containerIdKC.jdbcSetValue(containerIdData.jdbcGetValue());
        subtaskIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        subtaskIdKC.jdbcSetValue(subtaskIdData.jdbcGetValue());
        rankData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+4) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SubtaskLink\n");
        sb.append("containerId:" +  containerIdData.toString() + "\n");
        sb.append("subtaskId:" +  subtaskIdData.toString() + "\n");
        sb.append("rank:" +  rankData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbSubtaskLink) ) return false;
        DbSubtaskLink dbObj = (DbSubtaskLink)obj;
        if( !containerIdData.equals( dbObj.containerIdData ) ) return false;
        if( !subtaskIdData.equals( dbObj.subtaskIdData ) ) return false;
        if( !rankData.equals( dbObj.rankData ) ) return false;
        if( !timeStampData.equals( dbObj.timeStampData ) ) return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Gets the raw data object for containerId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getContainerIdData() {
        return containerIdData;
    }

    /**
     * Gets the raw data object for subtaskId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getSubtaskIdData() {
        return subtaskIdData;
    }

    /**
     * Gets the raw data object for rank attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getRankData() {
        return rankData;
    }

    /**
     * Gets the raw data object for timeStamp attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTimeStampData() {
        return timeStampData;
    }

    /**
     * Inserts this object to the database table SubtaskLink.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     */
    public void insert(Connection con) throws SQLException, ObjectNotValidException {
        if( !containerIdData.isValid() ) throw new ObjectNotValidException("containerId");
        if( !subtaskIdData.isValid() ) throw new ObjectNotValidException("subtaskId");
        if( !rankData.isValid() ) throw new ObjectNotValidException("rank");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "insert into SubtaskLink (containerId, subtaskId, rank, timeStamp) values (?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, containerIdData.jdbcGetValue());
        ps.setObject(2, subtaskIdData.jdbcGetValue());
        ps.setObject(3, rankData.jdbcGetValue());
        ps.setObject(4, timeStampData.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows != 1 ) throw new SQLException("Insert did not return 1 row");
    }

    /**
     * Updates the row of this object in the database table SubtaskLink.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be updated does not exist or is not unique.
     */
    public void update(Connection con) throws SQLException, ObjectNotValidException, NoSuchItemException {
        if( !containerIdData.isValid() ) throw new ObjectNotValidException("containerId");
        if( !subtaskIdData.isValid() ) throw new ObjectNotValidException("subtaskId");
        if( !rankData.isValid() ) throw new ObjectNotValidException("rank");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "update SubtaskLink set containerId = ?, subtaskId = ?, rank = ?, timeStamp = ? where containerId = ? AND subtaskId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, containerIdData.jdbcGetValue());
        ps.setObject(2, subtaskIdData.jdbcGetValue());
        ps.setObject(3, rankData.jdbcGetValue());
        ps.setObject(4, timeStampData.jdbcGetValue());
        ps.setObject(5, containerIdKC.jdbcGetValue());
        ps.setObject(6, subtaskIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        try {
            containerIdKC.jdbcSetValue(containerIdData.jdbcGetValue());
            subtaskIdKC.jdbcSetValue(subtaskIdData.jdbcGetValue());
        } catch( InvalidValueException e ) {
            // Ignored (isValid already called)
        }
        if( rows != 1 ) throw new NoSuchItemException();
    }

    /**
     * Deletes the row of this object from the database table SubtaskLink.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws TooManyItemsException if the row to be deleted is not unique.
     * @throws NoSuchItemException if the row to be deleted does not exist.
     */
    public void delete(Connection con) throws SQLException, TooManyItemsException, NoSuchItemException {
        String prepareString = "delete from SubtaskLink where containerId = ? AND subtaskId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, containerIdKC.jdbcGetValue());
        ps.setObject(2, subtaskIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows > 1 ) throw new TooManyItemsException();
        if( rows < 1 ) throw new NoSuchItemException();
    }

    /**
     * Selects the row of this object from the database table SubtaskLink and updates the attributes accordingly.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from SubtaskLink where containerId = ? AND subtaskId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, containerIdData.jdbcGetValue());
        ps.setObject(2, subtaskIdData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database table SubtaskLink.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type SubtaskLink. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from SubtaskLink";
        else
            prepareString = "select * from SubtaskLink where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, SubtaskLink.class );
    }
}

// End of file.
