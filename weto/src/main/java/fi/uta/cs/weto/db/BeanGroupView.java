package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for GroupView.
 * This class extends the database access class by adding bean property getters.
 */
public class BeanGroupView extends DbGroupView {

    /**
     * Default constructor.
     */
    public BeanGroupView() {
        super();
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
}

// End of file.
