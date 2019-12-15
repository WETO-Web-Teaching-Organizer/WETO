package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for view GradeView.
 * 
 */
public class DbGradeView extends SqlAssignableObject implements Cloneable {
    private SqlInteger taskIdData;
    private SqlInteger idData;
    private SqlInteger reviewerIdData;
    private SqlInteger receiverIdData;
    private SqlReal markData;
    private SqlInteger timeStampData;
    private SqlLongvarchar reviewerFirstNameData;
    private SqlLongvarchar reviewerLastNameData;
    private SqlLongvarchar receiverFirstNameData;
    private SqlLongvarchar receiverLastNameData;
    private SqlLongvarchar taskNameData;

    /**
     * Default constructor.
     */
    public DbGradeView() {
        super();
        taskIdData = new SqlInteger();
        idData = new SqlInteger();
        reviewerIdData = new SqlInteger();
        receiverIdData = new SqlInteger();
        markData = new SqlReal();
        timeStampData = new SqlInteger();
        reviewerFirstNameData = new SqlLongvarchar();
        reviewerLastNameData = new SqlLongvarchar();
        receiverFirstNameData = new SqlLongvarchar();
        receiverLastNameData = new SqlLongvarchar();
        taskNameData = new SqlLongvarchar();
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
        idData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        reviewerIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
        receiverIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+4) );
        markData.jdbcSetValue( (java.lang.Float) resultSet.getObject(baseIndex+5) );
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+6) );
        reviewerFirstNameData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+7) );
        reviewerLastNameData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+8) );
        receiverFirstNameData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+9) );
        receiverLastNameData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+10) );
        taskNameData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+11) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("GradeView\n");
        sb.append("taskId:" +  taskIdData.toString() + "\n");
        sb.append("id:" +  idData.toString() + "\n");
        sb.append("reviewerId:" +  reviewerIdData.toString() + "\n");
        sb.append("receiverId:" +  receiverIdData.toString() + "\n");
        sb.append("mark:" +  markData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("reviewerFirstName:" +  reviewerFirstNameData.toString() + "\n");
        sb.append("reviewerLastName:" +  reviewerLastNameData.toString() + "\n");
        sb.append("receiverFirstName:" +  receiverFirstNameData.toString() + "\n");
        sb.append("receiverLastName:" +  receiverLastNameData.toString() + "\n");
        sb.append("taskName:" +  taskNameData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbGradeView) ) return false;
        DbGradeView dbObj = (DbGradeView)obj;
        if( !taskIdData.equals( dbObj.taskIdData ) ) return false;
        if( !idData.equals( dbObj.idData ) ) return false;
        if( !reviewerIdData.equals( dbObj.reviewerIdData ) ) return false;
        if( !receiverIdData.equals( dbObj.receiverIdData ) ) return false;
        if( !markData.equals( dbObj.markData ) ) return false;
        if( !timeStampData.equals( dbObj.timeStampData ) ) return false;
        if( !reviewerFirstNameData.equals( dbObj.reviewerFirstNameData ) ) return false;
        if( !reviewerLastNameData.equals( dbObj.reviewerLastNameData ) ) return false;
        if( !receiverFirstNameData.equals( dbObj.receiverFirstNameData ) ) return false;
        if( !receiverLastNameData.equals( dbObj.receiverLastNameData ) ) return false;
        if( !taskNameData.equals( dbObj.taskNameData ) ) return false;
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
     * Gets the raw data object for id attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getIdData() {
        return idData;
    }

    /**
     * Gets the raw data object for reviewerId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getReviewerIdData() {
        return reviewerIdData;
    }

    /**
     * Gets the raw data object for receiverId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getReceiverIdData() {
        return receiverIdData;
    }

    /**
     * Gets the raw data object for mark attribute.
     * @return Data object as SqlReal.
     */
    public SqlReal getMarkData() {
        return markData;
    }

    /**
     * Gets the raw data object for timeStamp attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTimeStampData() {
        return timeStampData;
    }

    /**
     * Gets the raw data object for reviewerFirstName attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getReviewerFirstNameData() {
        return reviewerFirstNameData;
    }

    /**
     * Gets the raw data object for reviewerLastName attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getReviewerLastNameData() {
        return reviewerLastNameData;
    }

    /**
     * Gets the raw data object for receiverFirstName attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getReceiverFirstNameData() {
        return receiverFirstNameData;
    }

    /**
     * Gets the raw data object for receiverLastName attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getReceiverLastNameData() {
        return receiverLastNameData;
    }

    /**
     * Gets the raw data object for taskName attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getTaskNameData() {
        return taskNameData;
    }

    /**
     * Selects the row of this object from the database view GradeView and updates the attributes accordingly.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from GradeView where taskId = ? AND id = ? AND reviewerId = ? AND receiverId = ? AND mark = ? AND timeStamp = ? AND reviewerFirstName = ? AND reviewerLastName = ? AND receiverFirstName = ? AND receiverLastName = ? AND taskName = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, taskIdData.jdbcGetValue());
        ps.setObject(2, idData.jdbcGetValue());
        ps.setObject(3, reviewerIdData.jdbcGetValue());
        ps.setObject(4, receiverIdData.jdbcGetValue());
        ps.setObject(5, markData.jdbcGetValue());
        ps.setObject(6, timeStampData.jdbcGetValue());
        ps.setObject(7, reviewerFirstNameData.jdbcGetValue());
        ps.setObject(8, reviewerLastNameData.jdbcGetValue());
        ps.setObject(9, receiverFirstNameData.jdbcGetValue());
        ps.setObject(10, receiverLastNameData.jdbcGetValue());
        ps.setObject(11, taskNameData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database view GradeView.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type GradeView. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from GradeView";
        else
            prepareString = "select * from GradeView where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, GradeView.class );
    }
}

// End of file.
