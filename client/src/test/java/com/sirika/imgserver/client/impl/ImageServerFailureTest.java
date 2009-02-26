package com.sirika.imgserver.client.impl;

import static com.sirika.imgserver.client.ImageFormat.JPEG;
import static com.sirika.imgserver.client.ImageId.imageId;
import static com.sirika.imgserver.client.ImageReference.originalImage;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.yemmaGourayaPictureStream;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.localserver.ServerTestBase;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.Assert;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ImageServer;
import com.sirika.imgserver.client.ResourceNotExistingException;
import com.sirika.imgserver.client.UnknownDownloadFailureException;
import com.sirika.imgserver.client.UnknownUploadFailureException;

public class ImageServerFailureTest extends ServerTestBase {

    private static class ErrorRequestHandler implements HttpRequestHandler {
        
        private int statuscode = HttpStatus.SC_INTERNAL_SERVER_ERROR;

        public ErrorRequestHandler(int statuscode) {
            super();
            if (statuscode > 0) {
                this.statuscode = statuscode;
            }
        }

        public ErrorRequestHandler() {
            this(-1);
        }

        public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
            response.setStatusLine(request.getRequestLine().getProtocolVersion(), this.statuscode);
        }
    }


    public ImageServerFailureTest(String testName) {
	super(testName);
    }

    public void testShouldThrowResourceNotExistingExceptionWhileDownloadingWhenResourceNotFound() throws IOException {
	registerErrorService(HttpStatus.SC_NOT_FOUND);
	
	ImageReference imageReference = originalImage("anyImageThatNobodyHasEverUploadedOnThisPlanet");
	ImageServer imageServer = new ImageServerImpl(getServerHttp().toURI());
	try {
	    InputStreamSource source = imageServer.downloadImage(imageReference);
	    source.getInputStream();
	    Assert.fail();
	} catch(ResourceNotExistingException e) {
	    assertEquals(imageReference, e.getImageReference());
	} 
	
    }
    
    public void testShouldThrowUnknownFailureExceptionWhileDownloadingWhenInternalServerError() throws IOException {
	registerErrorService(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        
	ImageReference imageReference = yemmaGouraya();
	ImageServer imageServer = new ImageServerImpl(getServerHttp().toURI());
	try {
	    InputStreamSource iss = imageServer.downloadImage(imageReference);
	    iss.getInputStream();
	    fail();
	} catch(UnknownDownloadFailureException e) {
	    assertEquals(imageReference, e.getImageReference());
	}
    }
    
    public void testShouldUploadYemmaGourayaPicture() throws IOException {
	registerErrorService(HttpStatus.SC_INTERNAL_SERVER_ERROR);
	ImageServer imageServer = new ImageServerImpl(getServerHttp().toURI());
	try {
	    imageServer.uploadImage(imageId("anyResourceThatWillThrowAnException"), JPEG, yemmaGourayaPictureStream());    
	} catch(UnknownUploadFailureException e) {
	    assertEquals(imageId("anyResourceThatWillThrowAnException"), e.getImageId());
	    assertEquals(JPEG, e.getImageFormat());
	}
	
    }

    private void registerErrorService(int errorCode) {
        this.localServer.register("*", new ErrorRequestHandler(errorCode));
    }
}
