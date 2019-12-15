package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for AutoGrading.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanAutoGrading extends DbAutoGrading {

    /**
     * Default constructor.
     */
    public BeanAutoGrading() {
        super();
    }

    /**
     * Gets the value of the property taskId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getTaskId() {
        return getTaskIdData().getValue();
    }

    /**
     * Sets the value of the property taskId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setTaskId( java.lang.Integer newValue ) throws InvalidValueException {
        getTaskIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property testDocId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getTestDocId() {
        return getTestDocIdData().getValue();
    }

    /**
     * Sets the value of the property testDocId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setTestDocId( java.lang.Integer newValue ) throws InvalidValueException {
        getTestDocIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property properties.
     * @return Value as java.lang.String.
     */
    public java.lang.String getProperties() {
        return getPropertiesData().getValue();
    }

    /**
     * Sets the value of the property properties.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setProperties( java.lang.String newValue ) throws InvalidValueException {
        getPropertiesData().setValue( newValue );
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
}

// End of file.
