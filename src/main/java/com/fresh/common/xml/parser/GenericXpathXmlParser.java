package com.fresh.common.xml.parser;

import com.fresh.common.xml.GenericNamespaceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

public class GenericXpathXmlParser extends GenericXmlParser {
    private XPath xPath;
    private GenericNamespaceContext namespaceContext;

    public static GenericXpathXmlParser ofXpathDTD(InputStream xmlIn,
                                                   boolean namespaceAware,
                                                   EntityResolver entityResolver,
                                                   ErrorHandler errorHandler,
                                                   GenericNamespaceContext namespaceContext) {
        return new GenericXpathXmlParser(xmlIn, true, false, true, false,
                namespaceAware, entityResolver, errorHandler, namespaceContext);
    }

    public static GenericXpathXmlParser ofXpathSchema(InputStream xmlIn,
                                                      EntityResolver entityResolver,
                                                      ErrorHandler errorHandler,
                                                      GenericNamespaceContext namespaceContext) {
        return new GenericXpathXmlParser(xmlIn, false, true, false, true,
                true, entityResolver, errorHandler, namespaceContext);
    }

    public static GenericXpathXmlParser ofXpathNoValidator(InputStream xmlIn,
                                                           boolean namespaceAware,
                                                           ErrorHandler errorHandler,
                                                           GenericNamespaceContext namespaceContext) {
        return new GenericXpathXmlParser(xmlIn, false, false, true, false,
                namespaceAware, null, errorHandler, namespaceContext);
    }

    public GenericXpathXmlParser(InputStream xmlIn,
                                 boolean validation,
                                 boolean xsdValidation,
                                 Boolean externalDTD,
                                 Boolean externalSCHEMA,
                                 boolean namespaceAware,
                                 EntityResolver entityResolver,
                                 ErrorHandler errorHandler,
                                 GenericNamespaceContext namespaceContext) {
        super(xmlIn, validation, xsdValidation, externalDTD, externalSCHEMA, namespaceAware, entityResolver, errorHandler);
        if(namespaceAware && namespaceContext == null) {
            throw new IllegalArgumentException("当xml使用名称空间时，xpath必须设置NamespaceContext");
        }
        this.namespaceContext = namespaceContext;
        XPathFactory xPathFactory = XPathFactory.newInstance();
        this.xPath = xPathFactory.newXPath();
        if(isNamespaceAware()) {
            xPath.setNamespaceContext(namespaceContext);
        }
        if(namespaceContext != null) {
            namespaceContext.setDocument(getDocument());
        }
    }

    @Override
    protected void customeDoc(Document document) {
        //do nothing
    }

    public <T> T evaluate(String expression, Class<T> clazz) {
        QName qname = XPathConstants.STRING;
        if(clazz == Node.class)
            qname = XPathConstants.NODE;
        if(clazz == NodeList.class)
            qname = XPathConstants.NODESET;
        if(clazz == String.class)
            qname = XPathConstants.STRING;
        if(clazz == Boolean.class)
            qname = XPathConstants.BOOLEAN;
        if(Number.class.isAssignableFrom(clazz))
            qname = XPathConstants.NUMBER;
        try {
        return (T) xPath.evaluate(expression, getDocument(), qname);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating XPath.  Cause: " + e, e);
        }
    }

    private Object evaluate(String expression, Object root, QName returnType) {
        try {
            return xPath.evaluate(expression, root, returnType);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating XPath.  Cause: " + e, e);
        }
    }

    public String evalString(String exp) {
        String result = (String) evaluate(exp, getDocument(), XPathConstants.STRING);
        return result;
    }

    public boolean evalBoolean(String exp) {
        boolean result = (Boolean) evaluate(exp, getDocument(), XPathConstants.BOOLEAN);
        return result;
    }

    public Integer evalInteger(String exp) {
        return Integer.valueOf(evalString(exp));
    }

    public Double evalDouble(String exp) {
        return (Double) evaluate(exp, getDocument(), XPathConstants.NUMBER);
    }

    public Node evalNode(String exp) {
        Node node = (Node) evaluate(exp, getDocument(), XPathConstants.NODE);
        return node;
    }

    public NodeList evalNodes(String exp) {
        NodeList nodes = (NodeList) evaluate(exp, getDocument(), XPathConstants.NODESET);
        return nodes;
    }

}
