package com.sirika.imgserver.client.impl;


import org.apache.commons.lang.Validate;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageId;
import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ImageServer;
import com.sirika.imgserver.client.ResourceNotExistingException;
import com.sirika.imgserver.client.UnknownFailureException;

public class ImageServerImpl implements ImageServer {
    private static final Logger logger = LoggerFactory.getLogger(ImageServerImpl.class);
    private UrlGenerator urlGenerator;
    private HttpClient httpClient;
    
    public ImageServerImpl(String baseImageServiceUrl) {
	this.urlGenerator = new RESTfulUrlGenerator(baseImageServiceUrl);
	this.httpClient = defaultHttpClient();
	logCreation();
    }

    public ImageServerImpl(UrlGenerator urlGenerator) {
	this.urlGenerator = urlGenerator ;
	this.httpClient = defaultHttpClient();
	logCreation();
    }
    
    public ImageServerImpl(HttpClient httpClient, UrlGenerator urlGenerator) {
	this.urlGenerator = urlGenerator ;
	this.httpClient = httpClient;
	logCreation();
    }

    private void logCreation() {
	logger.info("Creating Image Server using URLGenerator [{}], HttpClient [{}]", urlGenerator, httpClient);
    }
   
    private DefaultHttpClient defaultHttpClient() {
	return new DefaultHttpClient(threadSafeClientConnManager(), httpParams());
    }

    private ThreadSafeClientConnManager threadSafeClientConnManager() {
	return new ThreadSafeClientConnManager(httpParams(), schemeRegistry());
    }

    private SchemeRegistry schemeRegistry() {
	SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(
                new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        return schemeRegistry;
    }

    private BasicHttpParams httpParams() {
	return new BasicHttpParams();
    }
    
    public void deleteImage(ImageId imageId) {
	// TODO Auto-generated method stub

    }

    public InputStreamSource downloadImage(ImageReference imageReference) throws ResourceNotExistingException, UnknownFailureException{
	logger.debug("Generating InputStreamSource for Image Reference [{}]", imageReference);
	return new HttpDownloadInputStreamSource(this, httpClient, imageReference);
    }

    public String getDownloadUrl(ImageReference imageReference) {
	String url = urlGenerator.urlFor(imageReference);
	if(logger.isDebugEnabled()) {
	    logger.debug("getDownloadUrl: generated URL : {}", url);
	}
	return url;
    }

    public ImageReference uploadImage(ImageId id, InputStreamSource imageSource) {
	// TODO Auto-generated method stub
	return null;
    }
    
    public void destroy() throws Exception {
	this.httpClient.getConnectionManager().shutdown();
    }

    public void setUrlGenerator(UrlGenerator urlGenerator) {
	Validate.notNull(urlGenerator);
        this.urlGenerator = urlGenerator;
    }

    public void setHttpClient(HttpClient httpClient) {
	Validate.notNull(httpClient);
	this.httpClient = httpClient;
    }

}
