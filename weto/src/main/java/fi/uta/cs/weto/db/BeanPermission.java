package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for Permission.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanPermission extends DbPermission {

    /**
     * Default constructor.
     */
    public BeanPermission() {
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
     * Gets the value of the property userRefId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getUserRefId() {
        return getUserRefIdData().getValue();
    }

    /**
     * Sets the value of the property userRefId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setUserRefId( java.lang.Integer newValue ) throws InvalidValueException {
        getUserRefIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property userRefType.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getUserRefType() {
        return getUserRefTypeData().getValue();
    }

    /**
     * Sets the value of the property userRefType.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setUserRefType( java.lang.Integer newValue ) throws InvalidValueException {
        getUserRefTypeData().setValue( newValue );
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
     * Gets the value of the property type.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getType() {
        return getTypeData().getValue();
    }

    /**
     * Sets the value of the property type.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setType( java.lang.Integer newValue ) throws InvalidValueException {
        getTypeData().setValue( newValue );
    }

    /**
     * Gets the value of the property startDate.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getStartDate() {
        return getStartDateData().getValue();
    }

    /**
     * Sets the value of the property startDate.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setStartDate( java.lang.Integer newValue ) throws InvalidValueException {
        getStartDateData().setValue( newValue );
    }

    /**
     * Gets the value of the property endDate.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getEndDate() {
        return getEndDateData().getValue();
    }

    /**
     * Sets the value of the property endDate.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setEndDate( java.lang.Integer newValue ) throws InvalidValueException {
        getEndDateData().setValue( newValue );
    }

    /**
     * Gets the value of the property detail.
     * @return Value as java.lang.String.
     */
    public java.lang.String getDetail() {
        return getDetailData().getValue();
    }

    /**
     * Sets the value of the property detail.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setDetail( java.lang.String newValue ) throws InvalidValueException {
        getDetailData().setValue( newValue );
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
