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
package com.sirika.pymager.api;

import static com.sirika.pymager.api.ImageFormat.JPEG;
import static com.sirika.pymager.api.testhelpers.ImageIdObjectMother.yemmaGourayaId;
import static com.sirika.pymager.api.testhelpers.ImageReferenceObjectMother.cornicheKabyle;
import static com.sirika.pymager.api.testhelpers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.pymager.api.testhelpers.PictureStreamSourceObjectMother.textfileresource;
import static com.sirika.pymager.api.testhelpers.PictureStreamSourceObjectMother.yemmaGourayaOriginalPictureStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.cookie.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamSource;

import com.sirika.httpclienthelpers.springframework.InputStreamSourceBody;
import com.sirika.httpclienthelpers.springframework.RepeatableMultipartEntity;
import com.sirika.pymager.api.ImageReference;
import com.sirika.pymager.api.ResourceNotExistingException;
import com.sirika.pymager.api.impl.UploadImageCommand;

/**
 * The idea of this class is to specify the behavior (executable specification)
 * of Image Server for borderline cases.
 * 
 * Some of the tests do not directly hit the ImageServer client API as
 * <ul>
 * <li>the goal is to explicitly reproduce errors that are not possible when
 * using the API (e.g. the client API always generates correct URLs).</li>
 * <li>we want a clearly-readable specification of image server's behavior</li>
 * </ul>
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 * 
 */
public class ImageServerHttpStatusCodesAndHeadersIntegrationTest extends
        AbstractImageServerIntegrationTestCase {

    private static final String IF_MODIFIED_SINCE_HEADER_NAME = "If-Modified-Since";
    private static final String LAST_MODIFIED_HEADER_NAME = "Last-Modified";
    @Autowired
    private HttpClient httpClient;
    @Autowired
    @Qualifier("baseUrl")
    private String baseUrl;

    @Before
    @After
    public void clean() throws IOException {
        cleanup();
    }

    private void cleanup() throws IOException {
        for (ImageReference imageReference : Arrays.asList(yemmaGouraya(),
                cornicheKabyle())) {
            try {
                InputStreamSource source = imageServer
                        .downloadImage(imageReference);
                source.getInputStream();
                imageServer.deleteImage(imageReference.getId());
            } catch (ResourceNotExistingException e) {
                // do nothing, it's fine
            }
        }
    }

    @Test
    public void shouldReturnLastModifiedHeaderForOriginalResource()
            throws ClientProtocolException, IOException, InterruptedException {
        uploadYemmaGouraya();
        HttpGet firstHttpGet = new HttpGet(baseUrl + "/original/"
                + yemmaGourayaId());
        HttpResponse httpResponse = httpClient.execute(firstHttpGet);
        firstHttpGet.abort();
        assertTrue(StringUtils.isNotEmpty(lastModifiedValue(httpResponse)));
    }

    @Test
    public void shouldReturnLastModifiedHeaderForDerivedResource()
            throws ClientProtocolException, IOException, InterruptedException {
        uploadYemmaGouraya();
        HttpGet httpGet = new HttpGet(baseUrl + "/derived/" + yemmaGourayaId()
                + "-100x100.jpg");
        HttpResponse httpResponse = httpClient.execute(httpGet);
        httpGet.abort();
        assertTrue(StringUtils.isNotEmpty(lastModifiedValue(httpResponse)));
    }

    private String lastModifiedValue(HttpResponse httpResponse) {
        return httpResponse.getFirstHeader(LAST_MODIFIED_HEADER_NAME)
                .getValue();
    }

    @Test
    public void shouldRaise304WhenOriginalResourceHasNotBeenModified()
            throws ClientProtocolException, IOException, InterruptedException {
        uploadYemmaGouraya();
        shouldRaise304WithModifiedSinceHeaderForUrl(baseUrl + "/original/"
                + yemmaGourayaId());
    }

    @Test
    public void shouldRaise304WhenDerivedResourceHasNotBeenModified()
            throws ClientProtocolException, IOException, InterruptedException {
        uploadYemmaGouraya();
        shouldRaise304WithModifiedSinceHeaderForUrl(baseUrl + "/derived/"
                + yemmaGourayaId() + "-100x100.jpg");
    }

    private void shouldRaise304WithModifiedSinceHeaderForUrl(String url)
            throws ClientProtocolException, IOException, InterruptedException {
        // DateTime now = new DateTime(DateTimeZone.UTC);
        HttpGet firstHttpGet = new HttpGet(url);
        HttpResponse firstResponse = httpClient.execute(firstHttpGet);
        firstHttpGet.abort();
        HttpGet secondHttpGet = new HttpGet(url);
        secondHttpGet.addHeader(IF_MODIFIED_SINCE_HEADER_NAME,
                lastModifiedValue(firstResponse));
        HttpResponse secondResponse = httpClient.execute(secondHttpGet);

        secondHttpGet.abort();
        assertEquals(HttpStatus.SC_NOT_MODIFIED, secondResponse.getStatusLine()
                .getStatusCode());
    }

    private Date dateInFuture() {
        return new DateTime().plusDays(1).toDate();
    }

    @Test
    public void shouldRaise404WhenDownloadingNotExistingOriginalResource()
            throws ClientProtocolException, IOException {
        HttpGet httpGet = new HttpGet(baseUrl
                + "/original/someOriginalResourceThatDoesNotExist");
        HttpResponse response = httpClient.execute(httpGet);
        httpGet.abort();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine()
                .getStatusCode());
    }

    @Test
    public void shouldRaise404WhenDeletingNonExistingOriginalResource()
            throws ClientProtocolException, IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl
                + "/original/someOriginalResourceThatDoesNotExist");
        HttpResponse response = httpClient.execute(httpDelete);
        httpDelete.abort();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine()
                .getStatusCode());
    }

    @Test
    public void shouldRaise404WhenDownloadingNotExistingDerivedResource()
            throws ClientProtocolException, IOException {
        HttpGet httpGet = new HttpGet(baseUrl
                + "/derived/someDerivedResourceThatDoesNotExist-100x100.jpg");
        HttpResponse response = httpClient.execute(httpGet);
        httpGet.abort();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine()
                .getStatusCode());
    }

    @Test
    public void shouldRaise404WhenDerivedResourceUrlFormatIsInvalid()
            throws ClientProtocolException, IOException {
        HttpGet httpGet = new HttpGet(baseUrl
                + "/derived/derivedResourceThatHasNoSizeNorFormat-100x100.jpg");
        HttpResponse response = httpClient.execute(httpGet);
        httpGet.abort();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine()
                .getStatusCode());
    }

    @Test
    public void shouldRaise400WhenRequestingNotSupportedImageFormat()
            throws ClientProtocolException, IOException {
        uploadYemmaGouraya();
        HttpGet httpGet = new HttpGet(baseUrl + "/derived/" + yemmaGourayaId()
                + "-100x100.pixar");
        HttpResponse response = httpClient.execute(httpGet);
        httpGet.abort();
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine()
                .getStatusCode());
    }

    @Test
    public void shouldRaise405WhenHttpMethodNotSupported()
            throws ClientProtocolException, IOException {
        uploadYemmaGouraya();
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/derived/"
                + yemmaGourayaId() + "-100x100.jpg");
        HttpResponse response = httpClient.execute(httpDelete);
        httpDelete.abort();
        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, response.getStatusLine()
                .getStatusCode());
    }

    @Test
    public void shouldRaise400WhenImageStreamIsNotRecognized()
            throws ClientProtocolException, IOException {

        HttpPost httpPost = new HttpPost(baseUrl + "/original/myimage");
        MultipartEntity entity = new RepeatableMultipartEntity();
        entity.addPart(UploadImageCommand.UPLOAD_PARAMETER_NAME,
                new InputStreamSourceBody(textfileresource(), "text/plain",
                        UploadImageCommand.UPLOAD_PARAMETER_NAME));

        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        httpPost.abort();
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine()
                .getStatusCode());
    }

    @Test
    public void shouldRaise400WhenMultipartEntityDoesNotContainRequiredField()
            throws ClientProtocolException, IOException {
        HttpPost httpPost = new HttpPost(baseUrl + "/original/myimage");
        MultipartEntity entity = new RepeatableMultipartEntity();
        entity.addPart("fuckedupParameterName", new InputStreamSourceBody(
                yemmaGourayaOriginalPictureStream(), JPEG.mimeType(),
                "fuckedupParameterName"));

        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        httpPost.abort();
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine()
                .getStatusCode());
    }

    @Test
    public void shouldRaise409WhenImageIdAlreadyExists()
            throws ClientProtocolException, IOException {
        uploadYemmaGouraya();
        HttpResponse response = uploadYemmaGouraya();
        assertEquals(HttpStatus.SC_CONFLICT, response.getStatusLine()
                .getStatusCode());
    }

    @Test
    public void shouldRaise403WhenSpecifyingUnauthorizedResizeCharacteristics()
            throws ClientProtocolException, IOException {
        uploadYemmaGouraya();
        HttpGet httpGet = new HttpGet(baseUrl + "/derived/" + yemmaGourayaId()
                + "-25000x25000.jpg");
        HttpResponse response = httpClient.execute(httpGet);
        httpGet.abort();
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusLine()
                .getStatusCode());
    }

    private HttpResponse uploadYemmaGouraya() throws IOException,
            ClientProtocolException {
        HttpPost httpPost = new HttpPost(baseUrl + "/original/"
                + yemmaGourayaId());
        MultipartEntity entity = new RepeatableMultipartEntity();
        entity.addPart(UploadImageCommand.UPLOAD_PARAMETER_NAME,
                new InputStreamSourceBody(yemmaGourayaOriginalPictureStream(),
                        JPEG.mimeType(),
                        UploadImageCommand.UPLOAD_PARAMETER_NAME));
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        httpPost.abort();
        return response;
    }

    /*
     * @Test public void
     * shouldThrowResourceNotExistingExceptionWhenResourceNotFound() throws
     * IOException { ImageReference imageReference =
     * originalImage("anyImageThatNobodyHasEverUploadedOnThisPlanet"); try {
     * InputStreamSource source = imageServer.downloadImage(imageReference);
     * source.getInputStream(); fail(); } catch(ResourceNotExistingException e)
     * { assertEquals(imageReference, e.getImageReference()); } }
     * 
     * @Test public void shouldUploadAndDownloadOriginalYemmaGourayaPicture()
     * throws IOException { ImageReference yemmaGouraya =
     * imageServer.uploadImage(yemmaGourayaId(), JPEG,
     * yemmaGourayaOriginalPictureStream()); InputStreamSource source =
     * imageServer.downloadImage(yemmaGouraya); assertNotNull(source);
     * assertTrue(isYemmaGourayaPicture(source));
     * assertFalse(isCornicheKabylePicture(source));
     * imageServer.deleteImage(yemmaGourayaId()); }
     * 
     * @Test(expected=ResourceNotExistingException.class) public void
     * shouldNotDownloadDeletedYemmaGourayaPicture() throws IOException {
     * imageServer.uploadImage(yemmaGourayaId(), JPEG,
     * yemmaGourayaOriginalPictureStream());
     * imageServer.deleteImage(yemmaGourayaId()); ImageReference imageReference
     * = yemmaGouraya(); InputStreamSource source =
     * imageServer.downloadImage(imageReference); source.getInputStream(); }
     * 
     * @Test public void
     * shouldUploadSeveralPicturesAndDownloadCornicheKabylePicture() throws
     * IOException { imageServer.uploadImage(yemmaGourayaId(), JPEG,
     * yemmaGourayaOriginalPictureStream()); ImageReference cornicheKabyle =
     * imageServer.uploadImage(cornicheKabyleId(), JPEG,
     * PictureStreamSourceObjectMother.cornicheKabyleOriginalPictureStream());
     * InputStreamSource source = imageServer.downloadImage(cornicheKabyle);
     * assertNotNull(source); assertFalse(isYemmaGourayaPicture(source));
     * assertTrue(isCornicheKabylePicture(source));
     * imageServer.deleteImage(yemmaGourayaId());
     * imageServer.deleteImage(cornicheKabyleId()); }
     * 
     * @Test public void shouldUploadYemmaGourayaAndDownloadDerivedPicture()
     * throws IOException { ImageReference yemmaGouraya =
     * imageServer.uploadImage(yemmaGourayaId(), JPEG,
     * yemmaGourayaOriginalPictureStream()); InputStreamSource source =
     * imageServer
     * .downloadImage(yemmaGouraya.rescaledTo(width(100).by(100)).convertedTo
     * (JPEG)); assertNotNull(source);
     * assertTrue(is100x100CornicheKabylePicture(source));
     * assertFalse(isCornicheKabylePicture(source));
     * imageServer.deleteImage(yemmaGourayaId()); }
     */
}
