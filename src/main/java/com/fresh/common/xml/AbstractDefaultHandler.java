package com.fresh.common.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDefaultHandler extends DefaultHandler {

    private final EntityResolver entityResolver;
    private final ErrorHandler errorHandler;
    private final List<Map<String, String>> namespaceMappings = new ArrayList<>();

    public AbstractDefaultHandler(EntityResolver entityResolver, ErrorHandler errorHandler) {
        if(errorHandler == null) throw new IllegalArgumentException("ErrorHandler can not be null");
        this.errorHandler = errorHandler;
        this.entityResolver = entityResolver;
    }

////////EntityResolver//////////
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
        if(entityResolver == null) return null;
        return entityResolver.resolveEntity(publicId, systemId);
    }

/////////DTDHandler///////////
    @Override
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        System.out.println("DTDHandler.notationDecl:[" + "name="+name+";publicId="+publicId+";systemId="+systemId+"]");
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        System.out.println("DTDHandler.unparsedEntityDecl:[" + "name="+name+";publicId="+publicId+";systemId="+systemId+";notaionName="+notationName+"]");
    }

//////////ErrorHandler/////////////
    @Override
    public void warning(SAXParseException e) throws SAXException {
        errorHandler.warning(e);
    }
    @Override
    public void error(SAXParseException e) throws SAXException {
        errorHandler.error(e);
    }
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        errorHandler.fatalError(e);
    }

////////////////ContentHandler/////////////////
    @Override
    public void startDocument() throws SAXException {
        clearAllNamespaceMappings();
        newNamespaceMapping();
        startDocumentInternal();
    }


    @Override
    public void endDocument() throws SAXException {
        clearAllNamespaceMappings();
        endDocumentInternal();
    }


    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        currentNamespaceMapping().put(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        //...
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        startElementInternal(parserQName(uri, qName), attributes, currentNamespaceMapping());
        newNamespaceMapping();
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        endElementInternal(parserQName(uri, qName), currentNamespaceMapping());
        clearNamespaceMappings();
    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String param = new String(ch, start, length);
        charactersInternal(param);
    }


    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        String param = new String(ch, start, length);
        ignorableWhitespaceInternal(param);
    }


    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        processingInstructionInternal(target, data);
    }
    
    @Override
    public void skippedEntity(String name) throws SAXException {
        skippedEntityInternal(name);
    }

    protected abstract void startDocumentInternal() throws SAXException;
    protected abstract void endDocumentInternal() throws SAXException;
    protected abstract void startElementInternal(QName qName, Attributes attributes, Map<String, String> namespaceMapping) throws SAXException;
    protected abstract void endElementInternal(QName qName, Map<String, String> namespaceMapping) throws SAXException;
    protected abstract void charactersInternal(String characters) throws SAXException;
    protected abstract void ignorableWhitespaceInternal(String characters) throws SAXException;
    protected abstract void processingInstructionInternal(String target, String data) throws SAXException;
    protected abstract void skippedEntityInternal(String name) throws SAXException;

    private void clearAllNamespaceMappings() {
        this.namespaceMappings.clear();
    }
    private void newNamespaceMapping() {
        this.namespaceMappings.add(new HashMap<>());
    }
    private Map<String, String> currentNamespaceMapping() {
        return this.namespaceMappings.get(this.namespaceMappings.size() - 1);
    }
    private void clearNamespaceMappings() {
        this.namespaceMappings.remove(this.namespaceMappings.size() - 1);
    }
    private QName parserQName(String namespaceURI, String name) {
        int idx = name.indexOf(":");
        if(idx == -1) {
            return new QName(namespaceURI, name);
        } else {
            String prefix = name.substring(0, idx);
            String localPart = name.substring(idx + 1);
            return new QName(name, localPart, prefix);
        }
    }

}
