package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for table AutoGrading.
 * 
 */
public class DbAutoGrading extends SqlAssignableObject implements Cloneable {
    private SqlInteger taskIdData;
    private SqlInteger taskIdKC;
    private SqlInteger testDocIdData;
    private SqlLongvarchar propertiesData;
    private SqlInteger timeStampData;

    /**
     * Default constructor.
     */
    public DbAutoGrading() {
        super();
        taskIdData = new SqlInteger();
        taskIdKC = new SqlInteger();
        testDocIdData = new SqlInteger();
        propertiesData = new SqlLongvarchar();
        timeStampData = new SqlInteger();
        taskIdData.setPrime(true);
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
        taskIdKC.jdbcSetValue(taskIdData.jdbcGetValue());
        testDocIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        propertiesData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+3) );
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+4) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AutoGrading\n");
        sb.append("taskId:" +  taskIdData.toString() + "\n");
        sb.append("testDocId:" +  testDocIdData.toString() + "\n");
        sb.append("properties:" +  propertiesData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbAutoGrading) ) return false;
        DbAutoGrading dbObj = (DbAutoGrading)obj;
        if( !taskIdData.equals( dbObj.taskIdData ) ) return false;
        if( !testDocIdData.equals( dbObj.testDocIdData ) ) return false;
        if( !propertiesData.equals( dbObj.propertiesData ) ) return false;
        if( !timeStampData.equals( dbObj.timeStampData ) ) return false;
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
     * Gets the raw data object for testDocId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTestDocIdData() {
        return testDocIdData;
    }

    /**
     * Gets the raw data object for properties attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getPropertiesData() {
        return propertiesData;
    }

    /**
     * Gets the raw data object for timeStamp attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTimeStampData() {
        return timeStampData;
    }

    /**
     * Inserts this object to the database table AutoGrading.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     */
    public void insert(Connection con) throws SQLException, ObjectNotValidException {
        if( !taskIdData.isValid() ) throw new ObjectNotValidException("taskId");
        if( !testDocIdData.isValid() ) throw new ObjectNotValidException("testDocId");
        if( !propertiesData.isValid() ) throw new ObjectNotValidException("properties");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "insert into AutoGrading (taskId, testDocId, properties, timeStamp) values (?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, taskIdData.jdbcGetValue());
        ps.setObject(2, testDocIdData.jdbcGetValue());
        ps.setObject(3, propertiesData.jdbcGetValue());
        ps.setObject(4, timeStampData.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows != 1 ) throw new SQLException("Insert did not return 1 row");
    }

    /**
     * Updates the row of this object in the database table AutoGrading.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be updated does not exist or is not unique.
     */
    public void update(Connection con) throws SQLException, ObjectNotValidException, NoSuchItemException {
        if( !taskIdData.isValid() ) throw new ObjectNotValidException("taskId");
        if( !testDocIdData.isValid() ) throw new ObjectNotValidException("testDocId");
        if( !propertiesData.isValid() ) throw new ObjectNotValidException("properties");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "update AutoGrading set taskId = ?, testDocId = ?, properties = ?, timeStamp = ? where taskId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, taskIdData.jdbcGetValue());
        ps.setObject(2, testDocIdData.jdbcGetValue());
        ps.setObject(3, propertiesData.jdbcGetValue());
        ps.setObject(4, timeStampData.jdbcGetValue());
        ps.setObject(5, taskIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        try {
            taskIdKC.jdbcSetValue(taskIdData.jdbcGetValue());
        } catch( InvalidValueException e ) {
            // Ignored (isValid already called)
        }
        if( rows != 1 ) throw new NoSuchItemException();
    }

    /**
     * Deletes the row of this object from the database table AutoGrading.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws TooManyItemsException if the row to be deleted is not unique.
     * @throws NoSuchItemException if the row to be deleted does not exist.
     */
    public void delete(Connection con) throws SQLException, TooManyItemsException, NoSuchItemException {
        String prepareString = "delete from AutoGrading where taskId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, taskIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows > 1 ) throw new TooManyItemsException();
        if( rows < 1 ) throw new NoSuchItemException();
    }

    /**
     * Selects the row of this object from the database table AutoGrading and updates the attributes accordingly.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from AutoGrading where taskId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, taskIdData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database table AutoGrading.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type AutoGrading. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from AutoGrading";
        else
            prepareString = "select * from AutoGrading where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, AutoGrading.class );
    }
}

// End of file.
