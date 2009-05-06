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
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldNotAllowSlashCharacterInId() {
	imageId("my/id");
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
