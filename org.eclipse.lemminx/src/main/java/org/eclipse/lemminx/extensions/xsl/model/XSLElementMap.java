/*******************************************************************************
* Copyright (c) 2020 Łukasz Kwiatkowski
* All rights reserved.
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*
* @author Lukasz Kwiatkowski <woocash@eikberg.com> - initial API and implementation
*******************************************************************************/

package org.eclipse.lemminx.extensions.xsl.model;


import java.util.*;

/**
 * XSL element model.
 */
public class XSLElementMap extends HashMap<String,XSLElement> implements IXSLElementCollection{

	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(XSLElement element){
		if(!element.hasAttribute("name")){
			return false;
		}
		return putIfAbsent(element.getAttribute("name"), element) == null;
	}
	
}