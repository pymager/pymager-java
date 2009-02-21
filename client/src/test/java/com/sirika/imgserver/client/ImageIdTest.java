package com.sirika.imgserver.client;



import static com.sirika.imgserver.client.ImageId.imageId;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ImageIdTest {

    @Test(expected=IllegalArgumentException.class)
    public void idShouldBeMandatory() {
	imageId(null);
    }
    
    @Test
    public void shouldCreateImageId() {
	ImageId imageId = imageId("britney");
	assertThat(imageId.toString(), is("britney"));
    }
    
    @Test
    public void shouldBeEqual() {
	ImageId britney1 = imageId("britney");
	ImageId britney2 = imageId("britney");
	assertThat(britney1, equalTo(britney2));
	assertThat(britney1.hashCode(), is(britney2.hashCode()));
    }
    
    @Test
    public void imageIdsCreatedWithDifferentIdsShouldNotBeEqual() {
	ImageId britney1 = imageId("britney1");
	ImageId britney2 = imageId("britney2");
	assertThat(britney1, is(not(equalTo(britney2))));
	assertThat(britney1.hashCode(), is(not(britney2.hashCode())));
    }
    
}
