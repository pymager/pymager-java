/**
 * ImgServer Java REST Client
 * Copyright (C) 2008 Sami Dalouche
 *
 * This file is part of ImgServer Java REST Client.
 *
 * ImgServer Java REST Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ImgServer Java REST Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ImgServer.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sirika.imgserver.client.impl;

import static com.sirika.imgserver.client.ImageFormat.JPEG;
import static com.sirika.imgserver.client.ImageId.imageId;
import static com.sirika.imgserver.client.ImageReference.originalImage;
import static com.sirika.imgserver.client.testhelpers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.imgserver.client.testhelpers.PictureStreamSourceObjectMother.yemmaGourayaOriginalPictureStream;

import java.awt.image.ImageProducer;
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

import com.sirika.imgserver.client.BadUploadRequestException;
import com.sirika.imgserver.client.ForbiddenRequestException;
import com.sirika.imgserver.client.ImageAlreadyExistsException;
import com.sirika.imgserver.client.ImageFormat;
import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ImageServer;
import com.sirika.imgserver.client.ResourceNotExistingException;
import com.sirika.imgserver.client.UnknownDeleteFailureException;
import com.sirika.imgserver.client.UnknownDownloadFailureException;
import com.sirika.imgserver.client.testhelpers.PictureStreamSourceObjectMother;

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
	ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
	try {
	    InputStreamSource source = imageServer.downloadImage(imageReference);
	    source.getInputStream();
	    Assert.fail();
	} catch(ResourceNotExistingException e) {
	    assertEquals(imageReference, e.getImageReference());
	} 
	
    }
    
    /**
     * Should theoretically _NEVER_ happen !!
     * @throws IOException
     */
    public void testShouldThrowUnknownDownloadFailureExceptionWhenServerSendsBadRequest() throws IOException {
	registerErrorService(HttpStatus.SC_BAD_REQUEST);
	
	ImageReference imageReference = originalImage("abadrequestthatshouldtheoreticallyneverhappen");
	ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
	try {
	    InputStreamSource source = imageServer.downloadImage(imageReference);
	    source.getInputStream();
	    Assert.fail();
	} catch(UnknownDownloadFailureException e) {
	    assertEquals(imageReference, e.getImageReference());
	} 
	
    }
    
    public void testShouldThrowUnknownFailureExceptionWhileDownloadingWhenInternalServerError() throws IOException {
	registerErrorService(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        
	ImageReference imageReference = yemmaGouraya();
	ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
	try {
	    InputStreamSource iss = imageServer.downloadImage(imageReference);
	    iss.getInputStream();
	    fail();
	} catch(UnknownDownloadFailureException e) {
	    assertEquals(imageReference, e.getImageReference());
	}
    }
    
    public void testShouldThrowUnknownFailureExceptionWhileUploadingYemmaGourayaPictureWhenInternalServerError() throws IOException {
	registerErrorService(HttpStatus.SC_INTERNAL_SERVER_ERROR);
	ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
	try {
	    imageServer.deleteImage(imageId("anyResourceThatWillThrowAnException")); 
	    fail();
	} catch(UnknownDeleteFailureException e) {
	    assertEquals(imageId("anyResourceThatWillThrowAnException"), e.getImageId());
	}
    }
    
    public void testShouldThrowBadUploadRequestExceptionWhileUploading() throws IOException {
	registerErrorService(HttpStatus.SC_BAD_REQUEST);
	ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
	try {
	    imageServer.uploadImage(imageId("anyResourceThatWillThrowAnException"), JPEG, yemmaGourayaOriginalPictureStream());
	    fail();
	} catch(BadUploadRequestException e) {
	    assertEquals(imageId("anyResourceThatWillThrowAnException"), e.getImageId());
	    assertEquals(JPEG, e.getImageFormat());
	}
	
    }

    
    
    public void testShouldThrowImageAlreadyExistsExceptionWhenConflict() throws IOException {
	registerErrorService(HttpStatus.SC_CONFLICT);
	ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
	try {
	    imageServer.uploadImage(imageId("anyResourceThatWillThrowAnException"), JPEG, yemmaGourayaOriginalPictureStream());
	    fail();
	} catch(ImageAlreadyExistsException e) {
	    assertEquals(imageId("anyResourceThatWillThrowAnException"), e.getImageId());
	    assertEquals(JPEG, e.getImageFormat());
	}
    }
    
    public void testShouldThrowForbiddenRequestExceptionWhenForbidden() throws IOException {
	registerErrorService(HttpStatus.SC_FORBIDDEN);
	ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
	try {
	    InputStreamSource iss = imageServer.downloadImage(yemmaGouraya());
	    iss.getInputStream();
	    fail();
	} catch(ForbiddenRequestException e) {
	    
	}
	
    }

    private void registerErrorService(int errorCode) {
        this.localServer.register("*", new ErrorRequestHandler(errorCode));
    }
}
