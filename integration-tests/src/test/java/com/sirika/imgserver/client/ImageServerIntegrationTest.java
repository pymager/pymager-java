package com.sirika.imgserver.client;

import static com.sirika.imgserver.client.ImageFormat.JPEG;
import static com.sirika.imgserver.client.ImageId.imageId;
import static com.sirika.imgserver.client.ImageReference.originalImage;
import static com.sirika.imgserver.client.objectmothers.ImageIdObjectMother.cornicheKabyleId;
import static com.sirika.imgserver.client.objectmothers.ImageIdObjectMother.yemmaGourayaId;
import static com.sirika.imgserver.client.objectmothers.PictureStreamAssertionUtils.isCornicheKabylePicture;
import static com.sirika.imgserver.client.objectmothers.PictureStreamAssertionUtils.isYemmaGourayaPicture;
import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.yemmaGourayaPictureStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.impl.ImageServerImpl;
import com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother;

public class ImageServerIntegrationTest {

    private static final String IMGSERVER_LOCATION = "http://localhost:8000";
    private ImageServerImpl imageServer;

    @Before
    public void setup() {
	this.imageServer = new ImageServerImpl(IMGSERVER_LOCATION);
    }

   
    @Test
    @Ignore
    public void shouldUploadAndDownloadYemmaGourayaPicture() throws IOException {
	ImageReference yemmaGouraya = imageServer.uploadImage(yemmaGourayaId(), JPEG, yemmaGourayaPictureStream());
	InputStreamSource source = imageServer.downloadImage(yemmaGouraya);
	assertNotNull(source);
	assertTrue(isYemmaGourayaPicture(source));
	assertFalse(isCornicheKabylePicture(source));
	imageServer.deleteImage(imageId("yemmaGouraya"));
    }
    
    @Test
    @Ignore
    public void shouldUploadSeveralPicturesAndDownloadCornicheKabylePicture() throws IOException {
	imageServer.uploadImage(yemmaGourayaId(), JPEG, yemmaGourayaPictureStream());
	ImageReference cornicheKabyle = imageServer.uploadImage(cornicheKabyleId(), JPEG, PictureStreamSourceObjectMother.cornicheKabylePictureStream());
	InputStreamSource source = imageServer.downloadImage(cornicheKabyle);
	assertNotNull(source);
	assertFalse(isYemmaGourayaPicture(source));
	assertTrue(isCornicheKabylePicture(source));
	imageServer.deleteImage(yemmaGourayaId());
	imageServer.deleteImage(cornicheKabyleId());
    }
    
    @Test
    public void shouldThrowResourceNotExistingExceptionWhenResourceNotFound() throws IOException {
	ImageReference imageReference = originalImage("anyImageThatNobodyHasEverUploadedOnThisPlanet");
	try {
	    InputStreamSource source = imageServer.downloadImage(imageReference);
	    source.getInputStream();
	    fail();
	} catch(ResourceNotExistingException e) {
	    assertEquals(imageReference, e.getImageReference());
	} 
	
    }
    /**
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
