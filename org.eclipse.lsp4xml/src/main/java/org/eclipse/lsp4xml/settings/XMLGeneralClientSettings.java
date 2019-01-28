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
package org.eclipse.lsp4xml.settings;

import org.eclipse.lsp4xml.services.extensions.CompletionSettings;
import org.eclipse.lsp4xml.utils.JSONUtility;

/**
 * Class to hold all settings from the client side.
 * 
 * See https://github.com/angelozerr/lsp4xml/wiki/Configuration for more information.
 * 
 * This class is created through the deseralization of a JSON object.
 * Each internal setting must be represented by a class and have:
 * 
 * 1) A constructor with no parameters
 * 
 * 2) The JSON key/parent for the settings must have the same
 *    name as a varible.
 * 
 *    eg: {"format" : {...}, "completion" : {...}}
 * 
 * 		In this class must exist both a "format" and "completion" variable
 * 		with the appropriate Class to represent the value of each key
 *
 */
public class XMLGeneralClientSettings {

	private LogsSettings logs;

	private XMLFormattingOptions format;
	
	private XMLExperimentalCapabilities experimental;

	private CompletionSettings completion;

	public void setLogs(LogsSettings logs) {
		this.logs = logs;
	}

	public LogsSettings getLogs() {
		return logs;
	}

	public void setFormat(XMLFormattingOptions format) {
		this.format = format;
	}

	public XMLFormattingOptions getFormat() {
		return format;
	}
	
	public XMLExperimentalCapabilities getExperimental() {
		return experimental;
	}

	/**
	 * Set completion settings
	 * @param completion
	 */
	public void setCompletion(CompletionSettings completion) {
		this.completion = completion;
	}

	/**
	 * Get completion settings
	 * @param completion
	 */
	public CompletionSettings getCompletion() {
		return completion;
	}

	public static XMLGeneralClientSettings getGeneralXMLSettings(Object initializationOptionsSettings) {
		return JSONUtility.toModel(initializationOptionsSettings, XMLGeneralClientSettings.class);
	}	
}