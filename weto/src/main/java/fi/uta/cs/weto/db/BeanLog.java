package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for Log.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanLog extends DbLog {

    /**
     * Default constructor.
     */
    public BeanLog() {
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
     * Gets the value of the property event.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getEvent() {
        return getEventData().getValue();
    }

    /**
     * Sets the value of the property event.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setEvent( java.lang.Integer newValue ) throws InvalidValueException {
        getEventData().setValue( newValue );
    }

    /**
     * Gets the value of the property par1.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getPar1() {
        return getPar1Data().getValue();
    }

    /**
     * Sets the value of the property par1.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setPar1( java.lang.Integer newValue ) throws InvalidValueException {
        getPar1Data().setValue( newValue );
    }

    /**
     * Gets the value of the property par2.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getPar2() {
        return getPar2Data().getValue();
    }

    /**
     * Sets the value of the property par2.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setPar2( java.lang.Integer newValue ) throws InvalidValueException {
        getPar2Data().setValue( newValue );
    }

    /**
     * Gets the value of the property address.
     * @return Value as java.lang.String.
     */
    public java.lang.String getAddress() {
        return getAddressData().getValue();
    }

    /**
     * Sets the value of the property address.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setAddress( java.lang.String newValue ) throws InvalidValueException {
        getAddressData().setValue( newValue );
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
