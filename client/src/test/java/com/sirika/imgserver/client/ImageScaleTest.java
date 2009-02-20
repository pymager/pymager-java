package com.sirika.imgserver.client;

import static com.sirika.imgserver.client.ImageScale.width;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ImageScaleTest {

    @Test
    public void shouldCreate800x600ImageScale() {
	ImageScale imageScale = width(800).by(600);
	assertThat(imageScale.getWidth(), is(800));
	assertThat(imageScale.getHeight(), is(600));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void widthMustBePositive() {
	width(-800).by(600);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void heightMustBePositive() {
	width(800).by(-600);
    }
    
    @Test
    public void shouldBeEqual() {
	ImageScale imageScale1 = width(800).by(600);
	ImageScale imageScale2 = width(800).by(600);
	assertThat(imageScale1, equalTo(imageScale2));
	assertThat(imageScale1.hashCode(), is(imageScale2.hashCode()));
    }
    
    @Test
    public void shouldNotBeEqualBecauseOfWidth() {
	ImageScale imageScale1 = width(800).by(600);
	ImageScale imageScale2 = width(801).by(600);
	assertThat(imageScale1, not(equalTo(imageScale2)));
	assertThat(imageScale1.hashCode(), is(not(imageScale2.hashCode())));
    }
    
    @Test
    public void shouldNotBeEqualBecauseOfHeight() {
	ImageScale imageScale1 = width(800).by(600);
	ImageScale imageScale2 = width(800).by(601);
	assertThat(imageScale1, not(equalTo(imageScale2)));
	assertThat(imageScale1.hashCode(), is(not(imageScale2.hashCode())));
    }
    
}
