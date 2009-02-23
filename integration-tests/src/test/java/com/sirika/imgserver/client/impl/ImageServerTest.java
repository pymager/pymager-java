package com.sirika.imgserver.client.impl;

import static com.sirika.imgserver.client.ImageReference.originalImage;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.yemmaGourayaPictureStream;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ResourceNotExistingException;
import com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother;

public class ImageServerTest {

    private ImageServerImpl imageServer;

    @Before
    public void setup() {
	this.imageServer = new ImageServerImpl("http://localhost:8000");
    }

    @Test
    public void shouldGenerateUrlByDelegatingToUrlGenerator() {
	ImageReference imageReference = yemmaGouraya();
	UrlGenerator urlGenerator = createMock(UrlGenerator.class);
	expect(urlGenerator.urlFor(imageReference)).andReturn("http://anyurl.com/yemmaGouraya");
	replay(urlGenerator);

	this.imageServer.setUrlGenerator(urlGenerator);

	assertThat(this.imageServer.getDownloadUrl(imageReference),is("http://anyurl.com/yemmaGouraya"));
	verify(urlGenerator);

    }

    /**
     * FIXME: for now, we cannot upload pictures, so the test relies on having the user manually upload
     * yemma gouraya's picture
     * @throws IOException
     */
    @Test
    public void shouldDownloadYemmaGourayaPicture() throws IOException {
	InputStreamSource source = imageServer.downloadImage(yemmaGouraya());
	assertNotNull(source);
	InputStream is = source.getInputStream();
	assertNotNull(is);
	assertTrue(isYemmaGourayaPicture(is));
	assertFalse(isCornicheKabylePicture(is));
	IOUtils.closeQuietly(is);
    }

    private boolean isYemmaGourayaPicture(InputStream is) throws IOException {
	InputStream yemmaGouraya = yemmaGourayaPictureStream().getInputStream();
	boolean b = IOUtils.contentEquals(yemmaGouraya, is);
	IOUtils.closeQuietly(yemmaGouraya);
	return b;
    }
    
    private boolean isCornicheKabylePicture(InputStream is) throws IOException {
	InputStream cornicheKabyle = PictureStreamSourceObjectMother.cornicheKabylePictureStream().getInputStream();
	boolean b = IOUtils.contentEquals(cornicheKabyle, is);
	IOUtils.closeQuietly(cornicheKabyle);
	return b;
    }
    
    

    /*
     * 
     should be moved to imgserver-interface-tests
     @Test
    public void shouldThrowResourceNotExistingExceptionWhenResourceNotFound() throws IOException {
	ImageReference imageReference = originalImage("anyImageThatNobodyHasEverUploadedOnThisPlanet");
	try {
	    InputStreamSource source = imageServer.downloadImage(imageReference);
	    source.getInputStream();
	    Assert.fail();
	} catch(ResourceNotExistingException e) {
	    assertEquals(imageReference, e.getImageReference());
	} 
	
    }
    
    should be moved to imgserver-stress-tests
    @Test
    public void shouldDownloadImageThousandTimes() throws IOException {
	for (int i = 0; i < 1000; i++) {
	    InputStreamSource source = imageServer.downloadImage(yemmaGouraya());
	    assertNotNull(source);
	    InputStream is = source.getInputStream();
	    assertNotNull(is);
	    assertTrue(isYemmaGourayaPicture(is));
	    assertFalse(isCornicheKabylePicture(is));
	    IOUtils.closeQuietly(is);
	}

    }*/
}
