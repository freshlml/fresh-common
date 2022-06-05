package com.fresh.common.xml.parser;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

public class GenericSAXParser {

    private boolean validation;
    private boolean xsdValidation;
    private Boolean externalDTD = Boolean.TRUE;
    private Boolean externalSCHEMA = Boolean.TRUE;
    private boolean namespaceAware;

    public static GenericSAXParser ofDTD(boolean namespaceAware) {
        return new GenericSAXParser(true, false, true, false, namespaceAware);
    }
    public static GenericSAXParser ofSchema(boolean namespaceAware) {
        return new GenericSAXParser(false, true, false, true, namespaceAware);
    }
    public static GenericSAXParser ofNoValidator(boolean namespaceAware) {
        return new GenericSAXParser(false, false, false, false, namespaceAware);
    }

    public GenericSAXParser(boolean validation,
                            boolean xsdValidation,
                            Boolean externalDTD,
                            Boolean externalSCHEMA,
                            boolean namespaceAware) {
        if(validation && xsdValidation) {
            throw new IllegalArgumentException("can not set validation=true; where set xsdValidation=true");
        }
        this.validation = validation;
        this.xsdValidation = xsdValidation;
        if(externalDTD != null) {
            this.externalDTD = externalDTD;
        }
        if(externalSCHEMA != null) {
            this.externalSCHEMA = externalSCHEMA;
        }
        this.namespaceAware = namespaceAware;
    }

    public SAXParser buildSAXParser() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setNamespaceAware(namespaceAware);
            factory.setXIncludeAware(false);
            validatorBefore(factory);
            SAXParser saxParser = factory.newSAXParser();
            validatorAfter(saxParser);
            return saxParser;
        } catch (Exception e) {
            throw new RuntimeException("build SAXParser error. Cause: " + e, e);
        }
    }

    protected void validatorBefore(SAXParserFactory factory) {
        try {
            if (validation) {
                factory.setValidating(true);
            } else if (xsdValidation) {
                factory.setValidating(false);
                factory.setNamespaceAware(true);
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema();
                factory.setSchema(schema);
            }
        } catch (SAXException e) {
            throw new RuntimeException("validator parser error. Cause: "+ e, e);
        }
    }

    protected void validatorAfter(SAXParser saxParser) {
        try {
            if (externalDTD) {
                saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "all");
            }
            if (xsdValidation && externalSCHEMA) {
                saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "all");
            }
        } catch (Exception e) {
            throw new RuntimeException("validator parser error. Cause: "+ e, e);
        }
    }

}
