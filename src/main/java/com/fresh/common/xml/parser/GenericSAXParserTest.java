package com.fresh.common.xml.parser;

import com.fresh.common.xml.AbstractDefaultHandler;
import com.fresh.common.xml.ClasspathEntityResolver;
import com.fresh.common.xml.DefaultErrorHandler;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import java.io.InputStream;
import java.util.Map;

public class GenericSAXParserTest {

    public static void main(String argv[]) throws Exception {

        InputStream in1 = GenericXmlParserTest.class.getClassLoader().getResourceAsStream("com/fresh/common/xml/validate_no_namespace_test.xml");
        GenericSAXParser noValidator = GenericSAXParser.ofNoValidator(true);
        //SAXParser noValidatorParser = noValidator.buildSAXParser();
        //noValidatorParser.parse(in1, new OutHandler(null, new DefaultErrorHandler()));


        InputStream in2 = GenericXmlParserTest.class.getClassLoader().getResourceAsStream("com/fresh/common/xml/validate_test.xml");
        GenericSAXParser dtd = GenericSAXParser.ofDTD(false);
        //SAXParser dtdParser = dtd.buildSAXParser();
        //dtdParser.parse(in2, new OutHandler(new ClasspathEntityResolver("com/freshjuice/fl/xml/*.dtd"), new DefaultErrorHandler()));

        InputStream in3 = GenericXmlParserTest.class.getClassLoader().getResourceAsStream("com/fresh/common/xml/validate_schema_test_2.xml");
        GenericSAXParser xsd = GenericSAXParser.ofSchema(true);
        SAXParser xsdParser = xsd.buildSAXParser();
        xsdParser.parse(in3, new OutHandler(new ClasspathEntityResolver("com/fresh/common/xml/*.xsd"), new DefaultErrorHandler()));


    }
    //特定的xml使用特定Handler接收并保存数据
    private static class OutHandler extends AbstractDefaultHandler {

        public OutHandler(EntityResolver entityResolver, ErrorHandler errorHandler) {
            super(entityResolver, errorHandler);
        }

        @Override
        protected void startDocumentInternal() throws SAXException {
            System.out.println("Document start");
        }

        @Override
        protected void endDocumentInternal() throws SAXException {
            System.out.println("Document end");
        }

        @Override
        protected void startElementInternal(QName qName, Attributes attributes, Map<String, String> namespaceMapping) throws SAXException {
            System.out.println("Element start:<" + qName.getPrefix()+":"+qName.getLocalPart()+">:" + namespaceMapping);
        }

        @Override
        protected void endElementInternal(QName qName, Map<String, String> namespaceMapping) throws SAXException {
            System.out.println("Element end:<" + qName.getPrefix()+":"+qName.getLocalPart()+">:" + namespaceMapping);
        }

        @Override
        protected void charactersInternal(String characters) throws SAXException {
            System.out.println("content: " + characters);
        }

        @Override
        protected void ignorableWhitespaceInternal(String characters) throws SAXException {
            System.out.println("ignorableWhitespace: "+ characters);
        }

        @Override
        protected void processingInstructionInternal(String target, String data) throws SAXException {

        }

        @Override
        protected void skippedEntityInternal(String name) throws SAXException {

        }
    }

}
