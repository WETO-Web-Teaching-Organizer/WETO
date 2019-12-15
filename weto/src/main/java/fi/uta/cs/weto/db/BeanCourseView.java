package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for CourseView.
 * This class extends the database access class by adding bean property getters.
 */
public class BeanCourseView extends DbCourseView {

    /**
     * Default constructor.
     */
    public BeanCourseView() {
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

    /**
     * Gets the value of the property name.
     * @return Value as java.lang.String.
     */
    public java.lang.String getName() {
        return getNameData().getValue();
    }
    /**
     * Sets the value of the property name.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setName( java.lang.String newValue ) throws InvalidValueException {
        getNameData().setValue( newValue );
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
     * Gets the value of the property subjectName.
     * @return Value as java.lang.String.
     */
    public java.lang.String getSubjectName() {
        return getSubjectNameData().getValue();
    }
    /**
     * Sets the value of the property subjectName.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setSubjectName( java.lang.String newValue ) throws InvalidValueException {
        getSubjectNameData().setValue( newValue );
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
}

// End of file.
