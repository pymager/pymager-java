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
import static com.sirika.imgserver.client.ImageReference.originalImage;
import static com.sirika.imgserver.client.ImageScale.width;
import static com.sirika.imgserver.client.objectmothers.ImageIdObjectMother.cornicheKabyleId;
import static com.sirika.imgserver.client.objectmothers.ImageIdObjectMother.yemmaGourayaId;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.cornicheKabyle;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.imgserver.client.objectmothers.PictureStreamAssertionUtils.is100x100CornicheKabylePicture;
import static com.sirika.imgserver.client.objectmothers.PictureStreamAssertionUtils.isCornicheKabylePicture;
import static com.sirika.imgserver.client.objectmothers.PictureStreamAssertionUtils.isYemmaGourayaPicture;
import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.yemmaGourayaOriginalPictureStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother;

public class ImageServerStressTest extends AbstractImageServerIntegrationTestCase {

    @Before
    public void setup() throws IOException {
	initialFailproofCleanup();
    }

    private void initialFailproofCleanup() throws IOException {
	for(ImageReference imageReference : Arrays.asList(yemmaGouraya(), cornicheKabyle())) {
	    try {
		InputStreamSource source = imageServer.downloadImage(imageReference);
		source.getInputStream();
		imageServer.deleteImage(imageReference.getId());
	    } catch(ResourceNotExistingException e) {
		// do nothing, it's fine
	    }
	}
    }
    
    @Test
    public void doIt(){
	
    }
  /*
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
    
    @Test
    public void shouldUploadAndDownloadOriginalYemmaGourayaPicture() throws IOException {
	ImageReference yemmaGouraya = imageServer.uploadImage(yemmaGourayaId(), JPEG, yemmaGourayaOriginalPictureStream());
	InputStreamSource source = imageServer.downloadImage(yemmaGouraya);
	assertNotNull(source);
	assertTrue(isYemmaGourayaPicture(source));
	assertFalse(isCornicheKabylePicture(source));
	imageServer.deleteImage(yemmaGourayaId());
    }
    
    @Test(expected=ResourceNotExistingException.class)
    public void shouldNotDownloadDeletedYemmaGourayaPicture() throws IOException {
	imageServer.uploadImage(yemmaGourayaId(), JPEG, yemmaGourayaOriginalPictureStream());
	imageServer.deleteImage(yemmaGourayaId());
	ImageReference imageReference = yemmaGouraya();
	InputStreamSource source = imageServer.downloadImage(imageReference);
	source.getInputStream();
    }
    
    @Test
    public void shouldUploadSeveralPicturesAndDownloadCornicheKabylePicture() throws IOException {
	imageServer.uploadImage(yemmaGourayaId(), JPEG, yemmaGourayaOriginalPictureStream());
	ImageReference cornicheKabyle = imageServer.uploadImage(cornicheKabyleId(), JPEG, PictureStreamSourceObjectMother.cornicheKabyleOriginalPictureStream());
	InputStreamSource source = imageServer.downloadImage(cornicheKabyle);
	assertNotNull(source);
	assertFalse(isYemmaGourayaPicture(source));
	assertTrue(isCornicheKabylePicture(source));
	imageServer.deleteImage(yemmaGourayaId());
	imageServer.deleteImage(cornicheKabyleId());
    }
    @Test
    public void shouldUploadYemmaGourayaAndDownloadDerivedPicture() throws IOException {
	ImageReference yemmaGouraya = imageServer.uploadImage(yemmaGourayaId(), JPEG, yemmaGourayaOriginalPictureStream());
	InputStreamSource source = imageServer.downloadImage(yemmaGouraya.rescaledTo(width(100).by(100)).convertedTo(JPEG));
	assertNotNull(source);
	assertTrue(is100x100CornicheKabylePicture(source));
	assertFalse(isCornicheKabylePicture(source));
	imageServer.deleteImage(yemmaGourayaId());
    }*/
}
