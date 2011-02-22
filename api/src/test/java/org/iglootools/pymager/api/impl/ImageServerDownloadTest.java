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
package org.iglootools.pymager.api.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.iglootools.pymager.api.testhelpers.ImageReferenceObjectMother.cornicheKabyle;
import static org.iglootools.pymager.api.testhelpers.ImageReferenceObjectMother.yemmaGouraya;
import static org.iglootools.pymager.api.testhelpers.PictureStreamSourceObjectMother.cornicheKabyleOriginalPictureStream;
import static org.iglootools.pymager.api.testhelpers.PictureStreamSourceObjectMother.yemmaGourayaOriginalPictureStream;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.localserver.ServerTestBase;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.iglootools.pymager.api.ImageReference;
import org.iglootools.pymager.api.ImageServer;
import org.iglootools.pymager.api.UrlGenerator;
import org.iglootools.pymager.api.impl.HttpImageServer;
import org.iglootools.pymager.api.testhelpers.PictureStreamAssertionUtils;
import org.iglootools.pymager.api.testhelpers.PictureStreamSourceObjectMother;
import org.mockito.Mockito;

import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

public class ImageServerDownloadTest extends ServerTestBase {

    private static class ImageGETRequestHandler implements HttpRequestHandler {
        public ImageGETRequestHandler() {
            super();
        }

        public void handle(final HttpRequest request,
                final HttpResponse response, final HttpContext context)
                throws HttpException, IOException {
            assertTrue("GET".equals(request.getRequestLine().getMethod()));
            response.setStatusLine(request.getRequestLine()
                    .getProtocolVersion(), HttpStatus.SC_OK);
            if ("/original/yemmaGouraya".equals(request.getRequestLine()
                    .getUri())) {
                response.setEntity(yemmaGourayaHttpEntity());
            } else if ("/original/cornicheKabyle".equals(request
                    .getRequestLine().getUri())) {
                response.setEntity(cornicheKabyleHttpEntity());
            }

        }

        private EntityTemplate yemmaGourayaHttpEntity() {
            return entityForInputStreamSource(yemmaGourayaOriginalPictureStream());
        }

        private EntityTemplate cornicheKabyleHttpEntity() {
            return entityForInputStreamSource(cornicheKabyleOriginalPictureStream());
        }

        private EntityTemplate entityForInputStreamSource(final InputSupplier<InputStream> iss) {
            return new EntityTemplate(new ContentProducer() {
                public void writeTo(OutputStream outstream) throws IOException {
                    ByteStreams.copy(iss, outstream);
                }
            });
        }
    }

    public ImageServerDownloadTest(String testName) {
        super(testName);
    }

    public void testShouldGenerateUrlByDelegatingToUrlGenerator() {
        ImageReference imageReference = yemmaGouraya();
        UrlGenerator urlGenerator = mock(UrlGenerator.class);
        Mockito.when(urlGenerator.getImageResourceUrl(imageReference)).thenReturn("http://anyurl.com/yemmaGouraya");

        ImageServer imageServer = new HttpImageServer(urlGenerator);
        assertThat(imageServer.getImageResourceUrl(imageReference), is("http://anyurl.com/yemmaGouraya"));
        Mockito.verify(urlGenerator).getImageResourceUrl(imageReference);
    }

    public void testShouldDownloadYemmaGourayaPicture() throws IOException {
        doDownloadImage(yemmaGouraya(), yemmaGourayaOriginalPictureStream());
    }

    public void testShouldDownloadCornicheKabylePicture() throws IOException {
        doDownloadImage(cornicheKabyle(), PictureStreamSourceObjectMother.cornicheKabyleOriginalPictureStream());
    }

    private void doDownloadImage(ImageReference imageReference, InputSupplier<InputStream> expectedInputStreamSource) throws IOException {
        registerImageDownloadService();
        ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
        InputSupplier<InputStream> actual = imageServer.downloadImage(imageReference);
        assertTrue(new PictureStreamAssertionUtils.PictureStreamAsserter(expectedInputStreamSource, actual).isSameStream());
    }

    private void registerImageDownloadService() {
        this.localServer.register("*", new ImageGETRequestHandler());
    }
}
