package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for table TaskDocument.
 * 
 */
public class DbTaskDocument extends SqlAssignableObject implements Cloneable {
    private SqlInteger taskIdData;
    private SqlInteger taskIdKC;
    private SqlInteger documentIdData;
    private SqlInteger documentIdKC;
    private SqlInteger userIdData;
    private SqlInteger statusData;
    private SqlInteger timeStampData;

    /**
     * Default constructor.
     */
    public DbTaskDocument() {
        super();
        taskIdData = new SqlInteger();
        taskIdKC = new SqlInteger();
        documentIdData = new SqlInteger();
        documentIdKC = new SqlInteger();
        userIdData = new SqlInteger();
        statusData = new SqlInteger();
        timeStampData = new SqlInteger();
        taskIdData.setPrime(true);
        documentIdData.setPrime(true);
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
        documentIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        documentIdKC.jdbcSetValue(documentIdData.jdbcGetValue());
        userIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
        statusData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+4) );
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+5) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("TaskDocument\n");
        sb.append("taskId:" +  taskIdData.toString() + "\n");
        sb.append("documentId:" +  documentIdData.toString() + "\n");
        sb.append("userId:" +  userIdData.toString() + "\n");
        sb.append("status:" +  statusData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbTaskDocument) ) return false;
        DbTaskDocument dbObj = (DbTaskDocument)obj;
        if( !taskIdData.equals( dbObj.taskIdData ) ) return false;
        if( !documentIdData.equals( dbObj.documentIdData ) ) return false;
        if( !userIdData.equals( dbObj.userIdData ) ) return false;
        if( !statusData.equals( dbObj.statusData ) ) return false;
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
     * Gets the raw data object for documentId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getDocumentIdData() {
        return documentIdData;
    }

    /**
     * Gets the raw data object for userId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getUserIdData() {
        return userIdData;
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
     * Inserts this object to the database table TaskDocument.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     */
    public void insert(Connection con) throws SQLException, ObjectNotValidException {
        if( !taskIdData.isValid() ) throw new ObjectNotValidException("taskId");
        if( !documentIdData.isValid() ) throw new ObjectNotValidException("documentId");
        if( !userIdData.isValid() ) throw new ObjectNotValidException("userId");
        if( !statusData.isValid() ) throw new ObjectNotValidException("status");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "insert into TaskDocument (taskId, documentId, userId, status, timeStamp) values (?, ?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, taskIdData.jdbcGetValue());
        ps.setObject(2, documentIdData.jdbcGetValue());
        ps.setObject(3, userIdData.jdbcGetValue());
        ps.setObject(4, statusData.jdbcGetValue());
        ps.setObject(5, timeStampData.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows != 1 ) throw new SQLException("Insert did not return 1 row");
    }

    /**
     * Updates the row of this object in the database table TaskDocument.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be updated does not exist or is not unique.
     */
    public void update(Connection con) throws SQLException, ObjectNotValidException, NoSuchItemException {
        if( !taskIdData.isValid() ) throw new ObjectNotValidException("taskId");
        if( !documentIdData.isValid() ) throw new ObjectNotValidException("documentId");
        if( !userIdData.isValid() ) throw new ObjectNotValidException("userId");
        if( !statusData.isValid() ) throw new ObjectNotValidException("status");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "update TaskDocument set taskId = ?, documentId = ?, userId = ?, status = ?, timeStamp = ? where taskId = ? AND documentId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, taskIdData.jdbcGetValue());
        ps.setObject(2, documentIdData.jdbcGetValue());
        ps.setObject(3, userIdData.jdbcGetValue());
        ps.setObject(4, statusData.jdbcGetValue());
        ps.setObject(5, timeStampData.jdbcGetValue());
        ps.setObject(6, taskIdKC.jdbcGetValue());
        ps.setObject(7, documentIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        try {
            taskIdKC.jdbcSetValue(taskIdData.jdbcGetValue());
            documentIdKC.jdbcSetValue(documentIdData.jdbcGetValue());
        } catch( InvalidValueException e ) {
            // Ignored (isValid already called)
        }
        if( rows != 1 ) throw new NoSuchItemException();
    }

    /**
     * Deletes the row of this object from the database table TaskDocument.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws TooManyItemsException if the row to be deleted is not unique.
     * @throws NoSuchItemException if the row to be deleted does not exist.
     */
    public void delete(Connection con) throws SQLException, TooManyItemsException, NoSuchItemException {
        String prepareString = "delete from TaskDocument where taskId = ? AND documentId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, taskIdKC.jdbcGetValue());
        ps.setObject(2, documentIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows > 1 ) throw new TooManyItemsException();
        if( rows < 1 ) throw new NoSuchItemException();
    }

    /**
     * Selects the row of this object from the database table TaskDocument and updates the attributes accordingly.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from TaskDocument where taskId = ? AND documentId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, taskIdData.jdbcGetValue());
        ps.setObject(2, documentIdData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database table TaskDocument.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type TaskDocument. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from TaskDocument";
        else
            prepareString = "select * from TaskDocument where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, TaskDocument.class );
    }
}

// End of file.
