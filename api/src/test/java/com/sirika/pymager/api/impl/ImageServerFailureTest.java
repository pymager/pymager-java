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
import static com.sirika.pymager.api.ImageReference.originalImage;
import static com.sirika.pymager.api.testhelpers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.pymager.api.testhelpers.PictureStreamSourceObjectMother.yemmaGourayaOriginalPictureStream;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.localserver.ServerTestBase;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.Assert;

import com.google.common.io.InputSupplier;
import com.sirika.pymager.api.BadUploadRequestException;
import com.sirika.pymager.api.ForbiddenRequestException;
import com.sirika.pymager.api.ImageAlreadyExistsException;
import com.sirika.pymager.api.ImageReference;
import com.sirika.pymager.api.ImageServer;
import com.sirika.pymager.api.ResourceNotExistingException;
import com.sirika.pymager.api.UnknownDeleteFailureException;
import com.sirika.pymager.api.UnknownGetFailureException;

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
            InputSupplier<InputStream> source = imageServer.downloadImage(imageReference);
            source.getInput();
            Assert.fail();
        } catch (ResourceNotExistingException e) {
            assertEquals(imageReference, e.getImageReference());
        }

    }

    /**
     * Should theoretically _NEVER_ happen !!
     * 
     * @throws IOException
     */
    public void testShouldThrowUnknownDownloadFailureExceptionWhenServerSendsBadRequest() throws IOException {
        registerErrorService(HttpStatus.SC_BAD_REQUEST);

        ImageReference imageReference = originalImage("abadrequestthatshouldtheoreticallyneverhappen");
        ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
        try {
            InputSupplier<InputStream> source = imageServer.downloadImage(imageReference);
            source.getInput();
            Assert.fail();
        } catch (UnknownGetFailureException e) {
            assertEquals(imageReference, e.getImageReference());
        }

    }

    public void testShouldThrowUnknownFailureExceptionWhileDownloadingWhenInternalServerError() throws IOException {
        registerErrorService(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        ImageReference imageReference = yemmaGouraya();
        ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
        try {
            InputSupplier<InputStream> iss = imageServer.downloadImage(imageReference);
            iss.getInput();
            fail();
        } catch (UnknownGetFailureException e) {
            assertEquals(imageReference, e.getImageReference());
        }
    }

    public void testShouldThrowUnknownFailureExceptionWhileUploadingYemmaGourayaPictureWhenInternalServerError() throws IOException {
        registerErrorService(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
        try {
            imageServer.deleteImage(imageId("anyResourceThatWillThrowAnException"));
            fail();
        } catch (UnknownDeleteFailureException e) {
            assertEquals(imageId("anyResourceThatWillThrowAnException"), e.getImageId());
        }
    }

    public void testShouldThrowBadUploadRequestExceptionWhileUploading() throws IOException {
        registerErrorService(HttpStatus.SC_BAD_REQUEST);
        ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
        try {
            imageServer.uploadImage(
                    imageId("anyResourceThatWillThrowAnException"), JPEG,
                    yemmaGourayaOriginalPictureStream());
            fail();
        } catch (BadUploadRequestException e) {
            assertEquals(imageId("anyResourceThatWillThrowAnException"), e.getImageId());
            assertEquals(JPEG, e.getImageFormat());
        }

    }

    public void testShouldThrowImageAlreadyExistsExceptionWhenConflict() throws IOException {
        registerErrorService(HttpStatus.SC_CONFLICT);
        ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
        try {
            imageServer.uploadImage(
                    imageId("anyResourceThatWillThrowAnException"), JPEG,
                    yemmaGourayaOriginalPictureStream());
            fail();
        } catch (ImageAlreadyExistsException e) {
            assertEquals(imageId("anyResourceThatWillThrowAnException"), e
                    .getImageId());
            assertEquals(JPEG, e.getImageFormat());
        }
    }

    public void testShouldThrowForbiddenRequestExceptionWhenForbidden()
            throws IOException {
        registerErrorService(HttpStatus.SC_FORBIDDEN);
        ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
        try {
            InputSupplier<InputStream> iss = imageServer.downloadImage(yemmaGouraya());
            iss.getInput();
            fail();
        } catch (ForbiddenRequestException e) {

        }

    }

    private void registerErrorService(int errorCode) {
        this.localServer.register("*", new ErrorRequestHandler(errorCode));
    }
}
