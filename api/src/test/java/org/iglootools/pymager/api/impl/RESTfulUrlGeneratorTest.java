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
package org.iglootools.pymager.api.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.iglootools.pymager.api.testhelpers.ImageReferenceObjectMother.yemmaGouraya;
import static org.iglootools.pymager.api.testhelpers.ImageReferenceObjectMother.yemmaGourayaResizedTo300x200InPng;
import static org.junit.Assert.assertThat;

import org.iglootools.pymager.api.UrlGenerator;
import org.iglootools.pymager.api.internal.RESTfulUrlGenerator;
import org.junit.Before;
import org.junit.Test;


public class RESTfulUrlGeneratorTest {

    private UrlGenerator urlGenerator;

    @Before
    public void onSetUp() {
        this.urlGenerator = new RESTfulUrlGenerator("http://localhost:8000");
    }

    @Test
    public void shouldGeneratUrlForOriginalImage() {
        assertThat(urlGenerator.getImageResourceUrl(yemmaGouraya()),
                is("http://localhost:8000/original/yemmaGouraya"));
    }

    @Test
    public void shouldGeneratUrlForDerivedImage() {
        assertThat(urlGenerator
                .getImageResourceUrl(yemmaGourayaResizedTo300x200InPng()),
                is("http://localhost:8000/derived/yemmaGouraya-300x200.png"));
    }
}
