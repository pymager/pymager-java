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
package com.sirika.imgserver.client;

import static com.sirika.imgserver.client.ImageFormat.JPEG;
import static com.sirika.imgserver.client.ImageId.imageId;
import static com.sirika.imgserver.client.ImageReference.originalImage;
import static com.sirika.imgserver.client.objectmothers.ImageIdObjectMother.cornicheKabyleId;
import static com.sirika.imgserver.client.objectmothers.ImageIdObjectMother.yemmaGourayaId;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGouraya;
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

import com.sirika.imgserver.client.impl.HttpImageServer;
import com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother;
import com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother;

public class ImageServerIntegrationTest {

    private static final String IMGSERVER_LOCATION = "http://localhost:8000";
    private HttpImageServer imageServer;

    @Before
    public void setup() {
	this.imageServer = new HttpImageServer(IMGSERVER_LOCATION);
    }

   
    @Test
    public void shouldUploadAndDownloadYemmaGourayaPicture() throws IOException {
	ImageReference yemmaGouraya = imageServer.uploadImage(yemmaGourayaId(), JPEG, yemmaGourayaPictureStream());
	InputStreamSource source = imageServer.downloadImage(yemmaGouraya);
	assertNotNull(source);
	assertTrue(isYemmaGourayaPicture(source));
	assertFalse(isCornicheKabylePicture(source));
	imageServer.deleteImage(yemmaGourayaId());
    }
    
    @Test(expected=ResourceNotExistingException.class)
    public void shouldNotDownloadDeletedYemmaGourayaPicture() throws IOException {
	imageServer.uploadImage(yemmaGourayaId(), JPEG, yemmaGourayaPictureStream());
	imageServer.deleteImage(yemmaGourayaId());
	ImageReference imageReference = yemmaGouraya();
	InputStreamSource source = imageServer.downloadImage(imageReference);
	source.getInputStream();
	fail(); 
    }
    
    @Test
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
