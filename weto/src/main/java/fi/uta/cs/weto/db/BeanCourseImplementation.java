package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for CourseImplementation.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanCourseImplementation extends DbCourseImplementation {

    /**
     * Default constructor.
     */
    public BeanCourseImplementation() {
        super();
    }

    /**
     * Gets the value of the property masterTaskId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getMasterTaskId() {
        return getMasterTaskIdData().getValue();
    }

    /**
     * Sets the value of the property masterTaskId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setMasterTaskId( java.lang.Integer newValue ) throws InvalidValueException {
        getMasterTaskIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property subjectId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getSubjectId() {
        return getSubjectIdData().getValue();
    }

    /**
     * Sets the value of the property subjectId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setSubjectId( java.lang.Integer newValue ) throws InvalidValueException {
        getSubjectIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property databaseId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getDatabaseId() {
        return getDatabaseIdData().getValue();
    }

    /**
     * Sets the value of the property databaseId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setDatabaseId( java.lang.Integer newValue ) throws InvalidValueException {
        getDatabaseIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property status.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getStatus() {
        return getStatusData().getValue();
    }

    /**
     * Sets the value of the property status.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setStatus( java.lang.Integer newValue ) throws InvalidValueException {
        getStatusData().setValue( newValue );
    }

    /**
     * Gets the value of the property timeStamp.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getTimeStamp() {
        return getTimeStampData().getValue();
    }

    /**
     * Sets the value of the property timeStamp.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setTimeStamp( java.lang.Integer newValue ) throws InvalidValueException {
        getTimeStampData().setValue( newValue );
    }

    /**
     * Gets the value of the property courseTaskId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getCourseTaskId() {
        return getCourseTaskIdData().getValue();
    }

    /**
     * Sets the value of the property courseTaskId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setCourseTaskId( java.lang.Integer newValue ) throws InvalidValueException {
        getCourseTaskIdData().setValue( newValue );
    }
}

// End of file.
