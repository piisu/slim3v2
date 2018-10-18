package com.google.appengine.tools.development;

import com.google.appengine.api.urlfetch.URLFetchServicePb;
import com.google.appengine.repackaged.com.google.common.util.concurrent.Futures;
import com.google.appengine.repackaged.com.google.protobuf.ByteString;
import com.google.apphosting.api.ApiProxy;
import org.slim3.tester.URLFetchHandler;
import org.slim3.util.ThrowableUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by katsume on 2018/10/18.
 */

/**
 * GCSServiceFactoryのcreateRawGcsServiceでApiProxyのパッケージ名を見てテストかどうか判定しているので、
 * このクラスはcom.google.appengine.tools.developmentに置く必要がある
 * @see com.google.appengine.tools.cloudstorage.GcsServiceFactory#createRawGcsService
 */
public class UrlFetchTestApiProxyWrapper implements ApiProxyLocal {


    private ApiProxyLocal delegate;
    protected URLFetchHandler urlFetchHandler;

    public UrlFetchTestApiProxyWrapper(ApiProxyLocal delegate) {
        this.delegate = delegate;
    }

    public void setUrlFetchHandler(URLFetchHandler urlFetchHandler) {
        this.urlFetchHandler = urlFetchHandler;
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
    public void setProperty(String s, String s1) {
        delegate.setProperty(s, s1);
    }

    @Override
    public void setProperties(Map<String, String> map) {
        delegate.setProperties(map);
    }

    @Override
    public void appendProperties(Map<String, String> map) {
        delegate.appendProperties(map);
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Override
    public LocalRpcService getService(String s) {
        return delegate.getService(s);
    }

    @Override
    public Clock getClock() {
        return delegate.getClock();
    }

    @Override
    public void setClock(Clock clock) {
        delegate.setClock(clock);
    }

    @Override
    public byte[] makeSyncCall(ApiProxy.Environment environment, String s, String s1, byte[] bytes) throws ApiProxy.ApiProxyException {
        if (s.equals("urlfetch")
                && s1.equals("Fetch")
                && urlFetchHandler != null) {
            return executeUrlFetchHandler(bytes);
        }
        return delegate.makeSyncCall(environment, s, s1, bytes);
    }

    @Override
    public Future<byte[]> makeAsyncCall(ApiProxy.Environment environment, String s, String s1, byte[] bytes, ApiProxy.ApiConfig apiConfig) {
        if (s.equals("urlfetch")
                && s1.equals("Fetch")
                && urlFetchHandler != null) {
            return Futures.immediateFuture(executeUrlFetchHandler(bytes));
        }
        return delegate.makeAsyncCall(environment, s, s1, bytes, apiConfig);
    }

    @Override
    public void log(ApiProxy.Environment environment, ApiProxy.LogRecord logRecord) {
        delegate.log(environment, logRecord);
    }

    @Override
    public void flushLogs(ApiProxy.Environment environment) {
        delegate.flushLogs(environment);
    }

    @Override
    public List<Thread> getRequestThreads(ApiProxy.Environment environment) {
        return delegate.getRequestThreads(environment);
    }



}
