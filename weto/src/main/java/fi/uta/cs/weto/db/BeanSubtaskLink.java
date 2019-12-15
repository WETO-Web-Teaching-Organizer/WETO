package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for SubtaskLink.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanSubtaskLink extends DbSubtaskLink {

    /**
     * Default constructor.
     */
    public BeanSubtaskLink() {
        super();
    }

    /**
     * Gets the value of the property containerId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getContainerId() {
        return getContainerIdData().getValue();
    }

    /**
     * Sets the value of the property containerId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setContainerId( java.lang.Integer newValue ) throws InvalidValueException {
        getContainerIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property subtaskId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getSubtaskId() {
        return getSubtaskIdData().getValue();
    }

    /**
     * Sets the value of the property subtaskId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setSubtaskId( java.lang.Integer newValue ) throws InvalidValueException {
        getSubtaskIdData().setValue( newValue );
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
}

// End of file.
