package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for table Student.
 * 
 */
public class DbStudent extends SqlAssignableObject implements Cloneable {
    private SqlInteger userIdData;
    private SqlInteger userIdKC;
    private SqlLongvarchar studentNumberData;
    private SqlInteger timeStampData;

    /**
     * Default constructor.
     */
    public DbStudent() {
        super();
        userIdData = new SqlInteger();
        userIdKC = new SqlInteger();
        studentNumberData = new SqlLongvarchar();
        timeStampData = new SqlInteger();
        userIdData.setPrime(true);
    }

    /**
     * Updates the data from the given ResultSet object.
     * @param resultSet ResultSet object containing the data.
     * @param baseIndex Base index of the columns in the ResultSet (exclusive).
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     */
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        userIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+1) );
        userIdKC.jdbcSetValue(userIdData.jdbcGetValue());
        studentNumberData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+2) );
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Student\n");
        sb.append("userId:" +  userIdData.toString() + "\n");
        sb.append("studentNumber:" +  studentNumberData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbStudent) ) return false;
        DbStudent dbObj = (DbStudent)obj;
        if( !userIdData.equals( dbObj.userIdData ) ) return false;
        if( !studentNumberData.equals( dbObj.studentNumberData ) ) return false;
        if( !timeStampData.equals( dbObj.timeStampData ) ) return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Gets the raw data object for userId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getUserIdData() {
        return userIdData;
    }

    /**
     * Gets the raw data object for studentNumber attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getStudentNumberData() {
        return studentNumberData;
    }

    /**
     * Gets the raw data object for timeStamp attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTimeStampData() {
        return timeStampData;
    }

    /**
     * Inserts this object to the database table Student.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     */
    public void insert(Connection con) throws SQLException, ObjectNotValidException {
        if( !userIdData.isValid() ) throw new ObjectNotValidException("userId");
        if( !studentNumberData.isValid() ) throw new ObjectNotValidException("studentNumber");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "insert into Student (userId, studentNumber, timeStamp) values (?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, userIdData.jdbcGetValue());
        ps.setObject(2, studentNumberData.jdbcGetValue());
        ps.setObject(3, timeStampData.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows != 1 ) throw new SQLException("Insert did not return 1 row");
    }

    /**
     * Updates the row of this object in the database table Student.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws ObjectNotValidException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be updated does not exist or is not unique.
     */
    public void update(Connection con) throws SQLException, ObjectNotValidException, NoSuchItemException {
        if( !userIdData.isValid() ) throw new ObjectNotValidException("userId");
        if( !studentNumberData.isValid() ) throw new ObjectNotValidException("studentNumber");
        if( !timeStampData.isValid() ) throw new ObjectNotValidException("timeStamp");
        String prepareString = "update Student set userId = ?, studentNumber = ?, timeStamp = ? where userId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, userIdData.jdbcGetValue());
        ps.setObject(2, studentNumberData.jdbcGetValue());
        ps.setObject(3, timeStampData.jdbcGetValue());
        ps.setObject(4, userIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        try {
            userIdKC.jdbcSetValue(userIdData.jdbcGetValue());
        } catch( InvalidValueException e ) {
            // Ignored (isValid already called)
        }
        if( rows != 1 ) throw new NoSuchItemException();
    }

    /**
     * Deletes the row of this object from the database table Student.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws TooManyItemsException if the row to be deleted is not unique.
     * @throws NoSuchItemException if the row to be deleted does not exist.
     */
    public void delete(Connection con) throws SQLException, TooManyItemsException, NoSuchItemException {
        String prepareString = "delete from Student where userId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, userIdKC.jdbcGetValue());
        int rows = ps.executeUpdate();
        ps.close();
        if( rows > 1 ) throw new TooManyItemsException();
        if( rows < 1 ) throw new NoSuchItemException();
    }

    /**
     * Selects the row of this object from the database table Student and updates the attributes accordingly.
     * The row is identified by the primary key attribute(s).
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from Student where userId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, userIdData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database table Student.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type Student. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from Student";
        else
            prepareString = "select * from Student where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, Student.class );
    }
}

// End of file.
