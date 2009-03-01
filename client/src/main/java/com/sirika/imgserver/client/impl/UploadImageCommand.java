package com.sirika.imgserver.client.impl;

import static com.sirika.imgserver.client.ImageReference.originalImage;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageFormat;
import com.sirika.imgserver.client.ImageId;
import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.UnknownUploadFailureException;
import com.sirika.imgserver.client.UrlGenerator;

public class UploadImageCommand {
    private static final String UPLOAD_PARAMETER_NAME = "file";
    private static final Logger logger = LoggerFactory.getLogger(UploadImageCommand.class);
    
    private HttpClient httpClient;
    private UrlGenerator urlGenerator;
    private ImageId imageId;
    private ImageFormat imageFormat;
    private InputStreamSource imageSource;
    
    public UploadImageCommand(HttpClient httpClient, UrlGenerator urlGenerator,
	    ImageId imageId, ImageFormat imageFormat,
	    InputStreamSource imageSource) {
	super();
	this.httpClient = httpClient;
	this.urlGenerator = urlGenerator;
	this.imageId = imageId;
	this.imageFormat = imageFormat;
	this.imageSource = imageSource;
    }

    
    public ImageReference execute() throws UnknownUploadFailureException{
	ImageReference imageReference = originalImage(this.imageId.toString());
	try {
	    HttpPost httpPost = createHttpPostFor(imageFormat, imageSource, imageReference);
	    HttpResponse response = httpClient.execute(httpPost);
	    handleUploadResponse(this.imageId, imageFormat, httpPost, response);
	} catch (IOException e) {
	    throw new UnknownUploadFailureException(this.imageId, imageFormat, e);
	}
	return imageReference;
    }
    
    private void handleUploadResponse(ImageId id, ImageFormat imageFormat,HttpPost httpPost, HttpResponse response) throws IOException {
	logger.debug("Received status : {}", response.getStatusLine());
	handleUploadNon2xxError(id, imageFormat, httpPost, response);
	HttpEntity entity = response.getEntity();
	if(entity != null) {
	    entity.consumeContent();
	}
    }

    private void handleUploadNon2xxError(ImageId id, ImageFormat imageFormat,HttpPost httpPost, HttpResponse response) {
	if(response.getStatusLine().getStatusCode() >= 300) {
	    httpPost.abort();
	    throw new UnknownUploadFailureException(id, imageFormat, new HttpResponseException(response.getStatusLine().getStatusCode(), "Error while uploading"));
	}
    }

    private HttpPost createHttpPostFor(ImageFormat imageFormat, InputStreamSource imageSource, ImageReference imageReference) throws IOException {
	HttpPost httpPost = new HttpPost(urlGenerator.getImageResourceUrl(imageReference));
	httpPost.setEntity(uploadStreamEntity(imageSource, imageFormat));
	return httpPost;
    }

//    private InputStreamEntity uploadStreamEntity(InputStreamSource imageSource, ImageFormat imageFormat) throws IOException {
//	InputStreamEntity entity = new InputStreamEntity(imageSource.getInputStream(), -1);
//	entity.setChunked(true);
//	entity.setContentType(imageFormat.mimeType());
//	return entity;
//    }
    
    private HttpEntity uploadStreamEntity(InputStreamSource imageSource, ImageFormat imageFormat) throws IOException {
	MultipartEntity entity = new MultipartEntity();
	entity.addPart(UPLOAD_PARAMETER_NAME, new InputStreamBody(imageSource.getInputStream(), imageFormat.mimeType(), UPLOAD_PARAMETER_NAME));
	return entity;
    }
}
