/**
 *  Copyright (c) 2018 Angelo ZERR
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v20.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.lsp4xml.extensions.contentmodel.participants;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4xml.commons.BadLocationException;
import org.eclipse.lsp4xml.dom.DOMDocument;
import org.eclipse.lsp4xml.dom.DOMElement;
import org.eclipse.lsp4xml.extensions.contentmodel.model.CMAttributeDeclaration;
import org.eclipse.lsp4xml.extensions.contentmodel.model.CMDocument;
import org.eclipse.lsp4xml.extensions.contentmodel.model.CMElementDeclaration;
import org.eclipse.lsp4xml.extensions.contentmodel.model.ContentModelManager;
import org.eclipse.lsp4xml.extensions.contentmodel.utils.XMLGenerator;
import org.eclipse.lsp4xml.services.AttributeCompletionItem;
import org.eclipse.lsp4xml.services.extensions.CompletionParticipantAdapter;
import org.eclipse.lsp4xml.services.extensions.ICompletionRequest;
import org.eclipse.lsp4xml.services.extensions.ICompletionResponse;
import org.eclipse.lsp4xml.settings.SharedSettings;
import org.eclipse.lsp4xml.uriresolver.CacheResourceDownloadingException;
import org.eclipse.lsp4xml.utils.StringUtils;

/**
 * Extension to support XML completion based on content model (XML Schema
 * completion, etc)
 */
public class ContentModelCompletionParticipant extends CompletionParticipantAdapter {

	@Override
	public void onTagOpen(ICompletionRequest request, ICompletionResponse response) throws Exception {
		try {
			DOMDocument document = request.getXMLDocument();
			String schemaURI;
			String fileURI;
			ContentModelManager contentModelManager = request.getComponent(ContentModelManager.class);
			DOMElement parentElement = request.getParentElement();
			CMDocument cmDocument;
			if (parentElement == null) {
				// XML is empty, in case of XML file associations, a XMl Schema/DTD can be bound
				// check if it's root element (in the case of XML file associations, the link to
				// XML Schema is done with pattern and not with XML root element)
				cmDocument = contentModelManager.findCMDocument(document, null);
				if (cmDocument != null) {
					schemaURI = cmDocument.getURI();
					fillWithChildrenElementDeclaration(null, cmDocument.getElements(), null, false, request, response, schemaURI);
				}
				return;
			}
			// Try to retrieve XML Schema/DTD element declaration for the parent element
			// where completion was triggered.
			cmDocument = contentModelManager.findCMDocument(parentElement, parentElement.getNamespaceURI());
			
			schemaURI = cmDocument != null ? cmDocument.getURI() : null;
			CMElementDeclaration cmElement = contentModelManager.findCMElement(parentElement);
			String defaultPrefix = null;
			
			if (cmElement != null) {
				defaultPrefix = parentElement.getPrefix();
				fillWithChildrenElementDeclaration(parentElement, cmElement.getElements(), defaultPrefix, false,
						request, response, schemaURI);
			}
			if (parentElement.isDocumentElement()) {
				// root document element
				Collection<String> prefixes = parentElement.getAllPrefixes();
				for (String prefix : prefixes) {
					if (defaultPrefix != null && prefix.equals(defaultPrefix)) {
						continue;
					}
					String namespaceURI = parentElement.getNamespaceURI(prefix);
					cmDocument = contentModelManager.findCMDocument(parentElement, namespaceURI);
					if (cmDocument != null) {
						fillWithChildrenElementDeclaration(parentElement, cmDocument.getElements(), prefix, true,
								request, response, cmDocument.getURI());
					}
				}
			}
			// Completion on tag based on internal content model (ex : internal DTD declared
			// in XML)
			CMElementDeclaration cmInternalElement = contentModelManager.findInternalCMElement(parentElement);
			if (cmInternalElement != null) {
				defaultPrefix = parentElement.getPrefix();
				fillWithChildrenElementDeclaration(parentElement, cmInternalElement.getElements(), defaultPrefix, false,
						request, response, schemaURI);
			}
		} catch (CacheResourceDownloadingException e) {
			// XML Schema, DTD is loading, ignore this error
		}
	}

	private void fillWithChildrenElementDeclaration(DOMElement element, Collection<CMElementDeclaration> cmElements,
			String p, boolean forceUseOfPrefix, ICompletionRequest request, ICompletionResponse response, String schemaURI)
			throws BadLocationException {
		XMLGenerator generator = request.getXMLGenerator();
		for (CMElementDeclaration child : cmElements) {
			String prefix = forceUseOfPrefix ? p : (element != null ? element.getPrefix(child.getNamespace()) : null);
			String label = child.getName(prefix);
			CompletionItem item = new CompletionItem(label);
			item.setFilterText(request.getFilterForStartTagName(label));
			item.setKind(CompletionItemKind.Property);
			StringBuilder sb = new StringBuilder();
			String documentation;
			String tempDoc = child.getDocumentation();
			boolean tempDocHasContent = !StringUtils.isEmpty(tempDoc);
			if(tempDocHasContent) {
				sb.append(tempDoc);
			}
			
			if(schemaURI != null) {
				if(tempDocHasContent) {
					String lineSeparator = System.getProperty("line.separator");
					sb.append(lineSeparator);
					sb.append(lineSeparator);
				}
				Path schemaPath = Paths.get(schemaURI);
				sb.append("Source: ");
				sb.append(schemaPath.getFileName().toString());
			}
			documentation = sb.toString();
			if (documentation != null && !documentation.isEmpty()) {
				item.setDetail(documentation);
			}
			String xml = generator.generate(child, prefix);
			item.setTextEdit(new TextEdit(request.getReplaceRange(), xml));
			item.setInsertTextFormat(InsertTextFormat.Snippet);
			response.addCompletionItem(item, true);
		}
	}

	@Override
	public void onAttributeName(boolean generateValue, Range fullRange, ICompletionRequest request,
			ICompletionResponse response, SharedSettings settings) throws Exception {
		// otherwise, manage completion based on XML Schema, DTD.
		DOMElement parentElement = request.getNode().isElement() ? (DOMElement) request.getNode() : null;
		if (parentElement == null) {
			return;
		}
		try {
			boolean canSupportSnippet = request.getCompletionSettings().isCompletionSnippetsSupported();
			ContentModelManager contentModelManager = request.getComponent(ContentModelManager.class);
			// Completion on attribute based on external grammar
			CMElementDeclaration cmElement = contentModelManager.findCMElement(parentElement);
			fillAttributesWithCMAttributeDeclarations(parentElement, fullRange, cmElement, canSupportSnippet,
					generateValue, response, settings);
			// Completion on attribute based on internal grammar
			cmElement = contentModelManager.findInternalCMElement(parentElement);
			fillAttributesWithCMAttributeDeclarations(parentElement, fullRange, cmElement, canSupportSnippet,
					generateValue, response, settings);
		} catch (CacheResourceDownloadingException e) {
			// XML Schema, DTD is loading, ignore this error
		}
	}

	private void fillAttributesWithCMAttributeDeclarations(DOMElement parentElement, Range fullRange,
			CMElementDeclaration cmElement, boolean canSupportSnippet, boolean generateValue,
			ICompletionResponse response, SharedSettings settings) {
		if (cmElement == null) {
			return;
		}
		Collection<CMAttributeDeclaration> attributes = cmElement.getAttributes();
		if (attributes == null) {
			return;
		}
		for (CMAttributeDeclaration cmAttribute : attributes) {
			String attrName = cmAttribute.getName();
			if (!parentElement.hasAttribute(attrName)) {
				CompletionItem item = new AttributeCompletionItem(attrName, canSupportSnippet, fullRange, generateValue,
						cmAttribute.getDefaultValue(), cmAttribute.getEnumerationValues(), settings);
				String documentation = cmAttribute.getDocumentation();
				if (documentation != null) {
					item.setDetail(documentation);
				}
				response.addCompletionAttribute(item);
			}
		}
	}

	@Override
	public void onAttributeValue(String valuePrefix, Range fullRange, boolean addQuotes, ICompletionRequest request,
			ICompletionResponse response, SharedSettings settings) throws Exception {
		DOMElement parentElement = request.getNode().isElement() ? (DOMElement) request.getNode() : null;
		if (parentElement == null) {
			return;
		}
		try {
			ContentModelManager contentModelManager = request.getComponent(ContentModelManager.class);
			// Completion on attribute values based on external grammar
			CMElementDeclaration cmElement = contentModelManager.findCMElement(parentElement);
			fillAttributeValuesWithCMAttributeDeclarations(cmElement, request, response);
			// Completion on attribute values based on internal grammar
			cmElement = contentModelManager.findInternalCMElement(parentElement);
			fillAttributeValuesWithCMAttributeDeclarations(cmElement, request, response);
		} catch (CacheResourceDownloadingException e) {
			// XML Schema, DTD is loading, ignore this error
		}
	}

	private void fillAttributeValuesWithCMAttributeDeclarations(CMElementDeclaration cmElement,
			ICompletionRequest request, ICompletionResponse response) {
		if (cmElement != null) {
			String attributeName = request.getCurrentAttributeName();
			CMAttributeDeclaration cmAttribute = cmElement.findCMAttribute(attributeName);
			if (cmAttribute != null) {
				cmAttribute.getEnumerationValues().forEach(value -> {
					CompletionItem item = new CompletionItem();
					item.setLabel(value);
					item.setKind(CompletionItemKind.Value);
					response.addCompletionAttribute(item);
				});
			}
		}
	}

}
