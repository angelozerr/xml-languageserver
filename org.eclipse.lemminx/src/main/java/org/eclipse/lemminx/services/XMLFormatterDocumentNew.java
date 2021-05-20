package org.eclipse.lemminx.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.parser.Scanner;
import org.eclipse.lemminx.dom.parser.TokenType;
import org.eclipse.lemminx.dom.parser.XMLScanner;
import org.eclipse.lemminx.services.extensions.format.IFormatterParticipant;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lemminx.settings.XMLFormattingOptions.EmptyElements;
import org.eclipse.lemminx.utils.DOMUtils;
import org.eclipse.lemminx.utils.XMLBuilder;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;

class XMLFormatterDocumentNew {
	private final TextDocument textDocument;
	private final Range range;
	private final SharedSettings sharedSettings;
	private final Collection<IFormatterParticipant> formatterParticipants;
	private final EmptyElements emptyElements;

	private int startOffset;
	private int endOffset;
	private DOMDocument fullDomDocument;
	private DOMDocument rangeDomDocument;
	private XMLBuilder xmlBuilder;
	private int indentLevel;
	private boolean linefeedOnNextWrite;
	private boolean withinDTDContent;
	private CancelChecker monitor;

	/**
	 * XML formatter document.
	 */
	public XMLFormatterDocumentNew(TextDocument textDocument, Range range, SharedSettings sharedSettings,
			Collection<IFormatterParticipant> formatterParticipants) {
		this.textDocument = textDocument;
		this.range = range;
		this.sharedSettings = sharedSettings;
		this.formatterParticipants = formatterParticipants;
		this.emptyElements = sharedSettings.getFormattingSettings().getEmptyElements();
		this.linefeedOnNextWrite = false;
	}

	/**
	 * Returns a List containing a single TextEdit, containing the newly formatted
	 * changes of this.textDocument
	 * 
	 * @return List containing a single TextEdit
	 * @throws BadLocationException
	 */
	public List<? extends TextEdit> format() throws BadLocationException {
		List<TextEdit> edits = new ArrayList<>();
		boolean isDTD = DOMUtils.isDTD(textDocument.getUri());
		String text = textDocument.getText();
		Scanner scanner = XMLScanner.createScanner(text, 0, isDTD);

		int indentLevel = 0;
		TokenType token = scanner.scan();
		while (token != TokenType.EOS) {
			if (monitor != null) {
				monitor.checkCanceled();
			}

			switch (token) {
			case StartTagOpen: {
				Position p = textDocument.positionAt(scanner.getTokenOffset());				
				TextEdit edit = new TextEdit(new Range(p, p), (indentLevel > 0 ? "\r" : "") + getIndentation(indentLevel));
				edits.add(edit);
				indentLevel++;
				break;
			}
			case EndTag: {
				indentLevel--;
			}
			}
			token = scanner.scan();
		}
		return edits;
	}
	
	public String getIndentation(int level) {
		StringBuilder indent = new StringBuilder();
		for (int i = 0; i < level; i++) {
			/*if (isInsertSpaces()) {
				for (int j = 0; j < getTabSize(); j++) {
					appendSpace();
				}
			} else {
				append("\t");
			}*/
			indent.append('\t');
		}
		return indent.toString();
	}

}