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

import static com.sirika.pymager.api.ImageId.imageId;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sirika.pymager.api.ImageId;

public class ImageIdTest {

    @Test(expected = IllegalArgumentException.class)
    public void idShouldBeMandatory() {
        imageId(null);
    }

    @Test
    public void shouldCreateImageId() {
        ImageId imageId = imageId("britney");
        assertThat(imageId.toString(), is("britney"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowSlashCharacterInId() {
        imageId("my/id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowUnicodeCharacters() {
        imageId("myéèàid");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowHyphen() {
        imageId("my-id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowUnderscore() {
        imageId("my_id");
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
