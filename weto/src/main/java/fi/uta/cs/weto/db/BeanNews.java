package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for News.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanNews extends DbNews {

    /**
     * Default constructor.
     */
    public BeanNews() {
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
     * Gets the value of the property title.
     * @return Value as java.lang.String.
     */
    public java.lang.String getTitle() {
        return getTitleData().getValue();
    }

    /**
     * Sets the value of the property title.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setTitle( java.lang.String newValue ) throws InvalidValueException {
        getTitleData().setValue( newValue );
    }

    /**
     * Gets the value of the property text.
     * @return Value as java.lang.String.
     */
    public java.lang.String getText() {
        return getTextData().getValue();
    }

    /**
     * Sets the value of the property text.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setText( java.lang.String newValue ) throws InvalidValueException {
        getTextData().setValue( newValue );
    }

    /**
     * Gets the value of the property endDate.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getEndDate() {
        return getEndDateData().getValue();
    }

    /**
     * Sets the value of the property endDate.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setEndDate( java.lang.Integer newValue ) throws InvalidValueException {
        getEndDateData().setValue( newValue );
    }

    /**
     * Gets the value of the property startDate.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getStartDate() {
        return getStartDateData().getValue();
    }

    /**
     * Sets the value of the property startDate.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setStartDate( java.lang.Integer newValue ) throws InvalidValueException {
        getStartDateData().setValue( newValue );
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
