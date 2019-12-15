package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for Submission.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanSubmission extends DbSubmission {

    /**
     * Default constructor.
     */
    public BeanSubmission() {
        super();
    }

    /**
     * Gets the value of the property id.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getId() {
        return getIdData().getValue();
    }

    /**
     * Sets the value of the property id.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setId( java.lang.Integer newValue ) throws InvalidValueException {
        getIdData().setValue( newValue );
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
     * Gets the value of the property autoGradeMark.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getAutoGradeMark() {
        return getAutoGradeMarkData().getValue();
    }

    /**
     * Sets the value of the property autoGradeMark.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setAutoGradeMark( java.lang.Integer newValue ) throws InvalidValueException {
        getAutoGradeMarkData().setValue( newValue );
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
     * Gets the value of the property message.
     * @return Value as java.lang.String.
     */
    public java.lang.String getMessage() {
        return getMessageData().getValue();
    }

    /**
     * Sets the value of the property message.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setMessage( java.lang.String newValue ) throws InvalidValueException {
        getMessageData().setValue( newValue );
    }

    /**
     * Gets the value of the property error.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getError() {
        return getErrorData().getValue();
    }

    /**
     * Sets the value of the property error.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setError( java.lang.Integer newValue ) throws InvalidValueException {
        getErrorData().setValue( newValue );
    }

    /**
     * Gets the value of the property fileCount.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getFileCount() {
        return getFileCountData().getValue();
    }

    /**
     * Sets the value of the property fileCount.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setFileCount( java.lang.Integer newValue ) throws InvalidValueException {
        getFileCountData().setValue( newValue );
    }
}

// End of file.
