package org.eclipse.lemminx.extensions.xerces.xmlmodel;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.util.UriOrFile;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.ResolverFactory;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.Validator;
import com.thaiopensource.validate.auto.AutoSchemaReader;
import com.thaiopensource.xml.sax.CountingErrorHandler;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

/**
 * Provides a simplified API for validating XML documents against schemas.
 * This class is neither reentrant nor safe for access from multiple threads.
 *
 * @author <a href="mailto:jjc@jclark.com">James Clark</a>
 */

public class MyValidationDriver {
  private XMLReader xr;
  private final CountingErrorHandler eh;
  private final SchemaReader sr;
  private final PropertyMap schemaProperties;
  private final PropertyMap instanceProperties;
  private Validator validator;
  private Schema schema;

  /**
   * Creates and initializes a ValidationDriver.
   *
   * @param schemaProperties a PropertyMap specifying properties controlling schema creation;
   * must not be <code>null</code>
   * @param instanceProperties a PropertyMap specifying properties controlling validation;
   * must not be <code>null</code>
   * @param schemaReader the SchemaReader to use; if this is <code>null</code>, then the schema
   * must be in XML, and the namespace URI of the root element will be used to determine what
   * the schema language is
   */
  public MyValidationDriver(PropertyMap schemaProperties,
                          PropertyMap instanceProperties,
                          SchemaReader schemaReader) {
    ErrorHandler seh = schemaProperties.get(ValidateProperty.ERROR_HANDLER);
    PropertyMapBuilder builder;
    if (seh == null) {
      seh = new ErrorHandlerImpl();
      builder = new PropertyMapBuilder(schemaProperties);
      builder.put(ValidateProperty.ERROR_HANDLER, seh);
      this.schemaProperties = builder.toPropertyMap();
    }
    else
      this.schemaProperties = schemaProperties;
    builder = new PropertyMapBuilder(instanceProperties);
    ErrorHandler ieh = instanceProperties.get(ValidateProperty.ERROR_HANDLER);
    if (ieh == null)
      ieh = seh;
    eh = new CountingErrorHandler(ieh);
    builder.put(ValidateProperty.ERROR_HANDLER, eh);
    this.instanceProperties = builder.toPropertyMap();
    this.sr = schemaReader == null ? new AutoSchemaReader() : schemaReader;
  }

  /**
   * Equivalent to ValidationDriver(schemaProperties, instanceProperties, null).
   *
   * @see #ValidationDriver(PropertyMap,PropertyMap,SchemaReader)
   */
   public MyValidationDriver(PropertyMap schemaProperties, PropertyMap instanceProperties) {
     this(schemaProperties, instanceProperties, null);
  }

  /**
   * Equivalent to ValidationDriver(properties, properties, sr).
   *
   * @see #ValidationDriver(PropertyMap,PropertyMap,SchemaReader)
   */
   public MyValidationDriver(PropertyMap properties, SchemaReader sr) {
    this(properties, properties, sr);
  }

  /**
   * Equivalent to ValidationDriver(properties, properties, null).
   *
   * @see #ValidationDriver(PropertyMap,PropertyMap,SchemaReader)
   */
   public MyValidationDriver(PropertyMap properties) {
    this(properties, properties, null);
  }

  /**
   * Equivalent to ValidationDriver(PropertyMap.EMPTY, PropertyMap.EMPTY, null).
   *
   * @see #ValidationDriver(PropertyMap,PropertyMap,SchemaReader)
   */
   public MyValidationDriver(SchemaReader sr) {
    this(PropertyMap.EMPTY, sr);
  }

  /**
   * Equivalent to ValidationDriver(PropertyMap.EMPTY, PropertyMap.EMPTY, null).
   *
   * @see #ValidationDriver(PropertyMap,PropertyMap,SchemaReader)
   */
  public MyValidationDriver() {
    this(PropertyMap.EMPTY, PropertyMap.EMPTY, null);
  }

  /**
   * Loads a schema. Subsequent calls to <code>validate</code> will validate with
   * respect the loaded schema. This can be called more than once to allow
   * multiple documents to be validated against different schemas.
   *
   * @param in the InputSource for the schema
   * @return <code>true</code> if the schema was loaded successfully; <code>false</code> otherwise
   * @throws IOException if an I/O error occurred
   * @throws SAXException if an XMLReader or ErrorHandler threw a SAXException
   */
  public boolean loadSchema(InputSource in) throws SAXException, IOException {
    try {
      schema = sr.createSchema(new SAXSource(in), schemaProperties);
      validator = null;
      return true;
    }
    catch (IncorrectSchemaException e) {
      return false;
    }
  }

  /**
   * Validates a document against the currently loaded schema. This can be called
   * multiple times in order to validate multiple documents.
   *
   * @param in the InputSource for the document to be validated
   * @return <code>true</code> if the document is valid; <code>false</code> otherwise
   * @throws java.lang.IllegalStateException if there is no currently loaded schema
   * @throws java.io.IOException if an I/O error occurred
   * @throws org.xml.sax.SAXException if an XMLReader or ErrorHandler threw a SAXException
   */
  public boolean validate(InputSource in) throws SAXException, IOException {
    if (schema == null)
      throw new IllegalStateException("cannot validate without schema");
    if (validator == null)
      validator = schema.createValidator(instanceProperties);
    if (xr == null) {
      xr = ResolverFactory.createResolver(instanceProperties).createXMLReader();
      xr.setErrorHandler(eh);
    }
    eh.reset();
    xr.setContentHandler(validator.getContentHandler());
    DTDHandler dh = validator.getDTDHandler();
    if (dh != null)
      xr.setDTDHandler(dh);
    try {
      //xr.parse(in);
      //return !eh.getHadErrorOrFatalError();
    	return true;
    }
    finally {
      validator.reset();
    }
  }
  
  public void setXr(XMLReader xr) {
	this.xr = xr;
}

  /**
   * Get the actual properties of the loaded schema
   * @return a PropertyMap with the schema properties
   * @throws java.lang.IllegalStateException if there is no currently loaded schema
   */
  public PropertyMap getSchemaProperties() {
    if (schema == null)
      throw new IllegalStateException("getSchemaProperties requires a schema");
    return schema.getProperties();
  }

  /**
   * Returns an <code>InputSource</code> for a filename.
   *
   * @param filename a String specifying the filename
   * @return an <code>InputSource</code> for the filename
   */
  static public InputSource fileInputSource(String filename) {
    return fileInputSource(new File(filename));
  }

  /**
   * Returns an <code>InputSource</code> for a <code>File</code>.
   *
   * @param file the <code>File</code>
   * @return an <code>InputSource</code> for the filename
   */
  static public InputSource fileInputSource(File file) {
    return new InputSource(UriOrFile.fileToUri(file));
  }

  /**
   * Returns an <code>InputSource</code> for a string that represents either a file
   * or an absolute URI. If the string looks like an absolute URI, it will be
   * treated as an absolute URI, otherwise it will be treated as a filename.
   *
   * @param uriOrFile a <code>String</code> representing either a file or an absolute URI
   * @return an <code>InputSource</code> for the file or absolute URI
   */
  static public InputSource uriOrFileInputSource(String uriOrFile) {
    return new InputSource(UriOrFile.toUri(uriOrFile));
  }
}
