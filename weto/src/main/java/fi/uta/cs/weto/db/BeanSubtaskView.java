package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for SubtaskView.
 * This class extends the database access class by adding bean property getters.
 */
public class BeanSubtaskView extends DbSubtaskView {

    /**
     * Default constructor.
     */
    public BeanSubtaskView() {
        super();
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
     * Gets the value of the property componentBits.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getComponentBits() {
        return getComponentBitsData().getValue();
    }
    /**
     * Sets the value of the property componentBits.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setComponentBits( java.lang.Integer newValue ) throws InvalidValueException {
        getComponentBitsData().setValue( newValue );
    }

    /**
     * Gets the value of the property showTextInParent.
     * @return Value as java.lang.Boolean.
     */
    public java.lang.Boolean getShowTextInParent() {
        return getShowTextInParentData().getValue();
    }
    /**
     * Sets the value of the property showTextInParent.
     * @param newValue New value as java.lang.Boolean.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setShowTextInParent( java.lang.Boolean newValue ) throws InvalidValueException {
        getShowTextInParentData().setValue( newValue );
    }

    /**
     * Gets the value of the property rootTaskId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getRootTaskId() {
        return getRootTaskIdData().getValue();
    }
    /**
     * Sets the value of the property rootTaskId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setRootTaskId( java.lang.Integer newValue ) throws InvalidValueException {
        getRootTaskIdData().setValue( newValue );
    }
}

// End of file.
