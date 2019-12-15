package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for Property.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanProperty extends DbProperty {

    /**
     * Default constructor.
     */
    public BeanProperty() {
        super();
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
     * Gets the value of the property key.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getKey() {
        return getKeyData().getValue();
    }

    /**
     * Sets the value of the property key.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setKey( java.lang.Integer newValue ) throws InvalidValueException {
        getKeyData().setValue( newValue );
    }

    /**
     * Gets the value of the property value.
     * @return Value as java.lang.String.
     */
    public java.lang.String getValue() {
        return getValueData().getValue();
    }

    /**
     * Sets the value of the property value.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setValue( java.lang.String newValue ) throws InvalidValueException {
        getValueData().setValue( newValue );
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
