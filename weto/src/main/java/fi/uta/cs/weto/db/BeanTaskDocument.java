package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for TaskDocument.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanTaskDocument extends DbTaskDocument {

    /**
     * Default constructor.
     */
    public BeanTaskDocument() {
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
     * Gets the value of the property documentId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getDocumentId() {
        return getDocumentIdData().getValue();
    }

    /**
     * Sets the value of the property documentId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setDocumentId( java.lang.Integer newValue ) throws InvalidValueException {
        getDocumentIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property userId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getUserId() {
        return getUserIdData().getValue();
    }

    /**
     * Sets the value of the property userId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setUserId( java.lang.Integer newValue ) throws InvalidValueException {
        getUserIdData().setValue( newValue );
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
}

// End of file.
