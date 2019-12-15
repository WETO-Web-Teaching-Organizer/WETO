package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for Subject.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanSubject extends DbSubject {

    /**
     * Default constructor.
     */
    public BeanSubject() {
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
