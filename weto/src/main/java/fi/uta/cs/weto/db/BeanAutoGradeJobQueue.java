package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for AutoGradeJobQueue.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanAutoGradeJobQueue extends DbAutoGradeJobQueue {

    /**
     * Default constructor.
     */
    public BeanAutoGradeJobQueue() {
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
     * Gets the value of the property dbId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getDbId() {
        return getDbIdData().getValue();
    }

    /**
     * Sets the value of the property dbId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setDbId( java.lang.Integer newValue ) throws InvalidValueException {
        getDbIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property jobType.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getJobType() {
        return getJobTypeData().getValue();
    }

    /**
     * Sets the value of the property jobType.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setJobType( java.lang.Integer newValue ) throws InvalidValueException {
        getJobTypeData().setValue( newValue );
    }

    /**
     * Gets the value of the property refId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getRefId() {
        return getRefIdData().getValue();
    }

    /**
     * Sets the value of the property refId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setRefId( java.lang.Integer newValue ) throws InvalidValueException {
        getRefIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property queuePhase.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getQueuePhase() {
        return getQueuePhaseData().getValue();
    }

    /**
     * Sets the value of the property queuePhase.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setQueuePhase( java.lang.Integer newValue ) throws InvalidValueException {
        getQueuePhaseData().setValue( newValue );
    }

    /**
     * Gets the value of the property testRunning.
     * @return Value as java.lang.Boolean.
     */
    public java.lang.Boolean getTestRunning() {
        return getTestRunningData().getValue();
    }

    /**
     * Sets the value of the property testRunning.
     * @param newValue New value as java.lang.Boolean.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setTestRunning( java.lang.Boolean newValue ) throws InvalidValueException {
        getTestRunningData().setValue( newValue );
    }

    /**
     * Gets the value of the property jobComment.
     * @return Value as java.lang.String.
     */
    public java.lang.String getJobComment() {
        return getJobCommentData().getValue();
    }

    /**
     * Sets the value of the property jobComment.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setJobComment( java.lang.String newValue ) throws InvalidValueException {
        getJobCommentData().setValue( newValue );
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
