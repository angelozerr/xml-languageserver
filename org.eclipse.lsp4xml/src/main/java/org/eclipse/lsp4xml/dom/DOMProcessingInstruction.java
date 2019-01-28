/**
 *  Copyright (c) 2018 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v20.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.lsp4xml.dom;

import org.w3c.dom.DOMException;

/**
 * A processing instruction node.
 *
 */
public class DOMProcessingInstruction extends DOMCharacterData implements org.w3c.dom.ProcessingInstruction {

	boolean startTagClose;
	String target;
	boolean prolog = false;
	boolean processingInstruction = false;
	int startContent;
	int endContent;
	Integer endTagOpenOffset;

	public DOMProcessingInstruction(int start, int end, DOMDocument ownerDocument) {
		super(start, end, ownerDocument);
	}

	public boolean isProlog() {
		return prolog;
	}

	public boolean isProcessingInstruction() {
		return processingInstruction;
	}

	public int getStartContent() {
		return startContent;
	}

	public int getEndContent() {
		return endContent;
	}

	public Integer getEndTagStart() {
		return endTagOpenOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Node#getNodeType()
	 */
	@Override
	public short getNodeType() {
		return DOMNode.PROCESSING_INSTRUCTION_NODE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Node#getNodeName()
	 */
	@Override
	public String getNodeName() {
		return getTarget();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.ProcessingInstruction#getTarget()
	 */
	@Override
	public String getTarget() {
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.ProcessingInstruction#getData()
	 */
	@Override
	public String getData() {
		return super.getData().trim();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.ProcessingInstruction#setData(java.lang.String)
	 */
	@Override
	public void setData(String data) throws DOMException {
		throw new UnsupportedOperationException();
	}

}
