package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for DatabasePool.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanDatabasePool extends DbDatabasePool {

    /**
     * Default constructor.
     */
    public BeanDatabasePool() {
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
     * Gets the value of the property name.
     * @return Value as java.lang.String.
     */
    public java.lang.String getName() {
        return getNameData().getValue();
    }

    /**
     * Sets the value of the property name.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setName( java.lang.String newValue ) throws InvalidValueException {
        getNameData().setValue( newValue );
    }

    /**
     * Gets the value of the property url.
     * @return Value as java.lang.String.
     */
    public java.lang.String getUrl() {
        return getUrlData().getValue();
    }

    /**
     * Sets the value of the property url.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setUrl( java.lang.String newValue ) throws InvalidValueException {
        getUrlData().setValue( newValue );
    }

    /**
     * Gets the value of the property username.
     * @return Value as java.lang.String.
     */
    public java.lang.String getUsername() {
        return getUsernameData().getValue();
    }

    /**
     * Sets the value of the property username.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setUsername( java.lang.String newValue ) throws InvalidValueException {
        getUsernameData().setValue( newValue );
    }

    /**
     * Gets the value of the property password.
     * @return Value as java.lang.String.
     */
    public java.lang.String getPassword() {
        return getPasswordData().getValue();
    }

    /**
     * Sets the value of the property password.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setPassword( java.lang.String newValue ) throws InvalidValueException {
        getPasswordData().setValue( newValue );
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
