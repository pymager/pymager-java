package com.sirika.imgserver.client.impl;

import static com.sirika.imgserver.client.ImageReference.originalImage;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpDelete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sirika.imgserver.client.ImageId;
import com.sirika.imgserver.client.UnknownDeleteFailureException;
import com.sirika.imgserver.client.UrlGenerator;

public class DeleteImageCommand {
    private static final Logger logger = LoggerFactory.getLogger(DeleteImageCommand.class);
    
    private HttpClient httpClient;
    private UrlGenerator urlGenerator;
    private ImageId imageId;
    
    public DeleteImageCommand(HttpClient httpClient, UrlGenerator urlGenerator,
	    ImageId imageId) {
	super();
	this.httpClient = httpClient;
	this.urlGenerator = urlGenerator;
	this.imageId = imageId;
    }
    
    public void execute() throws UnknownDeleteFailureException{
	try {
	    HttpDelete httpDelete = new HttpDelete(urlGenerator.getImageResourceUrl(originalImage(imageId.toString())));
	    HttpResponse response = httpClient.execute(httpDelete);
	    handleDeleteResponse(imageId, httpDelete, response);
	} catch (IOException e) {
	    throw new UnknownDeleteFailureException(imageId, e);
	}
    }
    
    private void handleDeleteResponse(ImageId id, HttpDelete httpPost, HttpResponse response) throws IOException {
	logger.debug("Received status : {}", response.getStatusLine());
	handleDeleteNon2xxError(id, httpPost, response);
	HttpEntity entity = response.getEntity();
	if(entity != null) {
	    entity.consumeContent();
	}
    }
    
    private void handleDeleteNon2xxError(ImageId id, HttpDelete httpPost, HttpResponse response) {
	if(response.getStatusLine().getStatusCode() >= 300) {
	    httpPost.abort();
	    throw new UnknownDeleteFailureException(id, new HttpResponseException(response.getStatusLine().getStatusCode(), "Error while uploading"));
	}
    }
}
