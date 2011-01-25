/**
 * Copyright 2009 Sami Dalouche
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sirika.pymager.api.impl;

import static com.sirika.pymager.api.ImageFormat.JPEG;
import static com.sirika.pymager.api.ImageId.imageId;
import static com.sirika.pymager.api.testhelpers.ImageIdObjectMother.yemmaGourayaId;
import static com.sirika.pymager.api.testhelpers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.pymager.api.testhelpers.PictureStreamSourceObjectMother.yemmaGourayaOriginalPictureStream;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.localserver.ServerTestBase;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.sirika.pymager.api.ImageReference;
import com.sirika.pymager.api.ImageServer;
import com.sirika.pymager.api.impl.HttpImageServer;
import com.sirika.pymager.api.testhelpers.ImageIdObjectMother;

public class ImageServerUploadTest extends ServerTestBase {

    private static class ImagePOSTRequestHandler implements HttpRequestHandler {
        private boolean called = false;

        public ImagePOSTRequestHandler() {
            super();
        }

        public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
            assertTrue("POST".equals(request.getRequestLine().getMethod()));
            response.setStatusLine(request.getRequestLine().getProtocolVersion(), HttpStatus.SC_OK);
            if ("/original/yemmaGouraya".equals(request.getRequestLine().getUri())) {
                called = true;
            } else {
                throw new RuntimeException(
                        "We are only supposed to handle Yemma Gouraya");
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
        ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
        ImageReference imageReference = imageServer.uploadImage(yemmaGourayaId(), JPEG, yemmaGourayaOriginalPictureStream());
        assertEquals(yemmaGouraya(), imageReference);
        imagePOSTRequestHandler.verify();

    }

    private void registerImageUploadService() {
        imagePOSTRequestHandler = new ImagePOSTRequestHandler();
        this.localServer.register("*", imagePOSTRequestHandler);
    }
}
