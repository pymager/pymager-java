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
import java.io.InputStream;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.InputSupplier;
import com.sirika.pymager.api.testhelpers.PictureStreamSourceObjectMother;

public class ImageServerIntegrationTest extends
        AbstractImageServerIntegrationTestCase {

    @Before
    @After
    public void clean() throws IOException {
        for (ImageReference imageReference : Arrays.asList(yemmaGouraya(), cornicheKabyle())) {
            try {
                InputSupplier<InputStream> source = imageServer.downloadImage(imageReference);
                source.getInput();
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
            InputSupplier<InputStream> source = imageServer.downloadImage(imageReference);
            source.getInput();
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
        InputSupplier<InputStream> source = imageServer.downloadImage(yemmaGouraya);
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
        InputSupplier<InputStream> source = imageServer.downloadImage(imageReference);
        source.getInput();
    }

    @Test
    public void shouldUploadSeveralPicturesAndDownloadCornicheKabylePicture()
            throws IOException {
        imageServer.uploadImage(yemmaGourayaId(), JPEG,
                yemmaGourayaOriginalPictureStream());
        ImageReference cornicheKabyle = imageServer.uploadImage(
                cornicheKabyleId(), JPEG, PictureStreamSourceObjectMother
                        .cornicheKabyleOriginalPictureStream());
        InputSupplier<InputStream> source = imageServer.downloadImage(cornicheKabyle);
        assertNotNull(source);
        assertFalse(isYemmaGourayaPicture(source));
        assertTrue(isCornicheKabylePicture(source));
    }

    @Test
    public void shouldUploadYemmaGourayaAndDownloadDerivedPicture()
            throws IOException {
        ImageReference yemmaGouraya = imageServer.uploadImage(yemmaGourayaId(),JPEG, yemmaGourayaOriginalPictureStream());
        InputSupplier<InputStream> source = imageServer.downloadImage(yemmaGouraya.rescaledTo(width(100).by(100)).convertedTo(JPEG));
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
            InputSupplier<InputStream> source = imageServer.downloadImage(yemmaGouraya.rescaledTo(width(10000).by(10000)).convertedTo(JPEG));
            source.getInput();
            fail();
        } catch (ForbiddenRequestException e) {
            // ok
        }
    }
}
