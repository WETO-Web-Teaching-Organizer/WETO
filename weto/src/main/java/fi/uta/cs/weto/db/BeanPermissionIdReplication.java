package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for PermissionIdReplication.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanPermissionIdReplication extends DbPermissionIdReplication {

    /**
     * Default constructor.
     */
    public BeanPermissionIdReplication() {
        super();
    }

    /**
     * Gets the value of the property masterDbPermissionId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getMasterDbPermissionId() {
        return getMasterDbPermissionIdData().getValue();
    }

    /**
     * Sets the value of the property masterDbPermissionId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setMasterDbPermissionId( java.lang.Integer newValue ) throws InvalidValueException {
        getMasterDbPermissionIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property courseDbPermissionId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getCourseDbPermissionId() {
        return getCourseDbPermissionIdData().getValue();
    }

    /**
     * Sets the value of the property courseDbPermissionId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setCourseDbPermissionId( java.lang.Integer newValue ) throws InvalidValueException {
        getCourseDbPermissionIdData().setValue( newValue );
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
