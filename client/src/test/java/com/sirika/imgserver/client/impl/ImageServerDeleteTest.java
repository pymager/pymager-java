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

import static com.sirika.imgserver.client.ImageId.imageId;
import static com.sirika.imgserver.client.testhelpers.objectmothers.ImageIdObjectMother.yemmaGourayaId;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.localserver.ServerTestBase;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.sirika.imgserver.client.ImageServer;
import com.sirika.imgserver.client.testhelpers.objectmothers.ImageIdObjectMother;

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
	ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
	imageServer.deleteImage(yemmaGourayaId());
	imageDeleteRequestHandler.verify();
	
    }
    
    private void registerImageUploadService() {
	imageDeleteRequestHandler =  new ImageDeleteRequestHandler();
        this.localServer.register("*", imageDeleteRequestHandler);
    }
}
