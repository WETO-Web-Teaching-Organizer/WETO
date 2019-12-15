package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for RightsCluster.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanRightsCluster extends DbRightsCluster {

    /**
     * Default constructor.
     */
    public BeanRightsCluster() {
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
     * Gets the value of the property ownerViewBits.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getOwnerViewBits() {
        return getOwnerViewBitsData().getValue();
    }

    /**
     * Sets the value of the property ownerViewBits.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setOwnerViewBits( java.lang.Integer newValue ) throws InvalidValueException {
        getOwnerViewBitsData().setValue( newValue );
    }

    /**
     * Gets the value of the property ownerUpdateBits.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getOwnerUpdateBits() {
        return getOwnerUpdateBitsData().getValue();
    }

    /**
     * Sets the value of the property ownerUpdateBits.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setOwnerUpdateBits( java.lang.Integer newValue ) throws InvalidValueException {
        getOwnerUpdateBitsData().setValue( newValue );
    }

    /**
     * Gets the value of the property ownerCreateBits.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getOwnerCreateBits() {
        return getOwnerCreateBitsData().getValue();
    }

    /**
     * Sets the value of the property ownerCreateBits.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setOwnerCreateBits( java.lang.Integer newValue ) throws InvalidValueException {
        getOwnerCreateBitsData().setValue( newValue );
    }

    /**
     * Gets the value of the property ownerDeleteBits.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getOwnerDeleteBits() {
        return getOwnerDeleteBitsData().getValue();
    }

    /**
     * Sets the value of the property ownerDeleteBits.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setOwnerDeleteBits( java.lang.Integer newValue ) throws InvalidValueException {
        getOwnerDeleteBitsData().setValue( newValue );
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
     * Gets the value of the property generalViewBits.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getGeneralViewBits() {
        return getGeneralViewBitsData().getValue();
    }

    /**
     * Sets the value of the property generalViewBits.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setGeneralViewBits( java.lang.Integer newValue ) throws InvalidValueException {
        getGeneralViewBitsData().setValue( newValue );
    }

    /**
     * Gets the value of the property generalUpdateBits.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getGeneralUpdateBits() {
        return getGeneralUpdateBitsData().getValue();
    }

    /**
     * Sets the value of the property generalUpdateBits.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setGeneralUpdateBits( java.lang.Integer newValue ) throws InvalidValueException {
        getGeneralUpdateBitsData().setValue( newValue );
    }

    /**
     * Gets the value of the property generalCreateBits.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getGeneralCreateBits() {
        return getGeneralCreateBitsData().getValue();
    }

    /**
     * Sets the value of the property generalCreateBits.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setGeneralCreateBits( java.lang.Integer newValue ) throws InvalidValueException {
        getGeneralCreateBitsData().setValue( newValue );
    }

    /**
     * Gets the value of the property generalDeleteBits.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getGeneralDeleteBits() {
        return getGeneralDeleteBitsData().getValue();
    }

    /**
     * Sets the value of the property generalDeleteBits.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setGeneralDeleteBits( java.lang.Integer newValue ) throws InvalidValueException {
        getGeneralDeleteBitsData().setValue( newValue );
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
}

// End of file.
