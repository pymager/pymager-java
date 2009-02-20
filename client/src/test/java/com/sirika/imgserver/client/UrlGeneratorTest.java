package com.sirika.imgserver.client;

import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.britneySpearsOriginal;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.britneySpearsResizedTo300x200InPng;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

public class UrlGeneratorTest {

    private UrlGenerator urlGenerator;
    
    @Before
    public void onSetUp() {
	this.urlGenerator = new UrlGenerator("http://localhost:8000");
    }
    
    @Test
    public void shouldGeneratUrlForOriginalImage() {
	ImageReference britney = britneySpearsOriginal();
	assertThat(urlGenerator.urlFor(britney), CoreMatchers.is("http://localhost:8000/original/britney"));
    }
    
    @Test
    public void shouldGeneratUrlForDerivedImage() {
	ImageReference britney = britneySpearsResizedTo300x200InPng();
	assertThat(urlGenerator.urlFor(britney), CoreMatchers.is("http://localhost:8000/derived/britney-300x200.png"));
    }
}
