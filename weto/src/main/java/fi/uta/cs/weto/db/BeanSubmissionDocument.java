package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for SubmissionDocument.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanSubmissionDocument extends DbSubmissionDocument {

    /**
     * Default constructor.
     */
    public BeanSubmissionDocument() {
        super();
    }

    /**
     * Gets the value of the property submissionId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getSubmissionId() {
        return getSubmissionIdData().getValue();
    }

    /**
     * Sets the value of the property submissionId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setSubmissionId( java.lang.Integer newValue ) throws InvalidValueException {
        getSubmissionIdData().setValue( newValue );
    }

    /**
     * Gets the value of the property documentId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getDocumentId() {
        return getDocumentIdData().getValue();
    }

    /**
     * Sets the value of the property documentId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setDocumentId( java.lang.Integer newValue ) throws InvalidValueException {
        getDocumentIdData().setValue( newValue );
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
