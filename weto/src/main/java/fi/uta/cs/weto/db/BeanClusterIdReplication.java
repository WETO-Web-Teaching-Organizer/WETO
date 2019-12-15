package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for ClusterIdReplication.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanClusterIdReplication extends DbClusterIdReplication {

    /**
     * Default constructor.
     */
    public BeanClusterIdReplication() {
        super();
    }

    /**
     * Gets the value of the property masterDbClusterId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getMasterDbClusterId() {
        return getMasterDbClusterIdData().getValue();
    }

    /**
     * Sets the value of the property masterDbClusterId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setMasterDbClusterId( java.lang.Integer newValue ) throws InvalidValueException {
        getMasterDbClusterIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property courseDbClusterId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getCourseDbClusterId() {
        return getCourseDbClusterIdData().getValue();
    }

    /**
     * Sets the value of the property courseDbClusterId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setCourseDbClusterId( java.lang.Integer newValue ) throws InvalidValueException {
        getCourseDbClusterIdData().setValue( newValue );
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
