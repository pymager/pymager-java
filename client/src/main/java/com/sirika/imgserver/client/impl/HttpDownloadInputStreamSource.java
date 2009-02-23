/**
 * 
 */
package com.sirika.imgserver.client.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

class HttpDownloadInputStreamSource implements InputStreamSource{
    private final static Logger logger = LoggerFactory.getLogger(HttpDownloadInputStreamSource.class);
    private HttpClient httpClient;
    private String downloadUrl;
    private InputStream inputStream;
    private HttpGet httpGet;
    
    public HttpDownloadInputStreamSource(HttpClient httpClient, String downloadUrl) {
	super();
	this.httpClient = httpClient;
	this.downloadUrl = downloadUrl;
	
	this.httpGet = new HttpGet(downloadUrl);
    }

    public InputStream getInputStream() throws IOException {
	logger.debug("Generating InputStream for URL [{}]", downloadUrl);
	
	HttpResponse response = httpClient.execute(httpGet);
	HttpEntity entity = response.getEntity();
	if(entity != null) {
	    this.inputStream = entity.getContent();
	}
	
        return this.inputStream;
    }
    
}