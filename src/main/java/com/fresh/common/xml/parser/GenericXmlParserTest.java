package com.fresh.common.xml.parser;

import com.fresh.common.xml.ClasspathEntityResolver;
import com.fresh.common.xml.DefaultErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

public class GenericXmlParserTest {

    public static void main(String argv[]) throws Exception {
        InputStream in1 = GenericXmlParserTest.class.getClassLoader().getResourceAsStream("com/fresh/common/xml/validate_no_namespace_test.xml");

        GenericXmlParser noVlaidator = GenericXmlParser.ofNoValidator(in1, false, new DefaultErrorHandler());
        //out(noVlaidator.getDocument());

        InputStream in2 = GenericXmlParserTest.class.getClassLoader().getResourceAsStream("com/fresh/common/xml/validate_test.xml");
        GenericXmlParser dtd = GenericXmlParser.ofDTD(in2, false, new ClasspathEntityResolver("com/fresh/common/xml/*.dtd"), new DefaultErrorHandler());
        //out(dtd.getDocument());

        InputStream in3 = GenericXmlParserTest.class.getClassLoader().getResourceAsStream("com/fresh/common/xml/validate_schema_test_2.xml");
        GenericXmlParser xsd = GenericXmlParser.ofSchema(in3, new ClasspathEntityResolver("com/fresh/common/xml/*.xsd"), new DefaultErrorHandler());
        out(xsd.getDocument());

    }

    public static void out(Document document) {
        Element rootElem = document.getDocumentElement();
        NodeList childs = rootElem.getChildNodes();
        for(int i=0; i<childs.getLength(); i++) {
            Node node = childs.item(i);
            System.out.println(node.getNodeName() + ";" + node.getTextContent() + ";" + node.getNamespaceURI());
        }
    }
}
