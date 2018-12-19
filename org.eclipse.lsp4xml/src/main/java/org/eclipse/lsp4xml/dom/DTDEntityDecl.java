/**
 *  Copyright (c) 2018 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.lsp4xml.dom;

import org.w3c.dom.Entity;
import org.w3c.dom.Node;

/**
 * DOM Entity declaration <!ENTITY
 * 
 * @see https://www.w3.org/TR/REC-xml/#dt-entdecl
 */
public class DTDEntityDecl extends DTDDeclNode implements Entity {


	/**
	* Formats:
	* 
	* <!ENTITY entity-name "entity-value">
	* 
	* or
	* 
	* <!ENTITY % entity-name "entity-value">
	* 
	* or
	* 
	* <!ENTITY % entity-name SYSTEM "systemId">
	* 
	* or
	* 
	* <!ENTITY % entity-name PUBLIC "publicId" "systemId">
	* 
	* or
	* 
	* <!ENTITY % entity-name SYSTEM "systemId" NDATA name>
	* 
	* or
	* 
	* <!ENTITY % entity-name PUBLIC "publicId" "systemId" NDATA name>
	*/
	DTDDeclParameter percent;
	DTDDeclParameter name;
	DTDDeclParameter value;
	DTDDeclParameter kind;
	DTDDeclParameter publicId;
	DTDDeclParameter systemId;

	
	public DTDEntityDecl(int start, int end, DOMDocumentType parentDocumentType) {
		super(start, end, parentDocumentType);
		declType = new DTDDeclParameter(parentDocumentType, start + 2, start + 8);
	}

	public String getPercent() {
		return percent != null ? percent.getParameter() : null;
	}

	public void setPercent(int start, int end) {
		percent = addNewParameter(start, end);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Node#getNodeName()
	 */
	@Override
	public String getNodeName() {
		return name != null ? name.getParameter() : null;
	}

	public void setNodeName(int start, int end) {
		name = addNewParameter(start, end);
	}

	public String getValue() {
		return value != null ? value.getParameter() : null;
	}

	public void setValue(int start, int end) {
		value = addNewParameter(start, end);
	}

	public String getKind() {
		return kind != null ? kind.getParameter() : null;
	}

	public void setKind(int start, int end) {
		kind = addNewParameter(start, end);
	}

	@Override
	public short getNodeType() {
		return Node.ENTITY_NODE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Entity#getInputEncoding()
	 */
	@Override
	public String getInputEncoding() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Entity#getNotationName()
	 */
	@Override
	public String getNotationName() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Entity#getPublicId()
	 */
	@Override
	public String getPublicId() {
		return publicId != null ? publicId.getParameter() : null;
	}

	public void setPublicId(int start, int end) {
		publicId = addNewParameter(start, end);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Entity#getSystemId()
	 */
	@Override
	public String getSystemId() {
		return systemId != null ? systemId.getParameter() : null;
	}

	public void setSystemId(int start, int end) {
		systemId = addNewParameter(start, end);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Entity#getXmlEncoding()
	 */
	@Override
	public String getXmlEncoding() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Entity#getXmlVersion()
	 */
	@Override
	public String getXmlVersion() {
		throw new UnsupportedOperationException();
	}
}
