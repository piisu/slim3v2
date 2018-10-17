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

import java.util.*;
import java.util.concurrent.Future;

import com.google.appengine.api.mail.dev.LocalMailService;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.api.urlfetch.URLFetchServicePb;
import com.google.appengine.api.utils.FutureWrapper;
import com.google.appengine.repackaged.com.google.common.util.concurrent.Futures;
import com.google.appengine.repackaged.com.google.protobuf.ByteString;
import com.google.appengine.tools.development.ApiProxyLocal;
import com.google.appengine.tools.development.Clock;
import com.google.appengine.tools.development.LocalRpcService;
import com.google.appengine.tools.development.testing.*;
import org.slim3.datastore.DatastoreUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.mail.MailServicePb.MailMessage;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import org.slim3.util.FutureUtil;
import org.slim3.util.ThrowableUtil;

/**
 * A tester for local services.
 *
 * @author higa
 * @since 1.0.0
 *
 */
public class AppEngineTester {

    public TestEnvironment environment = new TestEnvironment();

    LocalServiceTestHelper helper = new LocalServiceTestHelper(getTestConfigs()) {
        @Override
        protected Environment newEnvironment() {
            return environment;
        }
    };


    public LocalServiceTestConfig[] getTestConfigs() {
        final String queueFile = System.getProperty("user.dir") + "/src/test/resources/WEB-INF/queue.xml";
        return new LocalServiceTestConfig[]{
                new LocalTaskQueueTestConfig().setQueueXmlPath(queueFile),
                new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(0)};
    }

    public Environment getEnvironment() {
        return ApiProxy.getCurrentEnvironment();
    }

    public void setUp() throws Exception {
        helper.setUp();
        final ApiProxyLocal delegate = (ApiProxyLocal)ApiProxy.getDelegate();
        ApiProxy.setDelegate(new ApiProxyLocal() {
            @Override
            public void setProperty(String property, String value) {
                delegate.setProperty(property, value);
            }

            @Override
            public void setProperties(Map<String, String> properties) {
                delegate.setProperties(properties);
            }

            @Override
            public void appendProperties(Map<String, String> properties) {
                delegate.appendProperties(properties);
            }

            @Override
            public void stop() {
                delegate.stop();
            }

            @Override
            public LocalRpcService getService(String pkg) {
                return delegate.getService(pkg);
            }

            @Override
            public Clock getClock() {
                return delegate.getClock();
            }

            @Override
            public void setClock(Clock clock) {
                delegate.setClock(clock);
            }

            private byte[] executeUrlFetchHandler(byte[] bytes) throws ApiProxy.ApiProxyException {
                try {
                    final URLFetchServicePb.URLFetchRequest requestPb =
                            URLFetchServicePb.URLFetchRequest.parseFrom(bytes);
                    return  URLFetchServicePb.URLFetchResponse
                            .newBuilder()
                            .setContent(
                                    ByteString.copyFrom(urlFetchHandler
                                            .getContent(requestPb)))
                            .setStatusCode(urlFetchHandler.getStatusCode(requestPb))
                            .build()
                            .toByteArray();
                } catch (Exception e) {
                    ThrowableUtil.wrapAndThrow(e);
                }
                return null;
            }

            @Override
            public byte[] makeSyncCall(Environment environment, String s, String m, byte[] bytes) throws ApiProxy.ApiProxyException {
                if (s.equals("urlfetch")
                        && m.equals("Fetch")
                        && urlFetchHandler != null) {
                    return executeUrlFetchHandler(bytes);
                }
                return delegate.makeSyncCall(environment, s, m, bytes);
            }

            @Override
            public Future<byte[]> makeAsyncCall(Environment environment, String s, String m, byte[] bytes, ApiProxy.ApiConfig apiConfig) {
                if (s.equals("urlfetch")
                        && m.equals("Fetch")
                        && urlFetchHandler != null) {
                    return Futures.immediateFuture(executeUrlFetchHandler(bytes));
                }

                return delegate.makeAsyncCall(environment, s, m, bytes, apiConfig);
            }

            @Override
            public void log(Environment environment, ApiProxy.LogRecord logRecord) {
                delegate.log(environment, logRecord);
            }

            @Override
            public void flushLogs(Environment environment) {
                delegate.flushLogs(environment);
            }

            @Override
            public List<Thread> getRequestThreads(Environment environment) {
                return delegate.getRequestThreads(environment);
            }
        });

    }

    public void tearDown() throws Exception {
        helper.tearDown();
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        for (Transaction tx : ds.getActiveTransactions()) {
            tx.rollback();
        }
    }
    protected URLFetchHandler urlFetchHandler;

    public List<QueueStateInfo.TaskStateInfo> getDefaultTaskInfo() {
        return getTaskInfo(QueueFactory.getDefaultQueue().getQueueName());
    }

    public List<QueueStateInfo.TaskStateInfo> getTaskInfo(String queueName) {
        LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
        QueueStateInfo qsi = ltq.getQueueStateInfo().get(queueName);
        return qsi.getTaskInfo();
    }

    public List<MailMessage> getSentMessages() {
        LocalMailService lms = LocalMailServiceTestConfig.getLocalMailService();
        return lms.getSentMessages();
    }

    /**
     * Sets {@link URLFetchHandler}.
     *
     * @param urlFetchHandler
     *            the {@link URLFetchHandler}
     */
    public void setUrlFetchHandler(URLFetchHandler urlFetchHandler) {
        this.urlFetchHandler = urlFetchHandler;
    }

    /**
     * Counts the number of the model.
     * 
     * @param modelClass
     *            the model class
     * @return the number of the model
     * @throws NullPointerException
     *             if the modelClass parameter is null
     */
    public int count(Class<?> modelClass) throws NullPointerException {
        if (modelClass == null) {
            throw new NullPointerException("The modelClass parameter is null.");
        }
        return count(DatastoreUtil.getModelMeta(modelClass).getKind());
    }

    /**
     * Counts the number of the entity.
     * 
     * @param kind
     *            the kind
     * @return the number of the model
     * @throws NullPointerException
     *             if the kind parameter is null
     */
    public int count(String kind) throws NullPointerException {
        if (kind == null) {
            throw new NullPointerException("The kind parameter is null.");
        }
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        return ds.prepare(new Query(kind)).countEntities(
            FetchOptions.Builder.withLimit(Integer.MAX_VALUE));
    }
}