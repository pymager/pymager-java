package com.sirika.imgserver.client.impl;

import static com.sirika.imgserver.client.ImageFormat.JPEG;
import static com.sirika.imgserver.client.ImageId.imageId;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.yemmaGourayaPictureStream;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.localserver.ServerTestBase;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ImageServer;

public class ImageServerUploadTest extends ServerTestBase {
    
    private static class ImagePOSTRequestHandler implements HttpRequestHandler {
	private boolean called = false;
        public ImagePOSTRequestHandler() {
            super();
        }

        public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
            assertTrue("POST".equals(request.getRequestLine().getMethod()));
            response.setStatusLine(request.getRequestLine().getProtocolVersion(), HttpStatus.SC_OK);
            if("/original/yemmaGouraya".equals(request.getRequestLine().getUri())) {
        	called = true;
            } else {
        	throw new RuntimeException("We are only supposed to handle Yemma Gouraya");
            }
        }

	public void verify() {
	    assertTrue(called);
	}
    }

    private ImagePOSTRequestHandler imagePOSTRequestHandler;
    
    public ImageServerUploadTest(String testName) {
	super(testName);
    }
    
    public void testShouldUploadYemmaGourayaPicture() throws IOException {
	registerImageUploadService();
	ImageServer imageServer = new ImageServerImpl(getServerHttp().toURI());
	ImageReference imageReference = imageServer.uploadImage(imageId("yemmaGouraya"), JPEG, yemmaGourayaPictureStream());
	assertEquals(yemmaGouraya(), imageReference);
	imagePOSTRequestHandler.verify();
	
    }
    
    private void registerImageUploadService() {
	imagePOSTRequestHandler = new ImagePOSTRequestHandler();
        this.localServer.register("*", imagePOSTRequestHandler);
    }
}
