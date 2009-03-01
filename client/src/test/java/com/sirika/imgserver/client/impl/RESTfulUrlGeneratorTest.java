package com.sirika.imgserver.client.impl;

import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGourayaResizedTo300x200InPng;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.sirika.imgserver.client.UrlGenerator;


public class RESTfulUrlGeneratorTest {

    private UrlGenerator urlGenerator;
    
    @Before
    public void onSetUp() {
	this.urlGenerator = new RESTfulUrlGenerator("http://localhost:8000");
    }
    
    @Test
    public void shouldGeneratUrlForOriginalImage() {
	assertThat(urlGenerator.getImageResourceUrl(yemmaGouraya()), is("http://localhost:8000/original/yemmaGouraya"));
    }
    
    @Test
    public void shouldGeneratUrlForDerivedImage() {
	assertThat(urlGenerator.getImageResourceUrl(yemmaGourayaResizedTo300x200InPng()), is("http://localhost:8000/derived/yemmaGouraya-300x200.png"));
    }
}
