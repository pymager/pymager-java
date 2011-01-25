/**
 * PyMager Java REST Client
 * Copyright (C) 2008 Sami Dalouche
 *
 * This file is part of PyMager Java REST Client.
 *
 * PyMager Java REST Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PyMager Java REST Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PyMager Java REST Client.  If not, see <http://www.gnu.org/licenses/>.
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
