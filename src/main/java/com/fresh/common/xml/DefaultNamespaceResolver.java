package com.fresh.common.xml;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 假定所有的namespace都定义在根元素
 */
public class DefaultNamespaceResolver implements GenericNamespaceContext {
    private Document document;
    public static final String default_nx = "edx";//默认名称空间，xpath表达式使用此值

    public DefaultNamespaceResolver(Document document) {
        this.document = document;
    }
    public DefaultNamespaceResolver() {}

    @Override
    public void setDocument(Document document) {
        this.document = document;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if(document == null) throw new RuntimeException("document can not be null");
        if(prefix.equals(default_nx)) {
            return document.lookupNamespaceURI(null);
        }else {
            return document.lookupNamespaceURI(prefix);
        }
    }

    @Override
    public String getPrefix(String namespaceURI) {
        if(document == null) throw new RuntimeException("document can not be null");
        return document.lookupPrefix(namespaceURI);
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        if(document == null) throw new RuntimeException("document can not be null");
        List<String> results = new ArrayList<>();
        results.add(document.lookupPrefix(namespaceURI));
        return results.iterator();
    }

}
