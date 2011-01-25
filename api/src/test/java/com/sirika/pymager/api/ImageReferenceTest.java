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
import static com.sirika.pymager.api.ImageFormat.PNG;
import static com.sirika.pymager.api.ImageReference.originalImage;
import static com.sirika.pymager.api.ImageScale.width;
import static com.sirika.pymager.api.testhelpers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.pymager.api.testhelpers.ImageReferenceObjectMother.yemmaGourayaResizedTo300x200InDefaultFormat;
import static com.sirika.pymager.api.testhelpers.ImageReferenceObjectMother.yemmaGourayaResizedTo300x200InPng;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sirika.pymager.api.ImageReference;

public class ImageReferenceTest {

    @Test(expected = IllegalArgumentException.class)
    public void idShouldBeMandatory() {
        originalImage((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rescalingIsMandatoryBeforeConversion() {
        originalImage("yemmaGouraya").convertedTo(JPEG);
    }

    /**
     * this is typically a limitation of the image service, but in the
     * meanwhile, we need to make sure we alert the user that he won't get
     * whatever format he asked for
     */
    @Test(expected = IllegalArgumentException.class)
    public void formatShouldBeMandatoryWhenRescaling() {
        originalImage("yemmaGouraya").rescaledTo(width(800).by(600))
                .convertedTo(null);
    }

    @Test
    public void shouldCreateOriginalImageReference() {
        ImageReference imageReference = yemmaGouraya();
        assertThat(imageReference.getId().toString(), is("yemmaGouraya"));
        assertThat(imageReference.isDerived(), is(false));
        assertThat(imageReference.isConverted(), is(false));
    }

    @Test
    public void shouldCreateDerivedImageReference() {
        ImageReference imageReference = yemmaGourayaResizedTo300x200InPng();
        assertThat(imageReference.getId().toString(), is("yemmaGouraya"));
        assertThat(imageReference.isDerived(), is(true));
        assertThat(imageReference.isConverted(), is(true));
        assertThat(imageReference.getImageFormat(), is(PNG));
        assertThat(imageReference.getRescaling(), is(width(300).by(200)));
    }

    @Test
    public void originalParentReferenceOfOriginalImageReferenceShouldBeItself() {
        ImageReference imageReference = yemmaGouraya();
        assertThat(imageReference.getOriginalParentReference(),
                is(sameInstance(imageReference)));
    }

    @Test
    public void shouldReturnOriginalParentReferenceOfDerivedImageReference() {
        assertThat(yemmaGourayaResizedTo300x200InPng()
                .getOriginalParentReference(), is((yemmaGouraya())));
    }

    @Test
    public void defaultFormatShouldBeJpeg() {
        ImageReference imageReference = yemmaGourayaResizedTo300x200InDefaultFormat();
        assertThat(imageReference.getImageFormat(), is(JPEG));
    }

    @Test
    public void originalImageReferencesShouldBeEqual() {
        ImageReference yemmaGouraya1 = yemmaGouraya();
        ImageReference yemmaGouraya2 = yemmaGouraya();
        assertThat(yemmaGouraya1, equalTo(yemmaGouraya2));
        assertThat(yemmaGouraya1.hashCode(), is(yemmaGouraya2.hashCode()));
    }

    @Test
    public void originalImageReferencesShouldNotBeEqualBecauseOfId() {
        ImageReference yemmaGouraya1 = originalImage("id1");
        ImageReference yemmaGouraya2 = originalImage("id2");
        assertThat(yemmaGouraya1, is(not(equalTo(yemmaGouraya2))));
        assertThat(yemmaGouraya1.hashCode(), is(not(yemmaGouraya2.hashCode())));
    }

    @Test
    public void derivedImageReferencesShouldBeEqual() {
        ImageReference yemmaGouraya1 = yemmaGourayaResizedTo300x200InPng();
        ImageReference yemmaGouraya2 = yemmaGourayaResizedTo300x200InPng();
        assertThat(yemmaGouraya1, equalTo(yemmaGouraya2));
        assertThat(yemmaGouraya1.hashCode(), is(yemmaGouraya2.hashCode()));
    }

    @Test
    public void derivedImageReferencesShouldNotBeEqualBecauseOfId() {
        ImageReference reference1 = originalImage("id").rescaledTo(
                width(800).by(600)).convertedTo(PNG);
        ImageReference reference2 = originalImage("id2").rescaledTo(
                width(800).by(600)).convertedTo(PNG);
        assertThat(reference1, is(not(equalTo(reference2))));
        assertThat(reference1.hashCode(), is(not(reference2.hashCode())));
    }

    @Test
    public void derivedImageReferencesShouldNotBeEqualBecauseOfScale() {
        ImageReference reference1 = originalImage("id").rescaledTo(
                width(800).by(600)).convertedTo(PNG);
        ImageReference reference2 = originalImage("id").rescaledTo(
                width(801).by(600)).convertedTo(PNG);
        assertThat(reference1, is(not(equalTo(reference2))));
        assertThat(reference1.hashCode(), is(not(reference2.hashCode())));
    }

    @Test
    public void derivedImageReferencesShouldNotBeEqualBecauseOfFormat() {
        ImageReference reference1 = originalImage("id").rescaledTo(
                width(800).by(600)).convertedTo(PNG);
        ImageReference reference2 = originalImage("id").rescaledTo(
                width(800).by(600)).convertedTo(JPEG);
        assertThat(reference1, is(not(equalTo(reference2))));
        assertThat(reference1.hashCode(), is(not(reference2.hashCode())));
    }

}
