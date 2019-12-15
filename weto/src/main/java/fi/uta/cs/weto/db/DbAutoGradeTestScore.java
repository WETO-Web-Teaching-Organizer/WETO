package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for table AutoGradeTestScore.
 * 
 */
public class DbAutoGradeTestScore extends SqlAssignableObject implements Cloneable {
    private SqlInteger submissionIdData;
    private SqlInteger submissionIdKC;
    private SqlInteger testNoData;
    private SqlInteger testNoKC;
    private SqlInteger testScoreData;
    private SqlInteger phaseData;
    private SqlInteger phaseKC;
    private SqlInteger processingTimeData;
    private SqlLongvarchar feedbackData;
    private SqlInteger timeStampData;

    /**
     * Default constructor.
     */
    public DbAutoGradeTestScore() {
        super();
        submissionIdData = new SqlInteger();
        submissionIdKC = new SqlInteger();
        testNoData = new SqlInteger();
        testNoKC = new SqlInteger();
        testScoreData = new SqlInteger();
        phaseData = new SqlInteger();
        phaseKC = new SqlInteger();
        processingTimeData = new SqlInteger();
        feedbackData = new SqlLongvarchar();
        timeStampData = new SqlInteger();
        submissionIdData.setPrime(true);
        testNoData.setPrime(true);
        phaseData.setPrime(true);
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
        testNoData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        testNoKC.jdbcSetValue(testNoData.jdbcGetValue());
        testScoreData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
        phaseData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+4) );
        phaseKC.jdbcSetValue(phaseData.jdbcGetValue());
        processingTimeData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+5) );
        feedbackData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+6) );
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+7) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AutoGradeTestScore\n");
        sb.append("submissionId:" +  submissionIdData.toString() + "\n");
        sb.append("testNo:" +  testNoData.toString() + "\n");
        sb.append("testScore:" +  testScoreData.toString() + "\n");
        sb.append("phase:" +  phaseData.toString() + "\n");
        sb.append("processingTime:" +  processingTimeData.toString() + "\n");
        sb.append("feedback:" +  feedbackData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbAutoGradeTestScore) ) return false;
        DbAutoGradeTestScore dbObj = (DbAutoGradeTestScore)obj;
        if( !submissionIdData.equals( dbObj.submissionIdData ) ) return false;
        if( !testNoData.equals( dbObj.testNoData ) ) return false;
        if( !testScoreData.equals( dbObj.testScoreData ) ) return false;
        if( !phaseData.equals( dbObj.phaseData ) ) return false;
        if( !processingTimeData.equals( dbObj.processingTimeData ) ) return false;
        if( !feedbackData.equals( dbObj.feedbackData ) ) return false;
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
     * Gets the raw data object for testNo attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTestNoData() {
        return testNoData;
    }

    /**
     * Gets the raw data object for testScore attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTestScoreData() {
        return testScoreData;
    }

    /**
     * Gets the raw data object for phase attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getPhaseData() {
        return phaseData;
    }

    /**
     * Gets the raw data object for processingTime attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getProcessingTimeData() {
        return processingTimeData;
    }

    /**
     * Gets the raw data object for feedback attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getFeedbackData() {
        return feedbackData;
    }

    /**
     * Gets the raw data object for timeStamp attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTimeStampData() {
        return timeStampData;
    }

    /**
     * Inserts this object to the database table AutoGradeTestScore.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     */
    public void insert(Connection con) throws SQLException, ObjectNotValidException {
        if( !submissionIdData.isValid() ) throw new ObjectNotValidException("submissionId");
        if( !testNoData.isValid() ) throw new ObjectNotValidException("testNo");
        if( !testScoreData.isValid() ) throw new ObjectNotValidException("testScore");
        if( !phaseData.isValid() ) throw new ObjectNotValidException("phase");
        if( !processingTimeData.isValid() ) throw new ObjectNotValidException("processingTime");
        if( !feedbackData.isValid() ) throw new ObjectNotValidException("feedback");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "insert into AutoGradeTestScore (submissionId, testNo, testScore, phase, processingTime, feedback, timeStamp) values (?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, submissionIdData.jdbcGetValue());
        ps.setObject(2, testNoData.jdbcGetValue());
        ps.setObject(3, testScoreData.jdbcGetValue());
        ps.setObject(4, phaseData.jdbcGetValue());
        ps.setObject(5, processingTimeData.jdbcGetValue());
        ps.setObject(6, feedbackData.jdbcGetValue());
        ps.setObject(7, timeStampData.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows != 1 ) throw new SQLException("Insert did not return 1 row");
    }

    /**
     * Updates the row of this object in the database table AutoGradeTestScore.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be updated does not exist or is not unique.
     */
    public void update(Connection con) throws SQLException, ObjectNotValidException, NoSuchItemException {
        if( !submissionIdData.isValid() ) throw new ObjectNotValidException("submissionId");
        if( !testNoData.isValid() ) throw new ObjectNotValidException("testNo");
        if( !testScoreData.isValid() ) throw new ObjectNotValidException("testScore");
        if( !phaseData.isValid() ) throw new ObjectNotValidException("phase");
        if( !processingTimeData.isValid() ) throw new ObjectNotValidException("processingTime");
        if( !feedbackData.isValid() ) throw new ObjectNotValidException("feedback");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "update AutoGradeTestScore set submissionId = ?, testNo = ?, testScore = ?, phase = ?, processingTime = ?, feedback = ?, timeStamp = ? where submissionId = ? AND testNo = ? AND phase = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, submissionIdData.jdbcGetValue());
        ps.setObject(2, testNoData.jdbcGetValue());
        ps.setObject(3, testScoreData.jdbcGetValue());
        ps.setObject(4, phaseData.jdbcGetValue());
        ps.setObject(5, processingTimeData.jdbcGetValue());
        ps.setObject(6, feedbackData.jdbcGetValue());
        ps.setObject(7, timeStampData.jdbcGetValue());
        ps.setObject(8, submissionIdKC.jdbcGetValue());
        ps.setObject(9, testNoKC.jdbcGetValue());
        ps.setObject(10, phaseKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        try {
            submissionIdKC.jdbcSetValue(submissionIdData.jdbcGetValue());
            testNoKC.jdbcSetValue(testNoData.jdbcGetValue());
            phaseKC.jdbcSetValue(phaseData.jdbcGetValue());
        } catch( InvalidValueException e ) {
            // Ignored (isValid already called)
        }
        if( rows != 1 ) throw new NoSuchItemException();
    }

    /**
     * Deletes the row of this object from the database table AutoGradeTestScore.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws TooManyItemsException if the row to be deleted is not unique.
     * @throws NoSuchItemException if the row to be deleted does not exist.
     */
    public void delete(Connection con) throws SQLException, TooManyItemsException, NoSuchItemException {
        String prepareString = "delete from AutoGradeTestScore where submissionId = ? AND testNo = ? AND phase = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, submissionIdKC.jdbcGetValue());
        ps.setObject(2, testNoKC.jdbcGetValue());
        ps.setObject(3, phaseKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows > 1 ) throw new TooManyItemsException();
        if( rows < 1 ) throw new NoSuchItemException();
    }

    /**
     * Selects the row of this object from the database table AutoGradeTestScore and updates the attributes accordingly.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from AutoGradeTestScore where submissionId = ? AND testNo = ? AND phase = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, submissionIdData.jdbcGetValue());
        ps.setObject(2, testNoData.jdbcGetValue());
        ps.setObject(3, phaseData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database table AutoGradeTestScore.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type AutoGradeTestScore. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from AutoGradeTestScore";
        else
            prepareString = "select * from AutoGradeTestScore where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, AutoGradeTestScore.class );
    }
}

// End of file.
