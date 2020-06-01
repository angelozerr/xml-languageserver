/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lemminx.client;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.lemminx.customservice.ActionableNotification;
import org.eclipse.lemminx.customservice.XMLLanguageClientAPI;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;


/**
 * LimitExceededWarnings sends a warning to the
 * client via a jsonrpc notification
 */
public class LimitExceededWarnings {

	private final XMLLanguageClientAPI client;
	private final SharedSettings sharedSettings;
	private final Set<String /* file URI */> cache;

	public LimitExceededWarnings(XMLLanguageClientAPI client, SharedSettings sharedSettings) {
		this.client = client;
		this.sharedSettings = sharedSettings;
		this.cache = new HashSet<String>();
	}

	/**
	 * Sends the limit exceeded warning to the client only if the provided
	 * uri does not exist in the cache
	 * 
	 * After sending the warning, the uri is added to the cache
	 * 
	 * @param uri            the file uri
	 * @param featureName    the feature name
	 */
	public void onResultLimitExceeded(String uri, LimitFeature feature) {
		int resultLimit = 0;
		switch(feature) {
			case SYMBOLS:
				resultLimit = sharedSettings.getSymbolSettings().getMaxItemsComputed();
		}
		onResultLimitExceeded(uri, resultLimit, feature.getName());
	}

	/**
	 * Sends the limit exceeded warning to the client only if the provided
	 * uri does not exist in the cache
	 * 
	 * After sending the warning, the uri is added to the cache
	 * 
	 * @param uri            the file uri
	 * @param resultLimit    the result limit
	 * @param featureName    the feature name
	 */
	public void onResultLimitExceeded(String uri, int resultLimit, String featureName) {
		if (cache.contains(uri)) {
			return;
		}
		sendLimitExceededWarning(uri, resultLimit, featureName);
		cache.add(uri);
	}

	/**
	 * Evict the provided uri from cache
	 * 
	 * @param uri
	 */
	public void evict(String uri) {
		cache.remove(uri);
	}

	/**
	 * Evict all contents from cache
	 */
	public void evictAll() {
		cache.clear();
	}

	/**
	 * Send limit exceeded warning to client
	 * 
	 * @param uri            the file uri
	 * @param resultLimit    the result limit
	 * @param featureName    the feature name
	 */
	private void sendLimitExceededWarning(String uri, int resultLimit, String featureName) {
		String filename = Paths.get(URI.create(uri)).getFileName().toString();
		String message = filename != null ? filename + ": " : "";
		message += "For performance reasons, " + featureName + " have been limited to " + resultLimit
				+ " items.\nIf a new limit is set, please close and reopen this file to recompute " + featureName + ".";

		if (sharedSettings.isActionableNotificationSupport() && sharedSettings.isOpenSettingsCommandSupport()) {
			// create command that opens the settings UI on the client side, in order to
			// quickly edit xml.symbols.maxItemsComputed
			Command command = new Command("Configure limit", ClientCommands.OPEN_SETTINGS,
					Collections.singletonList("xml.symbols.maxItemsComputed"));
			ActionableNotification notification = new ActionableNotification().withSeverity(MessageType.Info)
					.withMessage(message).withCommands(Collections.singletonList(command));
			client.actionableNotification(notification);
		} else {
			// the open settings command is not supported by the client, display a simple
			// message with LSP
			client.showMessage(new MessageParams(MessageType.Warning, message));
		}
	}
}