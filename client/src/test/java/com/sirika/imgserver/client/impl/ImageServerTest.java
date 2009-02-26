package com.sirika.imgserver.client.impl;

import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.cornicheKabyle;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.cornicheKabylePictureStream;
import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.yemmaGourayaPictureStream;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ImageServer;
import com.sirika.imgserver.client.objectmothers.PictureStreamAssertionUtils;
import com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother;

public class ImageServerTest extends ServerTestBase {
    
    private static class ImageGETRequestHandler implements HttpRequestHandler {
        public ImageGETRequestHandler() {
            super();
        }

        public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
            assertTrue("GET".equals(request.getRequestLine().getMethod()));
            response.setStatusLine(request.getRequestLine().getProtocolVersion(), HttpStatus.SC_OK);
            if("/original/yemmaGouraya".equals(request.getRequestLine().getUri())) {
        	response.setEntity(yemmaGourayaHttpEntity());
            } else if("/original/cornicheKabyle".equals(request.getRequestLine().getUri())) {
        	response.setEntity(cornicheKabyleHttpEntity());
            }
            
        }

	private EntityTemplate yemmaGourayaHttpEntity() {
	    return entityForInputStreamSource(yemmaGourayaPictureStream());
	}
	
	private EntityTemplate cornicheKabyleHttpEntity() {
	    return entityForInputStreamSource(cornicheKabylePictureStream());
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


    public ImageServerTest(String testName) {
	super(testName);
    }

    public void testShouldGenerateUrlByDelegatingToUrlGenerator() {
	ImageReference imageReference = yemmaGouraya();
	UrlGenerator urlGenerator = createMock(UrlGenerator.class);
	expect(urlGenerator.urlFor(imageReference)).andReturn("http://anyurl.com/yemmaGouraya");
	replay(urlGenerator);

	ImageServer imageServer = new ImageServerImpl(urlGenerator);
	assertThat(imageServer.getDownloadUrl(imageReference),is("http://anyurl.com/yemmaGouraya"));
	verify(urlGenerator);
    }
    
    public void testShouldDownloadYemmaGourayaPicture() throws IOException {
	doDownloadImage(yemmaGouraya(), yemmaGourayaPictureStream());
    }

    public void testShouldDownloadCornicheKabylePicture() throws IOException {
	doDownloadImage(cornicheKabyle(), PictureStreamSourceObjectMother.cornicheKabylePictureStream());
    }
    
    private void doDownloadImage(ImageReference imageReference, InputStreamSource expectedInputStreamSource) throws IOException {
	registerImageDownloadService();
	ImageServer imageServer = new ImageServerImpl(getServerHttp().toURI());
	InputStreamSource actual = imageServer.downloadImage(imageReference);
	assertTrue(new PictureStreamAssertionUtils.PictureStreamAsserter(expectedInputStreamSource, actual).isSameStream());
    }
    /*
    public void testThrowResourceNotExistingExceptionWhenResourceNotFound() throws IOException {
	registerImageDownloadService();
	
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
    
    public void testShouldThrowUnknownFailureExceptionWhenInternalServerError() throws IOException {
	registerImageDownloadService();
        
	ImageReference imageReference = yemmaGouraya();
	ImageServer imageServer = new ImageServerImpl(getServerHttp().toURI());
	try {
	    InputStreamSource iss = imageServer.downloadImage(imageReference);
	    iss.getInputStream();
	    fail();
	} catch(UnknownFailureException e) {
	    Assert.assertEquals(imageReference, e.getImageReference());
	}
    }
     */
    private void registerImageDownloadService() {
        this.localServer.register("*", new ImageGETRequestHandler());
    }
}
