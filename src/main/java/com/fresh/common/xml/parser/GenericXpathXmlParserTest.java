package com.fresh.common.xml.parser;

import com.fresh.common.xml.ClasspathEntityResolver;
import com.fresh.common.xml.DefaultErrorHandler;
import com.fresh.common.xml.DefaultNamespaceResolver;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

public class GenericXpathXmlParserTest {

    public static void main(String argv[]) {
        InputStream in1 = GenericXpathXmlParserTest.class.getClassLoader().getResourceAsStream("com/fresh/common/xml/validate_no_namespace_test.xml");

        GenericXpathXmlParser noVlaidator = GenericXpathXmlParser.ofXpathNoValidator(in1, false, new DefaultErrorHandler(), null);
        //Node node = noVlaidator.evalNode("/roots/bean");
        //System.out.println(node);

        InputStream in2 = GenericXpathXmlParserTest.class.getClassLoader().getResourceAsStream("com/fresh/common/xml/validate_test.xml");
        GenericXpathXmlParser dtd = GenericXpathXmlParser.ofXpathDTD(in2, false, new ClasspathEntityResolver("com/fresh/common/xml/*.dtd"), new DefaultErrorHandler(), null);
        //Node node = dtd.evalNode("/mapper/select");
        //System.out.println(node);

        InputStream in3 = GenericXpathXmlParserTest.class.getClassLoader().getResourceAsStream("com/fresh/common/xml/validate_schema_test_2.xml");
        GenericXpathXmlParser xsd = GenericXpathXmlParser.ofXpathSchema(in3, new ClasspathEntityResolver("com/fresh/common/xml/*.xsd"), new DefaultErrorHandler(), new DefaultNamespaceResolver());
        Node node = xsd.evalNode("/edx:roots/fl:jj");
        System.out.println(node);
        NodeList node2 = xsd.evaluate("/edx:roots/edx:bb", NodeList.class);
        System.out.println(node2);

    }




}
