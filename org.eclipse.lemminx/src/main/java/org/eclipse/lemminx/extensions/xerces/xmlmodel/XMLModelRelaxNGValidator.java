package org.eclipse.lemminx.extensions.xerces.xmlmodel;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;

public class XMLModelRelaxNGValidator implements XMLModelValidator {

	private XMLErrorReporter errorReporter;

	private XMLLocator locator;

	private String href;

	private boolean processed;

	private XMLEntityResolver entityResolver;

	private XMLReader xmlReader;

	public static final String ERROR_REPORTER = Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;

	protected static final String ENTITY_RESOLVER = Constants.XERCES_PROPERTY_PREFIX
			+ Constants.ENTITY_RESOLVER_PROPERTY;

	@Override
	public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
		// Get error reporter.
		try {
			errorReporter = (XMLErrorReporter) componentManager.getProperty(ERROR_REPORTER);
		} catch (XMLConfigurationException e) {
			errorReporter = null;
		}
		// Get error reporter.
		try {
			entityResolver = (XMLEntityResolver) componentManager.getProperty(ENTITY_RESOLVER);
		} catch (XMLConfigurationException e) {
			entityResolver = null;
		}
	}

	@Override
	public String[] getRecognizedFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] getRecognizedProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
		// TODO Auto-generated method stub

	}

	@Override
	public Boolean getFeatureDefault(String featureId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPropertyDefault(String propertyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext,
			Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs)
			throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void comment(XMLString text, Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
		if (!processed) {
			PropertyMapBuilder mapBuilder = new PropertyMapBuilder();
			mapBuilder.put(ValidateProperty.ERROR_HANDLER, errorReporter.getSAXErrorHandler());
			PropertyMap propertyMap = mapBuilder.toPropertyMap();
			MyValidationDriver driver = new MyValidationDriver(propertyMap);

			try {
				String expandedLoc = XMLEntityManager.expandSystemId(href, locator.getExpandedSystemId(), false);
				driver.loadSchema(new InputSource(expandedLoc));
				driver.setXr(xmlReader);
				driver.validate(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			processed = true;
		}
	}

	@Override
	public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs)
			throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void characters(XMLString text, Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endElement(QName element, Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startCDATA(Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endCDATA(Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDocument(Augmentations augs) throws XNIException {

	}

	@Override
	public void setDocumentSource(XMLDocumentSource source) {
		// TODO Auto-generated method stub

	}

	@Override
	public XMLDocumentSource getDocumentSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDocumentHandler(XMLDocumentHandler handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public XMLDocumentHandler getDocumentHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocator(XMLLocator locator) {
		this.locator = locator;
	}

	@Override
	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public void setXMLReader(XMLReader xmlReader) {
		this.xmlReader = xmlReader;
	}
}
