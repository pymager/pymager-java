package com.sirika.imgserver.client.impl;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ResourceNotExistingException;
import com.sirika.imgserver.client.UnknownDownloadFailureException;
import com.sirika.imgserver.client.UrlGenerator;

public class DownloadImageCommand {
    private static final Logger logger = LoggerFactory.getLogger(DownloadImageCommand.class);
    
    private HttpClient httpClient;
    private UrlGenerator urlGenerator;
    private ImageReference imageReference;
    
    public DownloadImageCommand(HttpClient httpClient,UrlGenerator urlGenerator, ImageReference imageReference) {
	super();
	this.httpClient = httpClient;
	this.urlGenerator = urlGenerator;
	this.imageReference = imageReference;
    }
    
    public InputStreamSource execute() throws ResourceNotExistingException, UnknownDownloadFailureException{
	return new HttpDownloadInputStreamSource(this.httpClient, this.urlGenerator, this.imageReference);
    }
    
}
