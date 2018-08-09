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
package org.eclipse.lsp4xml;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.WorkspaceSymbolParams;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.eclipse.lsp4xml.services.extensions.IXMLExtension;

/**
 * XML workspace service.
 *
 */
public class XMLWorkspaceService implements WorkspaceService {

	private final XMLLanguageServer xmlLanguageServer;

	public XMLWorkspaceService(XMLLanguageServer xmlLanguageServer) {
		this.xmlLanguageServer = xmlLanguageServer;
	}

	@Override
	public CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams params) {
		return null;
	}

	@Override
	public void didChangeConfiguration(DidChangeConfigurationParams params) {
		for (IXMLExtension extension : xmlLanguageServer.getXMLLanguageService().getExtensions()) {
			extension.didChangeConfiguration(params);
		}
	}

	@Override
	public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {

	}

}
