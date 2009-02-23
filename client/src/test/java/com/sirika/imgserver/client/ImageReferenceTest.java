package com.sirika.imgserver.client;

import static com.sirika.imgserver.client.ImageFormat.JPEG;
import static com.sirika.imgserver.client.ImageFormat.PNG;
import static com.sirika.imgserver.client.ImageReference.originalImage;
import static com.sirika.imgserver.client.ImageScale.width;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGourayaResizedTo300x200InDefaultFormat;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGourayaResizedTo300x200InPng;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ImageReferenceTest {

    @Test(expected=IllegalArgumentException.class)
    public void idShouldBeMandatory() {
	originalImage(null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void rescalingIsMandatoryBeforeConversion() {
	originalImage("yemmaGouraya").convertedTo(JPEG);
    }
    
    /**
     * this is typically a limitation of the image service, but in the meanwhile, 
     * we need to make sure we alert the user that he won't get whatever format
     * he asked for
     */
    @Test(expected=IllegalArgumentException.class)
    public void formatShouldBeMandatoryWhenRescaling() {
	originalImage("yemmaGouraya").rescaledTo(width(800).by(600)).convertedTo(null);
    }
    
    @Test
    public void shouldCreateOriginalImageReference() {
	ImageReference imageReference = yemmaGouraya();
	assertThat(imageReference.getId(), is("yemmaGouraya"));
	assertThat(imageReference.isDerived(), is(false));
	assertThat(imageReference.isConverted(), is(false));
    }
    
    @Test
    public void shouldCreateDerivedImageReference() {
	ImageReference imageReference = yemmaGourayaResizedTo300x200InPng();
	assertThat(imageReference.getId(), is("yemmaGouraya"));
	assertThat(imageReference.isDerived(), is(true));
	assertThat(imageReference.isConverted(), is(true));
	assertThat(imageReference.getImageFormat(), is(PNG));
	assertThat(imageReference.getRescaling(), is(width(300).by(200)));
    }
    
    @Test
    public void originalParentReferenceOfOriginalImageReferenceShouldBeItself() {
	ImageReference imageReference = yemmaGouraya();
	assertThat(imageReference.getOriginalParentReference(), is(sameInstance(imageReference)));
    }
    
    @Test
    public void shouldReturnOriginalParentReferenceOfDerivedImageReference() {
	assertThat(yemmaGourayaResizedTo300x200InPng().getOriginalParentReference(), is((yemmaGouraya())));
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
	ImageReference reference1 = originalImage("id").rescaledTo(width(800).by(600)).convertedTo(PNG);
	ImageReference reference2 = originalImage("id2").rescaledTo(width(800).by(600)).convertedTo(PNG);
	assertThat(reference1, is(not(equalTo(reference2))));
	assertThat(reference1.hashCode(), is(not(reference2.hashCode())));
    }
    
    @Test
    public void derivedImageReferencesShouldNotBeEqualBecauseOfScale() {
	ImageReference reference1 = originalImage("id").rescaledTo(width(800).by(600)).convertedTo(PNG);
	ImageReference reference2 = originalImage("id").rescaledTo(width(801).by(600)).convertedTo(PNG);
	assertThat(reference1, is(not(equalTo(reference2))));
	assertThat(reference1.hashCode(), is(not(reference2.hashCode())));
    }
    
    @Test
    public void derivedImageReferencesShouldNotBeEqualBecauseOfFormat() {
	ImageReference reference1 = originalImage("id").rescaledTo(width(800).by(600)).convertedTo(PNG);
	ImageReference reference2 = originalImage("id").rescaledTo(width(800).by(600)).convertedTo(JPEG);
	assertThat(reference1, is(not(equalTo(reference2))));
	assertThat(reference1.hashCode(), is(not(reference2.hashCode())));
    }
    
}
