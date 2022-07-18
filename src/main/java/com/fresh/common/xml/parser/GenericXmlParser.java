package com.fresh.common.xml.parser;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;

public class GenericXmlParser {

    private final Document document;
    /**
     * open DTD validation
     * when this value set to true, the xsdValidation can not set to true, otherwise it were
     * throws IllegalArgumentException
     * 注意，是否开启校验与external DTD文件是否加载没有关系，如果xml中定义了外部DTD，则会加载external DTD
     */
    private boolean validation;
    /**
     * using SCHEMA validation
     * when this value set to true, the validation can not set to true, otherwise it were
     * throws IllegalArgumentException
     */
    private boolean xsdValidation;
    /**
     * 开放external DTD加载协议
     */
    private Boolean externalDTD = Boolean.TRUE;
    /**
     * 开放SCHEMA 加载协议
     */
    private Boolean externalSCHEMA = Boolean.TRUE;
    /**
     * xml是否使用名称空间语法
     */
    private boolean namespaceAware;
    /**
     * entity加载，如external DTD,schema
     */
    private EntityResolver entityResolver;
    /**
     * errorHandler,if null, will throws IllegalArgumentException
     */
    private ErrorHandler errorHandler;

    public Document getDocument() {
        return document;
    }
    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    public static GenericXmlParser ofDTD(InputStream xmlIn,
                                         boolean namespaceAware,
                                         EntityResolver entityResolver,
                                         ErrorHandler errorHandler) {
        return new GenericXmlParser(xmlIn, true, false, true, false,
                namespaceAware, entityResolver, errorHandler);
    }

    public static GenericXmlParser ofSchema(InputStream xmlIn,
                                            EntityResolver entityResolver,
                                            ErrorHandler errorHandler) {
        return new GenericXmlParser(xmlIn, false, true, false, true,
                true, entityResolver, errorHandler);
    }

    public static GenericXmlParser ofNoValidator(InputStream xmlIn,
                                                 boolean namespaceAware,
                                                 ErrorHandler errorHandler) {
        return new GenericXmlParser(xmlIn, false, false, true, false,
                namespaceAware, null, errorHandler);
    }

    /**
     *
     * @param xmlIn xml
     * @param validation 是否dtd校验
     * @param xsdValidation 是否xsd校验
     * @param externalDTD  xml中有dtd，则设置为true
     * @param externalSCHEMA xml中有xsd，则设置为true
     * @param namespaceAware xml中使用了名称空间
     * @param entityResolver 如果需要自定义加载dtd ，xsd,则设置
     * @param errorHandler ErrorHandler
     */
    public GenericXmlParser(InputStream xmlIn,
                            boolean validation,
                            boolean xsdValidation,
                            Boolean externalDTD,
                            Boolean externalSCHEMA,
                            boolean namespaceAware,
                            EntityResolver entityResolver,
                            ErrorHandler errorHandler) {
        if(validation && xsdValidation) {
            throw new IllegalArgumentException("can not set validation=true; where set xsdValidation=true");
        }
        if(errorHandler == null) {
            throw new IllegalArgumentException("parameter errorHandler can not null");
        }
        InputSource inputSource = new InputSource(xmlIn);
        this.validation = validation;
        this.xsdValidation = xsdValidation;
        if(externalDTD != null) {
            this.externalDTD = externalDTD;
        }
        if(externalSCHEMA != null) {
            this.externalSCHEMA = externalSCHEMA;
        }
        this.namespaceAware = namespaceAware;
        this.entityResolver = entityResolver;
        this.errorHandler = errorHandler;
        DocumentBuilderFactory factory = getDomBuilderFactory();
        factory = validator(factory);

        this.document = getDocument(factory, inputSource);
        customeDoc(document);
    }


    protected void customeDoc(Document document) {
        //do nothing
    }
    protected DocumentBuilderFactory getDomBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            factory.setNamespaceAware(namespaceAware);
            factory.setIgnoringComments(true); //忽略comments节点
            factory.setCoalescing(false); //是否将CDATA节点转换为Text节点，并将其附加到相邻（如果有）的 Text 节点，false
            factory.setExpandEntityReferences(false); //不允许实体引用
            factory.setIgnoringElementContentWhitespace(false); //不消除元素内容中的空格
            factory.setXIncludeAware(false); //不处理XInclude markup
            
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Error creating documentBuilderFactory.  Cause: " + e, e);
        }
        return factory;
    }

    protected DocumentBuilderFactory validator(DocumentBuilderFactory factory) {
        if(externalDTD) {
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "all");
        }
        if(validation) {
            factory.setValidating(true);
        } else if(xsdValidation) {
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = null;
            try {
                schema = schemaFactory.newSchema();
            } catch (SAXException e) {
                throw new RuntimeException("can not create Schema.  Cause: " + e, e);
            }
            factory.setSchema(schema);
            if(externalSCHEMA) {
                factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "all");
            }
        }
        return factory;
    }

    protected Document getDocument(DocumentBuilderFactory factory, InputSource inputSource) {
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(entityResolver);
            builder.setErrorHandler(errorHandler);
            return builder.parse(inputSource);
        } catch (Exception e) {
            throw new RuntimeException("can not create Document.  Cause: " + e, e);
        }
    }


}
