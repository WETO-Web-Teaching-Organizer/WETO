package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for GradeView.
 * This class extends the database access class by adding bean property getters.
 */
public class BeanGradeView extends DbGradeView {

    /**
     * Default constructor.
     */
    public BeanGradeView() {
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
     * Gets the value of the property reviewerId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getReviewerId() {
        return getReviewerIdData().getValue();
    }
    /**
     * Sets the value of the property reviewerId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setReviewerId( java.lang.Integer newValue ) throws InvalidValueException {
        getReviewerIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property receiverId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getReceiverId() {
        return getReceiverIdData().getValue();
    }
    /**
     * Sets the value of the property receiverId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setReceiverId( java.lang.Integer newValue ) throws InvalidValueException {
        getReceiverIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property mark.
     * @return Value as java.lang.Float.
     */
    public java.lang.Float getMark() {
        return getMarkData().getValue();
    }
    /**
     * Sets the value of the property mark.
     * @param newValue New value as java.lang.Float.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setMark( java.lang.Float newValue ) throws InvalidValueException {
        getMarkData().setValue( newValue );
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
     * Gets the value of the property reviewerFirstName.
     * @return Value as java.lang.String.
     */
    public java.lang.String getReviewerFirstName() {
        return getReviewerFirstNameData().getValue();
    }
    /**
     * Sets the value of the property reviewerFirstName.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setReviewerFirstName( java.lang.String newValue ) throws InvalidValueException {
        getReviewerFirstNameData().setValue( newValue );
    }

    /**
     * Gets the value of the property reviewerLastName.
     * @return Value as java.lang.String.
     */
    public java.lang.String getReviewerLastName() {
        return getReviewerLastNameData().getValue();
    }
    /**
     * Sets the value of the property reviewerLastName.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setReviewerLastName( java.lang.String newValue ) throws InvalidValueException {
        getReviewerLastNameData().setValue( newValue );
    }

    /**
     * Gets the value of the property receiverFirstName.
     * @return Value as java.lang.String.
     */
    public java.lang.String getReceiverFirstName() {
        return getReceiverFirstNameData().getValue();
    }
    /**
     * Sets the value of the property receiverFirstName.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setReceiverFirstName( java.lang.String newValue ) throws InvalidValueException {
        getReceiverFirstNameData().setValue( newValue );
    }

    /**
     * Gets the value of the property receiverLastName.
     * @return Value as java.lang.String.
     */
    public java.lang.String getReceiverLastName() {
        return getReceiverLastNameData().getValue();
    }
    /**
     * Sets the value of the property receiverLastName.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setReceiverLastName( java.lang.String newValue ) throws InvalidValueException {
        getReceiverLastNameData().setValue( newValue );
    }

    /**
     * Gets the value of the property taskName.
     * @return Value as java.lang.String.
     */
    public java.lang.String getTaskName() {
        return getTaskNameData().getValue();
    }
    /**
     * Sets the value of the property taskName.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setTaskName( java.lang.String newValue ) throws InvalidValueException {
        getTaskNameData().setValue( newValue );
    }
}

// End of file.
