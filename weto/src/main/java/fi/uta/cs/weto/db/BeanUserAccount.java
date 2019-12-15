package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for UserAccount.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanUserAccount extends DbUserAccount {

    /**
     * Default constructor.
     */
    public BeanUserAccount() {
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
     * Gets the value of the property loginName.
     * @return Value as java.lang.String.
     */
    public java.lang.String getLoginName() {
        return getLoginNameData().getValue();
    }

    /**
     * Sets the value of the property loginName.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setLoginName( java.lang.String newValue ) throws InvalidValueException {
        getLoginNameData().setValue( newValue );
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
     * Gets the value of the property firstName.
     * @return Value as java.lang.String.
     */
    public java.lang.String getFirstName() {
        return getFirstNameData().getValue();
    }

    /**
     * Sets the value of the property firstName.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setFirstName( java.lang.String newValue ) throws InvalidValueException {
        getFirstNameData().setValue( newValue );
    }

    /**
     * Gets the value of the property lastName.
     * @return Value as java.lang.String.
     */
    public java.lang.String getLastName() {
        return getLastNameData().getValue();
    }

    /**
     * Sets the value of the property lastName.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setLastName( java.lang.String newValue ) throws InvalidValueException {
        getLastNameData().setValue( newValue );
    }

    /**
     * Gets the value of the property email.
     * @return Value as java.lang.String.
     */
    public java.lang.String getEmail() {
        return getEmailData().getValue();
    }

    /**
     * Sets the value of the property email.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setEmail( java.lang.String newValue ) throws InvalidValueException {
        getEmailData().setValue( newValue );
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
