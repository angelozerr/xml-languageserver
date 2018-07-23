[![Build Status](https://secure.travis-ci.org/angelozerr/lsp4xml.png)](http://travis-ci.org/angelozerr/lsp4xml)

XML Language Server (lsp4xml)
===========================

The **lsp4xml** is a XML language specific implementation of the [Language Server Protocol](https://github.com/Microsoft/language-server-protocol)
and can be used with any editor that supports the protocol, to offer good support for the **XML Language**. The server is based on:

 * [Eclipse LSP4J](https://github.com/eclipse/lsp4j), the Java binding for the Language Server Protocol.
 * Xerces to manage XML Schema validation, completion and hover

Features
--------------

* [textDocument/documentSymbol](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentSymbol).
* [textDocument/documentHighlight](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentHighlight).
* [textDocument/completion](https://microsoft.github.io/language-server-protocol/specification#textDocument_completion).


Extension
--------------

The XML Language Server is extensible with plugin kind (with SPI) to provide custom completion (like XML Schema, camel, Java class completion, etc).

Demo
--------------

![XML Language Server Demo](demos/XMLLanguageServerDemo.gif)

Clients
-------

Here client which consumes this XML Language Server:

 * Eclipse with [lsp4-xml](https://github.com/angelozerr/lsp4e-xml)
 * VSCode : coming soon...
