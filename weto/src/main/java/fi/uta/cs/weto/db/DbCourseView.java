package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for view CourseView.
 * 
 */
public class DbCourseView extends SqlAssignableObject implements Cloneable {
    private SqlInteger masterTaskIdData;
    private SqlInteger courseTaskIdData;
    private SqlLongvarchar nameData;
    private SqlInteger subjectIdData;
    private SqlLongvarchar subjectNameData;
    private SqlInteger databaseIdData;

    /**
     * Default constructor.
     */
    public DbCourseView() {
        super();
        masterTaskIdData = new SqlInteger();
        courseTaskIdData = new SqlInteger();
        nameData = new SqlLongvarchar();
        subjectIdData = new SqlInteger();
        subjectNameData = new SqlLongvarchar();
    // The database where the course is stored.
        databaseIdData = new SqlInteger();
    }

    /**
     * Updates the data from the given ResultSet object.
     * @param resultSet ResultSet object containing the data.
     * @param baseIndex Base index of the columns in the ResultSet (exclusive).
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     */
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        masterTaskIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+1) );
        courseTaskIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        nameData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+3) );
        subjectIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+4) );
        subjectNameData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+5) );
        databaseIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+6) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CourseView\n");
        sb.append("masterTaskId:" +  masterTaskIdData.toString() + "\n");
        sb.append("courseTaskId:" +  courseTaskIdData.toString() + "\n");
        sb.append("name:" +  nameData.toString() + "\n");
        sb.append("subjectId:" +  subjectIdData.toString() + "\n");
        sb.append("subjectName:" +  subjectNameData.toString() + "\n");
        sb.append("databaseId:" +  databaseIdData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbCourseView) ) return false;
        DbCourseView dbObj = (DbCourseView)obj;
        if( !masterTaskIdData.equals( dbObj.masterTaskIdData ) ) return false;
        if( !courseTaskIdData.equals( dbObj.courseTaskIdData ) ) return false;
        if( !nameData.equals( dbObj.nameData ) ) return false;
        if( !subjectIdData.equals( dbObj.subjectIdData ) ) return false;
        if( !subjectNameData.equals( dbObj.subjectNameData ) ) return false;
        if( !databaseIdData.equals( dbObj.databaseIdData ) ) return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Gets the raw data object for masterTaskId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getMasterTaskIdData() {
        return masterTaskIdData;
    }

    /**
     * Gets the raw data object for courseTaskId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getCourseTaskIdData() {
        return courseTaskIdData;
    }

    /**
     * Gets the raw data object for name attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getNameData() {
        return nameData;
    }

    /**
     * Gets the raw data object for subjectId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getSubjectIdData() {
        return subjectIdData;
    }

    /**
     * Gets the raw data object for subjectName attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getSubjectNameData() {
        return subjectNameData;
    }

    /**
     * Gets the raw data object for databaseId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getDatabaseIdData() {
        return databaseIdData;
    }

    /**
     * Selects the row of this object from the database view CourseView and updates the attributes accordingly.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from CourseView where masterTaskId = ? AND courseTaskId = ? AND name = ? AND subjectId = ? AND subjectName = ? AND databaseId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, masterTaskIdData.jdbcGetValue());
        ps.setObject(2, courseTaskIdData.jdbcGetValue());
        ps.setObject(3, nameData.jdbcGetValue());
        ps.setObject(4, subjectIdData.jdbcGetValue());
        ps.setObject(5, subjectNameData.jdbcGetValue());
        ps.setObject(6, databaseIdData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database view CourseView.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type CourseView. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from CourseView";
        else
            prepareString = "select * from CourseView where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, CourseView.class );
    }
}

// End of file.
