/**
 * PyMager Java REST Client
 * Copyright (C) 2008 Sami Dalouche
 *
 * This file is part of PyMager Java REST Client.
 *
 * PyMager Java REST Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PyMager Java REST Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PyMager Java REST Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sirika.pymager.client.impl;

import static com.sirika.pymager.client.testhelpers.ImageReferenceObjectMother.cornicheKabyle;
import static com.sirika.pymager.client.testhelpers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.pymager.client.testhelpers.PictureStreamSourceObjectMother.cornicheKabyleOriginalPictureStream;
import static com.sirika.pymager.client.testhelpers.PictureStreamSourceObjectMother.yemmaGourayaOriginalPictureStream;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.localserver.ServerTestBase;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.mockito.Mockito;
import org.springframework.core.io.InputStreamSource;

import com.sirika.pymager.client.ImageReference;
import com.sirika.pymager.client.ImageServer;
import com.sirika.pymager.client.UrlGenerator;
import com.sirika.pymager.client.testhelpers.PictureStreamAssertionUtils;
import com.sirika.pymager.client.testhelpers.PictureStreamSourceObjectMother;

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

        private EntityTemplate entityForInputStreamSource(
                final InputStreamSource iss) {
            return new EntityTemplate(new ContentProducer() {
                public void writeTo(OutputStream outstream) throws IOException {
                    InputStream yemmaGouraya = iss.getInputStream();
                    IOUtils.copy(yemmaGouraya, outstream);
                    IOUtils.closeQuietly(yemmaGouraya);
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
        assertThat(imageServer.getImageResourceUrl(imageReference),
                is("http://anyurl.com/yemmaGouraya"));
        Mockito.verify(urlGenerator).getImageResourceUrl(imageReference);
    }

    public void testShouldDownloadYemmaGourayaPicture() throws IOException {
        doDownloadImage(yemmaGouraya(), yemmaGourayaOriginalPictureStream());
    }

    public void testShouldDownloadCornicheKabylePicture() throws IOException {
        doDownloadImage(cornicheKabyle(), PictureStreamSourceObjectMother
                .cornicheKabyleOriginalPictureStream());
    }

    private void doDownloadImage(ImageReference imageReference,
            InputStreamSource expectedInputStreamSource) throws IOException {
        registerImageDownloadService();
        ImageServer imageServer = new HttpImageServer(getServerHttp().toURI());
        InputStreamSource actual = imageServer.downloadImage(imageReference);
        assertTrue(new PictureStreamAssertionUtils.PictureStreamAsserter(
                expectedInputStreamSource, actual).isSameStream());
    }

    private void registerImageDownloadService() {
        this.localServer.register("*", new ImageGETRequestHandler());
    }
}
