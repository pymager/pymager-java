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

import static com.sirika.pymager.api.ImageScale.width;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sirika.pymager.api.ImageScale;

public class ImageScaleTest {

    @Test
    public void shouldCreate800x600ImageScale() {
        ImageScale imageScale = width(800).by(600);
        assertThat(imageScale.getWidth(), is(800));
        assertThat(imageScale.getHeight(), is(600));
    }

    @Test(expected = IllegalArgumentException.class)
    public void widthMustBePositive() {
        width(-800).by(600);
    }

    @Test(expected = IllegalArgumentException.class)
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
