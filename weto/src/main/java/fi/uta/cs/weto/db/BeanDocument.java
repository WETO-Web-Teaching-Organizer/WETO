package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;

/**
 * Generated bean class for Document.
 * This class extends the database access class by adding bean property getters and setters.
 */
public class BeanDocument extends DbDocument {

    /**
     * Default constructor.
     */
    public BeanDocument() {
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
     * Gets the value of the property fileName.
     * @return Value as java.lang.String.
     */
    public java.lang.String getFileName() {
        return getFileNameData().getValue();
    }

    /**
     * Sets the value of the property fileName.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setFileName( java.lang.String newValue ) throws InvalidValueException {
        getFileNameData().setValue( newValue );
    }

    /**
     * Gets the value of the property fileSize.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getFileSize() {
        return getFileSizeData().getValue();
    }

    /**
     * Sets the value of the property fileSize.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setFileSize( java.lang.Integer newValue ) throws InvalidValueException {
        getFileSizeData().setValue( newValue );
    }

    /**
     * Gets the value of the property mimeType.
     * @return Value as java.lang.String.
     */
    public java.lang.String getMimeType() {
        return getMimeTypeData().getValue();
    }

    /**
     * Sets the value of the property mimeType.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setMimeType( java.lang.String newValue ) throws InvalidValueException {
        getMimeTypeData().setValue( newValue );
    }

    /**
     * Gets the value of the property contentFileSize.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getContentFileSize() {
        return getContentFileSizeData().getValue();
    }

    /**
     * Sets the value of the property contentFileSize.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setContentFileSize( java.lang.Integer newValue ) throws InvalidValueException {
        getContentFileSizeData().setValue( newValue );
    }

    /**
     * Gets the value of the property contentMimeType.
     * @return Value as java.lang.String.
     */
    public java.lang.String getContentMimeType() {
        return getContentMimeTypeData().getValue();
    }

    /**
     * Sets the value of the property contentMimeType.
     * @param newValue New value as java.lang.String.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setContentMimeType( java.lang.String newValue ) throws InvalidValueException {
        getContentMimeTypeData().setValue( newValue );
    }

    /**
     * Gets the value of the property contentId.
     * @return Value as java.lang.Integer.
     */
    public java.lang.Integer getContentId() {
        return getContentIdData().getValue();
    }

    /**
     * Sets the value of the property contentId.
     * @param newValue New value as java.lang.Integer.
     * @throws InvalidValueException if the new value is not valid.
     */
    public void setContentId( java.lang.Integer newValue ) throws InvalidValueException {
        getContentIdData().setValue( newValue );
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
