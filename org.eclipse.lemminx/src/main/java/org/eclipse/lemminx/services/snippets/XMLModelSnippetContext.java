/*******************************************************************************
* Copyright (c) 2020 Balduin Landolt and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Balduin Landolt - initial API and implementation
*******************************************************************************/


package org.eclipse.lemminx.services.snippets;

import java.util.Map;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.services.extensions.ICompletionRequest;
import org.eclipse.lemminx.utils.DOMUtils;

public class XMLModelSnippetContext implements IXMLSnippetContext { // FIXME: rename to ProcessingInstructionSnippetContext

	public static IXMLSnippetContext DEFAULT_CONTEXT = new XMLModelSnippetContext();

	@Override
	public boolean isMatch(ICompletionRequest request, Map<String, String> model) {
		DOMNode node = request.getNode();
		int offset = request.getOffset();
		if ((node.isComment() || node.isDoctype() || node.isProlog() || node.isProcessingInstruction()) && offset < node.getEnd()) {
			// completion was triggered inside comment, doctype, prolog or processing instruction
			return false;
		}

 		DOMDocument document = request.getXMLDocument();
		DOMElement documentElement = document.getDocumentElement();

		if (document.isDTD() || DOMUtils.isXSD(document)) {
			// triggered in a DTD or XSD file
			return false;
		}

 		if (document.hasProlog() && offset == 0){
			 // triggered before prolog
			 return false;
		}

		if (documentElement != null && documentElement.getTagName() != null) {
			return offset <= documentElement.getStart();
		}

		return true;
	}
	
}
