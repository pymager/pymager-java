package com.sirika.imgserver.client;

import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.imgserver.client.objectmothers.PictureStreamAssertionUtils.isCornicheKabylePicture;
import static com.sirika.imgserver.client.objectmothers.PictureStreamAssertionUtils.isYemmaGourayaPicture;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.impl.ImageServerImpl;

public class ImageServerTest {

    private ImageServerImpl imageServer;

    @Before
    public void setup() {
	this.imageServer = new ImageServerImpl("http://localhost:8000");
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
	assertTrue(isYemmaGourayaPicture(source));
	assertFalse(isCornicheKabylePicture(source));

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
