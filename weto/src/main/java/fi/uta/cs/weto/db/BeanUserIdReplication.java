package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for UserIdReplication.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanUserIdReplication extends DbUserIdReplication {

    /**
     * Default constructor.
     */
    public BeanUserIdReplication() {
        super();
    }

    /**
     * Gets the value of the property masterDbUserId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getMasterDbUserId() {
        return getMasterDbUserIdData().getValue();
    }

    /**
     * Sets the value of the property masterDbUserId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setMasterDbUserId( java.lang.Integer newValue ) throws InvalidValueException {
        getMasterDbUserIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property courseDbUserId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getCourseDbUserId() {
        return getCourseDbUserIdData().getValue();
    }

    /**
     * Sets the value of the property courseDbUserId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setCourseDbUserId( java.lang.Integer newValue ) throws InvalidValueException {
        getCourseDbUserIdData().setValue( newValue );
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
