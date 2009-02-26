package com.sirika.imgserver.httpclienthelpers;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

public class DefaultHttpClientFactory {
    public static DefaultHttpClient defaultHttpClient() {
	return new DefaultHttpClient(threadSafeClientConnManager(), defaultHttpParams());
    }

    public static ThreadSafeClientConnManager threadSafeClientConnManager() {
	return new ThreadSafeClientConnManager(defaultHttpParams(), defaultSchemeRegistry());
    }

    public static SchemeRegistry defaultSchemeRegistry() {
	SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(
                new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        return schemeRegistry;
    }

    public static BasicHttpParams defaultHttpParams() {
	return new BasicHttpParams();
    }
}
