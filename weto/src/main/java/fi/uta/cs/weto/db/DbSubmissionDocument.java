package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for table SubmissionDocument.
 * 
 */
public class DbSubmissionDocument extends SqlAssignableObject implements Cloneable {
    private SqlInteger submissionIdData;
    private SqlInteger submissionIdKC;
    private SqlInteger documentIdData;
    private SqlInteger documentIdKC;
    private SqlInteger timeStampData;

    /**
     * Default constructor.
     */
    public DbSubmissionDocument() {
        super();
        submissionIdData = new SqlInteger();
        submissionIdKC = new SqlInteger();
        documentIdData = new SqlInteger();
        documentIdKC = new SqlInteger();
        timeStampData = new SqlInteger();
        submissionIdData.setPrime(true);
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
        submissionIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+1) );
        submissionIdKC.jdbcSetValue(submissionIdData.jdbcGetValue());
        documentIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        documentIdKC.jdbcSetValue(documentIdData.jdbcGetValue());
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SubmissionDocument\n");
        sb.append("submissionId:" +  submissionIdData.toString() + "\n");
        sb.append("documentId:" +  documentIdData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbSubmissionDocument) ) return false;
        DbSubmissionDocument dbObj = (DbSubmissionDocument)obj;
        if( !submissionIdData.equals( dbObj.submissionIdData ) ) return false;
        if( !documentIdData.equals( dbObj.documentIdData ) ) return false;
        if( !timeStampData.equals( dbObj.timeStampData ) ) return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Gets the raw data object for submissionId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getSubmissionIdData() {
        return submissionIdData;
    }

    /**
     * Gets the raw data object for documentId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getDocumentIdData() {
        return documentIdData;
    }

    /**
     * Gets the raw data object for timeStamp attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTimeStampData() {
        return timeStampData;
    }

    /**
     * Inserts this object to the database table SubmissionDocument.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     */
    public void insert(Connection con) throws SQLException, ObjectNotValidException {
        if( !submissionIdData.isValid() ) throw new ObjectNotValidException("submissionId");
        if( !documentIdData.isValid() ) throw new ObjectNotValidException("documentId");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "insert into SubmissionDocument (submissionId, documentId, timeStamp) values (?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, submissionIdData.jdbcGetValue());
        ps.setObject(2, documentIdData.jdbcGetValue());
        ps.setObject(3, timeStampData.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows != 1 ) throw new SQLException("Insert did not return 1 row");
    }

    /**
     * Updates the row of this object in the database table SubmissionDocument.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be updated does not exist or is not unique.
     */
    public void update(Connection con) throws SQLException, ObjectNotValidException, NoSuchItemException {
        if( !submissionIdData.isValid() ) throw new ObjectNotValidException("submissionId");
        if( !documentIdData.isValid() ) throw new ObjectNotValidException("documentId");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "update SubmissionDocument set submissionId = ?, documentId = ?, timeStamp = ? where submissionId = ? AND documentId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, submissionIdData.jdbcGetValue());
        ps.setObject(2, documentIdData.jdbcGetValue());
        ps.setObject(3, timeStampData.jdbcGetValue());
        ps.setObject(4, submissionIdKC.jdbcGetValue());
        ps.setObject(5, documentIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        try {
            submissionIdKC.jdbcSetValue(submissionIdData.jdbcGetValue());
            documentIdKC.jdbcSetValue(documentIdData.jdbcGetValue());
        } catch( InvalidValueException e ) {
            // Ignored (isValid already called)
        }
        if( rows != 1 ) throw new NoSuchItemException();
    }

    /**
     * Deletes the row of this object from the database table SubmissionDocument.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws TooManyItemsException if the row to be deleted is not unique.
     * @throws NoSuchItemException if the row to be deleted does not exist.
     */
    public void delete(Connection con) throws SQLException, TooManyItemsException, NoSuchItemException {
        String prepareString = "delete from SubmissionDocument where submissionId = ? AND documentId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, submissionIdKC.jdbcGetValue());
        ps.setObject(2, documentIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows > 1 ) throw new TooManyItemsException();
        if( rows < 1 ) throw new NoSuchItemException();
    }

    /**
     * Selects the row of this object from the database table SubmissionDocument and updates the attributes accordingly.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from SubmissionDocument where submissionId = ? AND documentId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, submissionIdData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database table SubmissionDocument.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type SubmissionDocument. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from SubmissionDocument";
        else
            prepareString = "select * from SubmissionDocument where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, SubmissionDocument.class );
    }
}

// End of file.
