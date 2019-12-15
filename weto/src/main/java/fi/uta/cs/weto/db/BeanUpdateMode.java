package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for UpdateMode.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanUpdateMode extends DbUpdateMode {

    /**
     * Default constructor.
     */
    public BeanUpdateMode() {
        super();
    }

    /**
     * Gets the value of the property inUpdateMode.
     * @return Value as java.lang.Boolean.
     */
    public java.lang.Boolean getInUpdateMode() {
        return getInUpdateModeData().getValue();
    }

    /**
     * Sets the value of the property inUpdateMode.
     * @param newValue New value as java.lang.Boolean.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setInUpdateMode( java.lang.Boolean newValue ) throws InvalidValueException {
        getInUpdateModeData().setValue( newValue );
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
