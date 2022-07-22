/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.slim3.gen.task;

import org.slim3.gen.message.MessageCode;
import org.slim3.gen.message.MessageFormatter;
import org.slim3.gen.util.CloseableUtil;
import org.slim3.gen.util.StringUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Represents a web configuration file.
 *
 * @author taedium
 * @since 1.0.0
 */
public class WebConfig {

    /**
     * the war directory
     */
    protected final File warDir;

    /**
     * Creates a new {@link WebConfig}.
     *
     * @param warDir the war directory
     */
    public WebConfig(File warDir) {
        if (warDir == null) {
            throw new NullPointerException("The warDir parameter is null.");
        }
        this.warDir = warDir;
    }

    /**
     * Returns a root package name.
     *
     * @return a root package name
     * @throws IOException
     * @throws XPathExpressionException
     */
    public String getRootPackageName() throws IOException,
            XPathExpressionException {
        String rootPackageName =
                evaluate(
                        "/javaee:web-app/javaee:context-param/javaee:param-name[text()='slim3.rootPackage']/../javaee:param-value",
                        "/j2ee:web-app/j2ee:context-param/j2ee:param-name[text()='slim3.rootPackage']/../j2ee:param-value");
        if (rootPackageName != null) {
            return rootPackageName;
        }
        throw new RuntimeException(MessageFormatter
                .getMessage(MessageCode.SLIM3GEN0008));
    }

    /**
     * Evaluates xpath expressions.
     *
     * @param expressions xpath expressions
     * @return evaluated value
     * @throws IOException
     * @throws XPathExpressionException
     */
    protected String evaluate(String... expressions) throws IOException,
            XPathExpressionException {

        File webXmlFile = createWebXml();

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = documentBuilder.parse(webXmlFile);
            final String xmlns = doc.getDocumentElement().getAttribute("xmlns");

            XPath xpath = createXPath(xmlns);
            for (String expression : expressions) {
                InputStream inputStream = new FileInputStream(webXmlFile);
                try {
                    String value = null;
                    value =
                            xpath.evaluate(expression, new InputSource(inputStream));
                    if (!StringUtil.isEmpty(value)) {
                        return value;
                    }
                } finally {
                    CloseableUtil.close(inputStream);
                }
            }
            return null;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Create a XPath.
     *
     * @return a XPath
     */
    protected XPath createXPath(String xmlns) {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                if (prefix == null) {
                    throw new NullPointerException(
                            "The parameter prefix is null.");
                } else if ("javaee".equals(prefix)) {
                    return xmlns;
                } else if ("j2ee".equals(prefix)) {
                    return "http://java.sun.com/xml/ns/j2ee";
                } else if ("xml".equals(prefix)) {
                    return XMLConstants.XML_NS_URI;
                }
                return XMLConstants.NULL_NS_URI;
            }

            public String getPrefix(String uri) {
                throw new UnsupportedOperationException("getPrefix");
            }

            public Iterator<?> getPrefixes(String uri) {
                throw new UnsupportedOperationException("getPrefixes");
            }
        });
        return xpath;
    }

    /**
     * Create a web xml file.
     *
     * @return a web xml file
     */
    protected File createWebXml() {
        return new File(new File(warDir, "WEB-INF"), "web.xml");
    }

}
