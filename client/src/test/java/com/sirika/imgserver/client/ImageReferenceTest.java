package com.sirika.imgserver.client;

import static com.sirika.imgserver.client.ImageFormat.JPEG;
import static com.sirika.imgserver.client.ImageFormat.PNG;
import static com.sirika.imgserver.client.ImageReference.originalImage;
import static com.sirika.imgserver.client.ImageScale.width;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.britneySpearsOriginal;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.britneySpearsResizedTo300x200InDefaultFormat;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.britneySpearsResizedTo300x200InPng;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class ImageReferenceTest {

    @Test(expected=IllegalArgumentException.class)
    public void idShouldBeMandatory() {
	originalImage(null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void rescalingIsMandatoryBeforeConversion() {
	originalImage("id").convertedTo(JPEG);
    }
    
    /**
     * this is typically a limitation of the image service, but in the meanwhile, 
     * we need to make sure we alert the user that he won't get whatever format
     * he asked for
     */
    @Test(expected=IllegalArgumentException.class)
    public void formatShouldBeMandatoryWhenRescaling() {
	originalImage("id").rescaledTo(width(800).by(600)).convertedTo(null);
    }
    
    @Test
    public void shouldCreateOriginalImageReference() {
	ImageReference imageReference = britneySpearsOriginal();
	assertThat(imageReference.getId(), is("britney"));
	assertThat(imageReference.isDerived(), is(false));
	assertThat(imageReference.isConverted(), is(false));
    }
    
    @Test
    public void shouldCreateDerivedImageReference() {
	ImageReference imageReference = britneySpearsResizedTo300x200InPng();
	assertThat(imageReference.getId(), is("britney"));
	assertThat(imageReference.isDerived(), is(true));
	assertThat(imageReference.isConverted(), is(true));
	assertThat(imageReference.getImageFormat(), is(PNG));
	assertThat(imageReference.getRescaling(), is(width(300).by(200)));
    }
    
    @Test
    public void originalParentReferenceOfOriginalImageReferenceShouldBeItself() {
	ImageReference imageReference = britneySpearsOriginal();
	assertThat(imageReference.getOriginalParentReference(), is(sameInstance(imageReference)));
    }
    
    @Test
    public void shouldReturnOriginalParentReferenceOfDerivedImageReference() {
	assertThat(britneySpearsResizedTo300x200InPng().getOriginalParentReference(), is((britneySpearsOriginal())));
    }

    @Test
    public void defaultFormatShouldBeJpeg() {
	ImageReference imageReference = britneySpearsResizedTo300x200InDefaultFormat();
	assertThat(imageReference.getImageFormat(), is(JPEG));
    }
    
    @Test
    public void originalImageReferencesShouldBeEqual() {
	ImageReference britney1 = britneySpearsOriginal();
	ImageReference britney2 = britneySpearsOriginal();
	assertThat(britney1, equalTo(britney2));
	assertThat(britney1.hashCode(), is(britney2.hashCode()));
    }
    
    @Test
    public void originalImageReferencesShouldNotBeEqualBecauseOfId() {
	ImageReference britney1 = originalImage("id1");
	ImageReference britney2 = originalImage("id2");
	assertThat(britney1, is(not(equalTo(britney2))));
	assertThat(britney1.hashCode(), is(not(britney2.hashCode())));
    }
    
    @Test
    public void derivedImageReferencesShouldBeEqual() {
	ImageReference britney1 = britneySpearsResizedTo300x200InPng();
	ImageReference britney2 = britneySpearsResizedTo300x200InPng();
	assertThat(britney1, equalTo(britney2));
	assertThat(britney1.hashCode(), is(britney2.hashCode()));
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
