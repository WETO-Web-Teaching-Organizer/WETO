package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for Tag.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanTag extends DbTag {

    /**
     * Default constructor.
     */
    public BeanTag() {
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
     * Gets the value of the property authorId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getAuthorId() {
        return getAuthorIdData().getValue();
    }

    /**
     * Sets the value of the property authorId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setAuthorId( java.lang.Integer newValue ) throws InvalidValueException {
        getAuthorIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property status.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getStatus() {
        return getStatusData().getValue();
    }

    /**
     * Sets the value of the property status.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setStatus( java.lang.Integer newValue ) throws InvalidValueException {
        getStatusData().setValue( newValue );
    }

    /**
     * Gets the value of the property rank.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getRank() {
        return getRankData().getValue();
    }

    /**
     * Sets the value of the property rank.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setRank( java.lang.Integer newValue ) throws InvalidValueException {
        getRankData().setValue( newValue );
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
     * Gets the value of the property taggedId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getTaggedId() {
        return getTaggedIdData().getValue();
    }

    /**
     * Sets the value of the property taggedId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setTaggedId( java.lang.Integer newValue ) throws InvalidValueException {
        getTaggedIdData().setValue( newValue );
    }
}

// End of file.
