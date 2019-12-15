package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for StudentView.
 * This class extends the database access class by adding bean property getters.
 */
public class BeanStudentView extends DbStudentView {

    /**
     * Default constructor.
     */
    public BeanStudentView() {
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
     * Gets the value of the property studentNumber.
     * @return Value as java.lang.String.
     */
    public java.lang.String getStudentNumber() {
        return getStudentNumberData().getValue();
    }
    /**
     * Sets the value of the property studentNumber.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setStudentNumber( java.lang.String newValue ) throws InvalidValueException {
        getStudentNumberData().setValue( newValue );
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
}

// End of file.
