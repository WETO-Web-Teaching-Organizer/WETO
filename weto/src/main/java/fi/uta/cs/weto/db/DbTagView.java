package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for view TagView.
 * 
 */
public class DbTagView extends SqlAssignableObject implements Cloneable {
    private SqlInteger idData;
    private SqlInteger authorIdData;
    private SqlInteger statusData;
    private SqlInteger rankData;
    private SqlInteger timeStampData;
    private SqlLongvarchar textData;
    private SqlInteger typeData;
    private SqlInteger taggedIdData;
    private SqlLongvarchar firstNameData;
    private SqlLongvarchar lastNameData;

    /**
     * Default constructor.
     */
    public DbTagView() {
        super();
        idData = new SqlInteger();
        authorIdData = new SqlInteger();
        statusData = new SqlInteger();
        rankData = new SqlInteger();
        timeStampData = new SqlInteger();
        textData = new SqlLongvarchar();
    // Specifies the type of the tag (e.g. note, forum message, review) as an integer id.
        typeData = new SqlInteger();
        taggedIdData = new SqlInteger();
        firstNameData = new SqlLongvarchar();
        lastNameData = new SqlLongvarchar();
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
        authorIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        statusData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+3) );
        rankData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+4) );
        timeStampData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+5) );
        textData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+6) );
        typeData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+7) );
        taggedIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+8) );
        firstNameData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+9) );
        lastNameData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+10) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("TagView\n");
        sb.append("id:" +  idData.toString() + "\n");
        sb.append("authorId:" +  authorIdData.toString() + "\n");
        sb.append("status:" +  statusData.toString() + "\n");
        sb.append("rank:" +  rankData.toString() + "\n");
        sb.append("timeStamp:" +  timeStampData.toString() + "\n");
        sb.append("text:" +  textData.toString() + "\n");
        sb.append("type:" +  typeData.toString() + "\n");
        sb.append("taggedId:" +  taggedIdData.toString() + "\n");
        sb.append("firstName:" +  firstNameData.toString() + "\n");
        sb.append("lastName:" +  lastNameData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbTagView) ) return false;
        DbTagView dbObj = (DbTagView)obj;
        if( !idData.equals( dbObj.idData ) ) return false;
        if( !authorIdData.equals( dbObj.authorIdData ) ) return false;
        if( !statusData.equals( dbObj.statusData ) ) return false;
        if( !rankData.equals( dbObj.rankData ) ) return false;
        if( !timeStampData.equals( dbObj.timeStampData ) ) return false;
        if( !textData.equals( dbObj.textData ) ) return false;
        if( !typeData.equals( dbObj.typeData ) ) return false;
        if( !taggedIdData.equals( dbObj.taggedIdData ) ) return false;
        if( !firstNameData.equals( dbObj.firstNameData ) ) return false;
        if( !lastNameData.equals( dbObj.lastNameData ) ) return false;
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
     * Gets the raw data object for authorId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getAuthorIdData() {
        return authorIdData;
    }

    /**
     * Gets the raw data object for status attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getStatusData() {
        return statusData;
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
     * Gets the raw data object for text attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getTextData() {
        return textData;
    }

    /**
     * Gets the raw data object for type attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTypeData() {
        return typeData;
    }

    /**
     * Gets the raw data object for taggedId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTaggedIdData() {
        return taggedIdData;
    }

    /**
     * Gets the raw data object for firstName attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getFirstNameData() {
        return firstNameData;
    }

    /**
     * Gets the raw data object for lastName attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getLastNameData() {
        return lastNameData;
    }

    /**
     * Selects the row of this object from the database view TagView and updates the attributes accordingly.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from TagView where id = ? AND authorId = ? AND status = ? AND rank = ? AND timeStamp = ? AND text = ? AND type = ? AND taggedId = ? AND firstName = ? AND lastName = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, idData.jdbcGetValue());
        ps.setObject(2, authorIdData.jdbcGetValue());
        ps.setObject(3, statusData.jdbcGetValue());
        ps.setObject(4, rankData.jdbcGetValue());
        ps.setObject(5, timeStampData.jdbcGetValue());
        ps.setObject(6, textData.jdbcGetValue());
        ps.setObject(7, typeData.jdbcGetValue());
        ps.setObject(8, taggedIdData.jdbcGetValue());
        ps.setObject(9, firstNameData.jdbcGetValue());
        ps.setObject(10, lastNameData.jdbcGetValue());
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
     * Constructs and returns a selection iterator for rows in database view TagView.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type TagView. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from TagView";
        else
            prepareString = "select * from TagView where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, TagView.class );
    }
}

// End of file.
