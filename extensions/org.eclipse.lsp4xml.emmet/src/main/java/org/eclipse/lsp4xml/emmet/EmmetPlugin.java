package org.eclipse.lsp4xml.emmet;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4xml.emmet.participants.EmmetCompletionParticipant;
import org.eclipse.lsp4xml.services.extensions.ICompletionParticipant;
import org.eclipse.lsp4xml.services.extensions.IXMLExtension;
import org.eclipse.lsp4xml.services.extensions.XMLExtensionsRegistry;

public class EmmetPlugin implements IXMLExtension {

	private final ICompletionParticipant completionParticipant;

	public EmmetPlugin() {
		completionParticipant = new EmmetCompletionParticipant();
	}

	@Override
	public void didChangeConfiguration(DidChangeConfigurationParams params) {
	}

	@Override
	public void start(XMLExtensionsRegistry registry) {
		registry.registerCompletionParticipant(completionParticipant);
	}

	@Override
	public void stop(XMLExtensionsRegistry registry) {
		registry.unregisterCompletionParticipant(completionParticipant);
	}
}
