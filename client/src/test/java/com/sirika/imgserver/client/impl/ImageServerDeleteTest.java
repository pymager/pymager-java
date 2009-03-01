package com.sirika.imgserver.client.impl;

import static com.sirika.imgserver.client.ImageId.imageId;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.localserver.ServerTestBase;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.sirika.imgserver.client.ImageServer;

public class ImageServerDeleteTest extends ServerTestBase {
    
    private static class ImageDeleteRequestHandler implements HttpRequestHandler {
	private boolean called = false;
        public ImageDeleteRequestHandler() {
            super();
        }

        public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
            assertTrue("DELETE".equals(request.getRequestLine().getMethod()));
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

    private ImageDeleteRequestHandler imageDeleteRequestHandler;

    public ImageServerDeleteTest(String testName) {
	super(testName);
    }
    
    public void testShouldDeleteYemmaGourayaPicture() throws IOException {
	registerImageUploadService();
	ImageServer imageServer = new ImageServerImpl(getServerHttp().toURI());
	imageServer.deleteImage(imageId("yemmaGouraya"));
	imageDeleteRequestHandler.verify();
	
    }
    
    private void registerImageUploadService() {
	imageDeleteRequestHandler =  new ImageDeleteRequestHandler();
        this.localServer.register("*", imageDeleteRequestHandler);
    }
}
