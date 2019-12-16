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
package org.eclipse.lsp4xml.extensions.contentmodel.uriresolver;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.util.URI.MalformedURIException;
import org.eclipse.lsp4xml.extensions.contentmodel.settings.XMLFileAssociation;
import org.eclipse.lsp4xml.uriresolver.IExternalSchemaLocationProvider;
import org.eclipse.lsp4xml.uriresolver.URIResolverExtension;

/**
 * XML file association URI resolver.
 *
 */
public class XMLFileAssociationResolverExtension implements URIResolverExtension, IExternalSchemaLocationProvider {

	private static Logger LOGGER = Logger.getLogger(XMLFileAssociationResolverExtension.class.getName());

	private String rootUri;

	private XMLFileAssociation[] fileAssociations;

	/**
	 * 
	 * @param fileAssociations
	 * @return true if file associations changed and false otherwise
	 */
	public boolean setFileAssociations(XMLFileAssociation[] fileAssociations) {
		XMLFileAssociation[] oldFileAssociations = this.fileAssociations;
		this.fileAssociations = fileAssociations;
		expandSystemId();
		return !Arrays.equals(oldFileAssociations, fileAssociations);
	}

	@Override
	public String resolve(String baseLocation, String publicId, String systemId) {
		if (systemId != null) {
			// system id is defined in the XML root element (ex : systemId=Types.xsd for
			// <Types
			// xsi:noNamespaceSchemaLocation="Types.xsd">
			// ignore XML file association
			return null;
		}
		if (fileAssociations != null) {
			for (XMLFileAssociation fileAssociation : fileAssociations) {
				if (fileAssociation.matches(baseLocation)) {
					return fileAssociation.getSystemId();
				}
			}
		}
		return null;
	}

	@Override
	public Map<String, String> getExternalSchemaLocation(URI fileURI) {
		if (fileAssociations != null) {
			for (XMLFileAssociation fileAssociation : fileAssociations) {
				if (fileAssociation.matches(fileURI)) {
					return fileAssociation.getExternalSchemaLocation();
				}
			}
		}
		return null;
	}

	/**
	 * Set the root URI
	 * 
	 * @param rootUri the root URI
	 */
	public void setRootUri(String rootUri) {
		this.rootUri = rootUri;
	}

	private void expandSystemId() {
		if (fileAssociations != null) {
			for (XMLFileAssociation fileAssociation : fileAssociations) {
				// Expand original system id, if rootUri is null then it uses just the systemId.
				try {
					String expandSystemId = XMLEntityManager.expandSystemId(fileAssociation.getSystemId(), rootUri,
							false);
					if (expandSystemId != null) {
						fileAssociation.setSystemId(expandSystemId);
					}
				} catch (MalformedURIException e) {
					LOGGER.log(Level.WARNING, "Issue expanding the provided systemId from the file associations.", e);
				}
			}
		}

	}
}
