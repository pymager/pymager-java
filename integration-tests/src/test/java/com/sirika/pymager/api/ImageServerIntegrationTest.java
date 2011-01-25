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
package com.sirika.pymager.api;

import static com.sirika.pymager.api.ImageFormat.JPEG;
import static com.sirika.pymager.api.ImageId.imageId;
import static com.sirika.pymager.api.ImageReference.originalImage;
import static com.sirika.pymager.api.ImageScale.width;
import static com.sirika.pymager.api.testhelpers.ImageIdObjectMother.cornicheKabyleId;
import static com.sirika.pymager.api.testhelpers.ImageIdObjectMother.yemmaGourayaId;
import static com.sirika.pymager.api.testhelpers.ImageReferenceObjectMother.cornicheKabyle;
import static com.sirika.pymager.api.testhelpers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.pymager.api.testhelpers.PictureStreamAssertionUtils.is100x100CornicheKabylePicture;
import static com.sirika.pymager.api.testhelpers.PictureStreamAssertionUtils.isCornicheKabylePicture;
import static com.sirika.pymager.api.testhelpers.PictureStreamAssertionUtils.isYemmaGourayaPicture;
import static com.sirika.pymager.api.testhelpers.PictureStreamSourceObjectMother.textfileresource;
import static com.sirika.pymager.api.testhelpers.PictureStreamSourceObjectMother.yemmaGourayaOriginalPictureStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.InputStreamSource;

import com.sirika.pymager.api.BadUploadRequestException;
import com.sirika.pymager.api.ForbiddenRequestException;
import com.sirika.pymager.api.ImageAlreadyExistsException;
import com.sirika.pymager.api.ImageId;
import com.sirika.pymager.api.ImageReference;
import com.sirika.pymager.api.ResourceNotExistingException;
import com.sirika.pymager.api.testhelpers.PictureStreamSourceObjectMother;

public class ImageServerIntegrationTest extends
        AbstractImageServerIntegrationTestCase {

    @Before
    @After
    public void clean() throws IOException {
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
    public void shouldThrowResourceNotExistingExceptionWhenResourceNotFound()
            throws IOException {
        ImageReference imageReference = originalImage("anyImageThatNobodyHasEverUploadedOnThisPlanet");
        try {
            InputStreamSource source = imageServer
                    .downloadImage(imageReference);
            source.getInputStream();
            fail();
        } catch (ResourceNotExistingException e) {
            assertEquals(imageReference, e.getImageReference());
        }
    }

    @Test
    public void shouldThrowBadUploadRequestExceptionWhenUploadingGarbage()
            throws IOException {
        ImageId imageId = imageId("anImageThatIsNeverGoingToBeUploaded");
        try {
            imageServer.uploadImage(imageId, JPEG, textfileresource());
            fail();
        } catch (BadUploadRequestException e) {
            assertEquals(imageId, e.getImageId());
            assertEquals(JPEG, e.getImageFormat());
        }
    }

    @Test
    public void shouldThrowImageIdAlreadyExistsExceptionWhenUploadingSameImageTwice()
            throws IOException {
        ImageId imageId = yemmaGourayaId();
        imageServer.uploadImage(imageId, JPEG,
                yemmaGourayaOriginalPictureStream());
        try {
            imageServer.uploadImage(imageId, JPEG,
                    yemmaGourayaOriginalPictureStream());
            fail();
        } catch (ImageAlreadyExistsException e) {
            assertEquals(imageId, e.getImageId());
            assertEquals(JPEG, e.getImageFormat());
        }
    }

    @Test
    public void shouldUploadAndDownloadOriginalYemmaGourayaPicture()
            throws IOException {
        ImageReference yemmaGouraya = imageServer.uploadImage(yemmaGourayaId(),
                JPEG, yemmaGourayaOriginalPictureStream());
        InputStreamSource source = imageServer.downloadImage(yemmaGouraya);
        assertNotNull(source);
        assertTrue(isYemmaGourayaPicture(source));
        assertFalse(isCornicheKabylePicture(source));
    }

    @Test(expected = ResourceNotExistingException.class)
    public void shouldNotDownloadDeletedYemmaGourayaPicture()
            throws IOException {
        imageServer.uploadImage(yemmaGourayaId(), JPEG,
                yemmaGourayaOriginalPictureStream());
        imageServer.deleteImage(yemmaGourayaId());
        ImageReference imageReference = yemmaGouraya();
        InputStreamSource source = imageServer.downloadImage(imageReference);
        source.getInputStream();
    }

    @Test
    public void shouldUploadSeveralPicturesAndDownloadCornicheKabylePicture()
            throws IOException {
        imageServer.uploadImage(yemmaGourayaId(), JPEG,
                yemmaGourayaOriginalPictureStream());
        ImageReference cornicheKabyle = imageServer.uploadImage(
                cornicheKabyleId(), JPEG, PictureStreamSourceObjectMother
                        .cornicheKabyleOriginalPictureStream());
        InputStreamSource source = imageServer.downloadImage(cornicheKabyle);
        assertNotNull(source);
        assertFalse(isYemmaGourayaPicture(source));
        assertTrue(isCornicheKabylePicture(source));
    }

    @Test
    public void shouldUploadYemmaGourayaAndDownloadDerivedPicture()
            throws IOException {
        ImageReference yemmaGouraya = imageServer.uploadImage(yemmaGourayaId(),
                JPEG, yemmaGourayaOriginalPictureStream());
        InputStreamSource source = imageServer.downloadImage(yemmaGouraya
                .rescaledTo(width(100).by(100)).convertedTo(JPEG));
        assertNotNull(source);
        assertTrue(is100x100CornicheKabylePicture(source));
        assertFalse(isCornicheKabylePicture(source));
    }

    @Test
    public void shouldNotDownloadDerivedPictureWhenRequestedSizeIsForbidden()
            throws IOException {
        ImageReference yemmaGouraya = imageServer.uploadImage(yemmaGourayaId(),
                JPEG, yemmaGourayaOriginalPictureStream());
        try {
            InputStreamSource source = imageServer.downloadImage(yemmaGouraya
                    .rescaledTo(width(10000).by(10000)).convertedTo(JPEG));
            source.getInputStream();
            fail();
        } catch (ForbiddenRequestException e) {
            // ok
        }
    }
}
