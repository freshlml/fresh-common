package com.fresh.common.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * TODO
 */
public class DefaultErrorHandler implements ErrorHandler {
    @Override
    public void warning(SAXParseException exception) throws SAXException {
        System.out.println(exception);
    }
    @Override
    public void error(SAXParseException exception) throws SAXException {
        throw new RuntimeException(exception);
    }
    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        throw new RuntimeException(exception);
    }
}
