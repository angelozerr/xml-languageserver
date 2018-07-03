/**
 *  Copyright (c) 2018 Angelo ZERR
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.xml.languageserver.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * XML diagnostics support.
 *
 */
class XMLDiagnostics {

	private static final String XML_DIAGNOSTIC_SOURCE = "xml";

	private final XMLExtensionsRegistry extensionsRegistry;

	public XMLDiagnostics(XMLExtensionsRegistry extensionsRegistry) {
		this.extensionsRegistry = extensionsRegistry;
	}

	public List<Diagnostic> validateXML(String xmlDocumentUri, String xmlDocumentContent) {
		List<Diagnostic> diagnostics = new ArrayList<Diagnostic>();

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();

			String xmlSchemaFile = null;
			if (xmlSchemaFile == null) {
				parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
						"http://www.w3.org/2001/XMLSchema");
			}

			XMLReader reader = parser.getXMLReader();
			//reader.setProperty("http://apache.org/xml/properties/locale", Locale.ENGLISH);

			// Error handler
			reader.setErrorHandler(new ErrorHandler() {

				@Override
				public void warning(SAXParseException e) throws SAXException {
					Position start = new Position(e.getLineNumber() - 1, e.getColumnNumber() - 1);
					Position end = new Position(e.getLineNumber() - 1, e.getColumnNumber() - 1);
					String message = e.getLocalizedMessage();
					diagnostics.add(new Diagnostic(new Range(start, end), message, DiagnosticSeverity.Warning,
							XML_DIAGNOSTIC_SOURCE));
				}

				@Override
				public void error(SAXParseException e) throws SAXException {
					Position start = new Position(e.getLineNumber() - 1, e.getColumnNumber() - 1);
					Position end = new Position(e.getLineNumber() - 1, e.getColumnNumber() - 1);
					String message = e.getLocalizedMessage();
					diagnostics.add(new Diagnostic(new Range(start, end), message, DiagnosticSeverity.Error,
							XML_DIAGNOSTIC_SOURCE));
				}

				@Override
				public void fatalError(SAXParseException e) throws SAXException {
					Position start = new Position(e.getLineNumber() - 1, e.getColumnNumber() - 1);
					Position end = new Position(e.getLineNumber() - 1, e.getColumnNumber() - 1);
					String message = e.getLocalizedMessage();
					diagnostics.add(new Diagnostic(new Range(start, end), message, DiagnosticSeverity.Error,
							XML_DIAGNOSTIC_SOURCE));
				}

			});

			// Parse XML
			InputSource inputSource = new InputSource();
			inputSource.setByteStream(
					new ByteArrayInputStream(xmlDocumentContent.getBytes(StandardCharsets.UTF_8.name())));
			inputSource.setSystemId(xmlDocumentUri);
			reader.parse(inputSource);

		} catch (IOException | ParserConfigurationException | SAXException exception) {
		}
		return diagnostics;
	}
}
