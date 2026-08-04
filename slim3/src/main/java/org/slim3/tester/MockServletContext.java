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
package org.slim3.tester;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.slim3.util.WrapRuntimeException;

/**
 * A mock implementation for {@link ServletContext}.
 * 
 * @author higa
 * @since 1.0.0
 */
public class MockServletContext implements ServletContext, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Major Version
     */
    public static final int MAJOR_VERSION = 2;

    /**
     * Minor Version
     */
    public static final int MINOR_VERSION = 4;

    /**
     * The servlet context name.
     */
    protected String servletContextName = "";

    /**
     * The context path.
     */
    protected String contextPath = "";

    /**
     * The server information.
     */
    protected String serverInfo = "Slim3";
    /**
     * The map for the initial parameters.
     */
    protected Map<String, String> initParameterMap =
        new HashMap<String, String>();

    /**
     * The map for the attributes.
     */
    protected Map<String, Object> attributeTable =
        new HashMap<String, Object>();

    /**
     * The map for resource paths.
     */
    protected Map<String, Set<String>> resourcePathsMap =
        new HashMap<String, Set<String>>();

    /**
     * The map for resource.
     */
    protected Map<String, URL> resourceMap = new HashMap<String, URL>();

    /**
     * The map for initial parameters.
     */
    protected Map<String, String> realPathMap = new HashMap<String, String>();

    /**
     * The latest request dispatcher.
     */
    protected MockRequestDispatcher latestRequestDispatcher;

    protected int sessionTimeout;

    protected String requestCharacterEncoding;

    protected String responseCharacterEncoding;

    /**
     * Constructor.
     */
    public MockServletContext() {
    }

    public ServletContext getContext(String path) {
        throw new UnsupportedOperationException();
    }

    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    public int getEffectiveMajorVersion() {
        return MAJOR_VERSION;
    }

    public int getEffectiveMinorVersion() {
        return MINOR_VERSION;
    }

    public String getMimeType(String file) {
        throw new UnsupportedOperationException();
    }

    public Set<String> getResourcePaths(String path) {
        Set<String> resourcePaths = resourcePathsMap.get(path);
        if (resourcePaths != null) {
            return resourcePaths;
        }
        return new HashSet<String>();
    }

    /**
     * Adds the resource paths.
     * 
     * @param path
     *            the path
     * @param resourcePaths
     *            the resource paths
     */
    public void addResourcePaths(String path, Set<String> resourcePaths) {
        resourcePathsMap.put(path, resourcePaths);
    }

    public URL getResource(String path) {
        return resourceMap.get(path);
    }

    /**
     * Adds the URL resource.
     * 
     * @param path
     *            the path
     * @param url
     *            the URL resource
     */
    public void addResource(String path, URL url) {
        resourceMap.put(path, url);
    }

    public InputStream getResourceAsStream(String path) {
        try {
            URL url = getResource(path);
            if (url == null) {
                return null;
            }
            return url.openStream();
        } catch (Exception e) {
            throw new WrapRuntimeException(e);
        }
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        latestRequestDispatcher = new MockRequestDispatcher(path);
        return latestRequestDispatcher;
    }

    /**
     * Returns the latest request dispatcher.
     * 
     * @return the latest request dispatcher
     */
    public MockRequestDispatcher getLatestRequestDispatcher() {
        return latestRequestDispatcher;
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        throw new UnsupportedOperationException();
    }

    public Servlet getServlet(String name) throws ServletException {
        throw new UnsupportedOperationException();
    }

    public Enumeration<Servlet> getServlets() {
        throw new UnsupportedOperationException();
    }

    public Enumeration<String> getServletNames() {
        throw new UnsupportedOperationException();
    }

    public void log(String message) {
        System.out.println(message);
    }

    public void log(Exception ex, String message) {
        System.out.println(message);
        ex.printStackTrace();
    }

    public void log(String message, Throwable t) {
        System.out.println(message);
        t.printStackTrace();
    }

    public String getRealPath(String path) {
        return realPathMap.get(path);
    }

    /**
     * Adds the real path.
     * 
     * @param path
     *            the context relative path
     * @param realPath
     *            the real path
     */
    public void addRealPath(String path, String realPath) {
        realPathMap.put(path, realPath);
    }

    public String getServerInfo() {
        return serverInfo;
    }

    /**
     * Sets the server information.
     * 
     * @param serverInfo
     *            the server information
     */
    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }

    public String getInitParameter(String name) {
        return initParameterMap.get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameterMap.keySet());
    }

    /**
     * Sets the initial parameter.
     * 
     * @param name
     *            the parameter name
     * @param value
     *            the value
     */
    public boolean setInitParameter(String name, String value) {
        initParameterMap.put(name, value);
        return true;
    }

    /**
     * Removes the initial parameter.
     * 
     * @param name
     *            the parameter name
     */
    public void removeInitParameter(String name) {
        initParameterMap.remove(name);
    }

    public Object getAttribute(String name) {
        return attributeTable.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributeTable.keySet());
    }

    public void setAttribute(String name, Object value) {
        attributeTable.put(name, value);
    }

    public void removeAttribute(String name) {
        attributeTable.remove(name);
    }

    public String getServletContextName() {
        return servletContextName;
    }

    /**
     * Sets the servlet context name.
     * 
     * @param servletContextName
     *            the servlet context name
     */
    public void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    public String getContextPath() {
        return contextPath;
    }

    /**
     * Sets the context path.
     * 
     * @param contextPath
     *            the context path
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public ServletRegistration.Dynamic addServlet(
            String servletName, String className) {
        return null;
    }

    public ServletRegistration.Dynamic addServlet(
            String servletName, Servlet servlet) {
        return null;
    }

    public ServletRegistration.Dynamic addServlet(
            String servletName, Class<? extends Servlet> servletClass) {
        return null;
    }

    public ServletRegistration.Dynamic addJspFile(
            String servletName, String jspFile) {
        return null;
    }

    public <T extends Servlet> T createServlet(Class<T> clazz)
            throws ServletException {
        return createInstance(clazz);
    }

    public ServletRegistration getServletRegistration(String servletName) {
        return null;
    }

    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return Collections.emptyMap();
    }

    public FilterRegistration.Dynamic addFilter(
            String filterName, String className) {
        return null;
    }

    public FilterRegistration.Dynamic addFilter(
            String filterName, Filter filter) {
        return null;
    }

    public FilterRegistration.Dynamic addFilter(
            String filterName, Class<? extends Filter> filterClass) {
        return null;
    }

    public <T extends Filter> T createFilter(Class<T> clazz)
            throws ServletException {
        return createInstance(clazz);
    }

    public FilterRegistration getFilterRegistration(String filterName) {
        return null;
    }

    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return Collections.emptyMap();
    }

    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    public void setSessionTrackingModes(
            Set<SessionTrackingMode> sessionTrackingModes) {
    }

    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return Collections.emptySet();
    }

    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return Collections.emptySet();
    }

    public void addListener(String className) {
    }

    public <T extends EventListener> void addListener(T listener) {
    }

    public void addListener(Class<? extends EventListener> listenerClass) {
    }

    public <T extends EventListener> T createListener(Class<T> clazz)
            throws ServletException {
        return createInstance(clazz);
    }

    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public void declareRoles(String... roleNames) {
    }

    public String getVirtualServerName() {
        return serverInfo;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getRequestCharacterEncoding() {
        return requestCharacterEncoding;
    }

    public void setRequestCharacterEncoding(String requestCharacterEncoding) {
        this.requestCharacterEncoding = requestCharacterEncoding;
    }

    public String getResponseCharacterEncoding() {
        return responseCharacterEncoding;
    }

    public void setResponseCharacterEncoding(String responseCharacterEncoding) {
        this.responseCharacterEncoding = responseCharacterEncoding;
    }

    private <T> T createInstance(Class<T> clazz) throws ServletException {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
