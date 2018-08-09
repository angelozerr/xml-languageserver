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
package org.eclipse.lsp4xml.services.extensions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

/**
 * XML extensions registry.
 *
 */
public class XMLExtensionsRegistry {

	private final Collection<IXMLExtension> extensions;
	private final List<ICompletionParticipant> completionParticipants;
	private final List<IHoverParticipant> hoverParticipants;
	private final List<IDiagnosticsParticipant> diagnosticsParticipants;

	private boolean initialized;

	public XMLExtensionsRegistry() {
		extensions = new ArrayList<>();
		completionParticipants = new ArrayList<>();
		hoverParticipants = new ArrayList<>();
		diagnosticsParticipants = new ArrayList<>();
	}

	public Collection<IXMLExtension> getExtensions() {
		initializeIfNeeded();
		return extensions;
	}

	public Collection<ICompletionParticipant> getCompletionParticipants() {
		initializeIfNeeded();
		return completionParticipants;
	}

	public Collection<IHoverParticipant> getHoverParticipants() {
		initializeIfNeeded();
		return hoverParticipants;
	}

	public Collection<IDiagnosticsParticipant> getDiagnosticsParticipants() {
		initializeIfNeeded();
		return diagnosticsParticipants;
	}

	private void initializeIfNeeded() {
		if (initialized) {
			return;
		}
		initialize();
	}

	private synchronized void initialize() {
		if (initialized) {
			return;
		}
		ServiceLoader<IXMLExtension> extensions = ServiceLoader.load(IXMLExtension.class);
		extensions.forEach(extension -> {
			registerExtension(extension);
		});
		initialized = true;
	}

	void registerExtension(IXMLExtension extension) {
		extensions.add(extension);
		extension.start(this);
	}

	void unregisterExtension(IXMLExtension extension) {
		extensions.remove(extension);
		extension.stop(this);
	}

	public void registerCompletionParticipant(ICompletionParticipant completionParticipant) {
		completionParticipants.add(completionParticipant);
	}

	public void unregisterCompletionParticipant(ICompletionParticipant completionParticipant) {
		completionParticipants.add(completionParticipant);
	}

	public void registerHoverParticipant(IHoverParticipant hoverParticipant) {
		hoverParticipants.add(hoverParticipant);
	}

	public void unregisterHoverParticipant(IHoverParticipant hoverParticipant) {
		hoverParticipants.add(hoverParticipant);
	}

	public void registerDiagnosticsParticipant(IDiagnosticsParticipant diagnosticsParticipant) {
		diagnosticsParticipants.add(diagnosticsParticipant);
	}

	public void unregisterDiagnosticsParticipant(IDiagnosticsParticipant diagnosticsParticipant) {
		diagnosticsParticipants.add(diagnosticsParticipant);
	}
}
