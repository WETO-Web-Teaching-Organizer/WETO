package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for AutoGradeTestScore.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanAutoGradeTestScore extends DbAutoGradeTestScore {

    /**
     * Default constructor.
     */
    public BeanAutoGradeTestScore() {
        super();
    }

    /**
     * Gets the value of the property submissionId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getSubmissionId() {
        return getSubmissionIdData().getValue();
    }

    /**
     * Sets the value of the property submissionId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setSubmissionId( java.lang.Integer newValue ) throws InvalidValueException {
        getSubmissionIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property testNo.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getTestNo() {
        return getTestNoData().getValue();
    }

    /**
     * Sets the value of the property testNo.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setTestNo( java.lang.Integer newValue ) throws InvalidValueException {
        getTestNoData().setValue( newValue );
    }

    /**
     * Gets the value of the property testScore.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getTestScore() {
        return getTestScoreData().getValue();
    }

    /**
     * Sets the value of the property testScore.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setTestScore( java.lang.Integer newValue ) throws InvalidValueException {
        getTestScoreData().setValue( newValue );
    }

    /**
     * Gets the value of the property phase.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getPhase() {
        return getPhaseData().getValue();
    }

    /**
     * Sets the value of the property phase.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setPhase( java.lang.Integer newValue ) throws InvalidValueException {
        getPhaseData().setValue( newValue );
    }

    /**
     * Gets the value of the property processingTime.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getProcessingTime() {
        return getProcessingTimeData().getValue();
    }

    /**
     * Sets the value of the property processingTime.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setProcessingTime( java.lang.Integer newValue ) throws InvalidValueException {
        getProcessingTimeData().setValue( newValue );
    }

    /**
     * Gets the value of the property feedback.
     * @return Value as java.lang.String.
     */
    public java.lang.String getFeedback() {
        return getFeedbackData().getValue();
    }

    /**
     * Sets the value of the property feedback.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setFeedback( java.lang.String newValue ) throws InvalidValueException {
        getFeedbackData().setValue( newValue );
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
