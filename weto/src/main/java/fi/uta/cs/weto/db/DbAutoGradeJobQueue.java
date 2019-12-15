package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for table AutoGradeJobQueue.
 * Jobs to be executed
 */
public class DbAutoGradeJobQueue extends SqlAssignableObject implements Cloneable {
    private SqlInteger idData;
    private SqlInteger idKC;
    private SqlInteger taskIdData;
    private SqlInteger dbIdData;
    private SqlInteger jobTypeData;
    private SqlInteger refIdData;
    private SqlInteger queuePhaseData;
    private SqlBoolean testRunningData;
    private SqlLongvarchar jobCommentData;
    private SqlInteger timeStampData;

    /**
     * Default constructor.
     */
    public DbAutoGradeJobQueue() {
        super();
        idData = new SqlInteger();
        idKC = new SqlInteger();
        taskIdData = new SqlInteger();
        dbIdData = new SqlInteger();
        jobTypeData = new SqlInteger();
        refIdData = new SqlInteger();
        queuePhaseData = new SqlInteger();
        testRunningData = new SqlBoolean();
        jobCommentData = new SqlLongvarchar();
        timeStampData = new SqlInteger();
        idData.setPrime(true);
    }

    /**
     * Updates the data from the given ResultSet object.
     * @param resultSet ResultSet object containing the data.
     * @param baseIndex Base index of the columns in the ResultSet (exclusive).
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     */
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        idData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+1) );
        idKC.jdbcSetValue(idData.jdbcGetValue());
        taskIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        dbIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
        jobTypeData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+4) );
        refIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+5) );
        queuePhaseData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+6) );
        testRunningData.jdbcSetValue( (java.lang.Boolean) resultSet.getObject(baseIndex+7) );
        jobCommentData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+8) );
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+9) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AutoGradeJobQueue\n");
        sb.append("id:" +  idData.toString() + "\n");
        sb.append("taskId:" +  taskIdData.toString() + "\n");
        sb.append("dbId:" +  dbIdData.toString() + "\n");
        sb.append("jobType:" +  jobTypeData.toString() + "\n");
        sb.append("refId:" +  refIdData.toString() + "\n");
        sb.append("queuePhase:" +  queuePhaseData.toString() + "\n");
        sb.append("testRunning:" +  testRunningData.toString() + "\n");
        sb.append("jobComment:" +  jobCommentData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbAutoGradeJobQueue) ) return false;
        DbAutoGradeJobQueue dbObj = (DbAutoGradeJobQueue)obj;
        if( !idData.equals( dbObj.idData ) ) return false;
        if( !taskIdData.equals( dbObj.taskIdData ) ) return false;
        if( !dbIdData.equals( dbObj.dbIdData ) ) return false;
        if( !jobTypeData.equals( dbObj.jobTypeData ) ) return false;
        if( !refIdData.equals( dbObj.refIdData ) ) return false;
        if( !queuePhaseData.equals( dbObj.queuePhaseData ) ) return false;
        if( !testRunningData.equals( dbObj.testRunningData ) ) return false;
        if( !jobCommentData.equals( dbObj.jobCommentData ) ) return false;
        if( !timeStampData.equals( dbObj.timeStampData ) ) return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Gets the raw data object for id attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getIdData() {
        return idData;
    }

    /**
     * Gets the raw data object for taskId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTaskIdData() {
        return taskIdData;
    }

    /**
     * Gets the raw data object for dbId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getDbIdData() {
        return dbIdData;
    }

    /**
     * Gets the raw data object for jobType attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getJobTypeData() {
        return jobTypeData;
    }

    /**
     * Gets the raw data object for refId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getRefIdData() {
        return refIdData;
    }

    /**
     * Gets the raw data object for queuePhase attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getQueuePhaseData() {
        return queuePhaseData;
    }

    /**
     * Gets the raw data object for testRunning attribute.
     * @return Data object as SqlBoolean.
     */
    public SqlBoolean getTestRunningData() {
        return testRunningData;
    }

    /**
     * Gets the raw data object for jobComment attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getJobCommentData() {
        return jobCommentData;
    }

    /**
     * Gets the raw data object for timeStamp attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTimeStampData() {
        return timeStampData;
    }

    /**
     * Inserts this object to the database table AutoGradeJobQueue.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     */
    public void insert(Connection con) throws SQLException, ObjectNotValidException {
        if( !taskIdData.isValid() ) throw new ObjectNotValidException("taskId");
        if( !dbIdData.isValid() ) throw new ObjectNotValidException("dbId");
        if( !jobTypeData.isValid() ) throw new ObjectNotValidException("jobType");
        if( !refIdData.isValid() ) throw new ObjectNotValidException("refId");
        if( !queuePhaseData.isValid() ) throw new ObjectNotValidException("queuePhase");
        if( !testRunningData.isValid() ) throw new ObjectNotValidException("testRunning");
        if( !jobCommentData.isValid() ) throw new ObjectNotValidException("jobComment");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "insert into AutoGradeJobQueue (taskId, dbId, jobType, refId, queuePhase, testRunning, jobComment, timeStamp) values (?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, taskIdData.jdbcGetValue());
        ps.setObject(2, dbIdData.jdbcGetValue());
        ps.setObject(3, jobTypeData.jdbcGetValue());
        ps.setObject(4, refIdData.jdbcGetValue());
        ps.setObject(5, queuePhaseData.jdbcGetValue());
        ps.setObject(6, testRunningData.jdbcGetValue());
        ps.setObject(7, jobCommentData.jdbcGetValue());
        ps.setObject(8, timeStampData.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows != 1 ) throw new SQLException("Insert did not return 1 row");
    }

    /**
     * Updates the row of this object in the database table AutoGradeJobQueue.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be updated does not exist or is not unique.
     */
    public void update(Connection con) throws SQLException, ObjectNotValidException, NoSuchItemException {
        if( !idData.isValid() ) throw new ObjectNotValidException("id");
        if( !taskIdData.isValid() ) throw new ObjectNotValidException("taskId");
        if( !dbIdData.isValid() ) throw new ObjectNotValidException("dbId");
        if( !jobTypeData.isValid() ) throw new ObjectNotValidException("jobType");
        if( !refIdData.isValid() ) throw new ObjectNotValidException("refId");
        if( !queuePhaseData.isValid() ) throw new ObjectNotValidException("queuePhase");
        if( !testRunningData.isValid() ) throw new ObjectNotValidException("testRunning");
        if( !jobCommentData.isValid() ) throw new ObjectNotValidException("jobComment");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "update AutoGradeJobQueue set id = ?, taskId = ?, dbId = ?, jobType = ?, refId = ?, queuePhase = ?, testRunning = ?, jobComment = ?, timeStamp = ? where id = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, idData.jdbcGetValue());
        ps.setObject(2, taskIdData.jdbcGetValue());
        ps.setObject(3, dbIdData.jdbcGetValue());
        ps.setObject(4, jobTypeData.jdbcGetValue());
        ps.setObject(5, refIdData.jdbcGetValue());
        ps.setObject(6, queuePhaseData.jdbcGetValue());
        ps.setObject(7, testRunningData.jdbcGetValue());
        ps.setObject(8, jobCommentData.jdbcGetValue());
        ps.setObject(9, timeStampData.jdbcGetValue());
        ps.setObject(10, idKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        try {
            idKC.jdbcSetValue(idData.jdbcGetValue());
        } catch( InvalidValueException e ) {
            // Ignored (isValid already called)
        }
        if( rows != 1 ) throw new NoSuchItemException();
    }

    /**
     * Deletes the row of this object from the database table AutoGradeJobQueue.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws TooManyItemsException if the row to be deleted is not unique.
     * @throws NoSuchItemException if the row to be deleted does not exist.
     */
    public void delete(Connection con) throws SQLException, TooManyItemsException, NoSuchItemException {
        String prepareString = "delete from AutoGradeJobQueue where id = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, idKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows > 1 ) throw new TooManyItemsException();
        if( rows < 1 ) throw new NoSuchItemException();
    }

    /**
     * Selects the row of this object from the database table AutoGradeJobQueue and updates the attributes accordingly.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException, ObjectNotValidException {
        String prepareString = "select * from AutoGradeJobQueue where id = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, idData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database table AutoGradeJobQueue.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type AutoGradeJobQueue. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from AutoGradeJobQueue";
        else
            prepareString = "select * from AutoGradeJobQueue where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, AutoGradeJobQueue.class );
    }
}

// End of file.
