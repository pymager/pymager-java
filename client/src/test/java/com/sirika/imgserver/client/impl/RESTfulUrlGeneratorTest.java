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
package com.sirika.imgserver.client.impl;

import static com.sirika.imgserver.client.testhelpers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.imgserver.client.testhelpers.ImageReferenceObjectMother.yemmaGourayaResizedTo300x200InPng;
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
