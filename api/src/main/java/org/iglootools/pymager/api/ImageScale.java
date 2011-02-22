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
package org.iglootools.pymager.api;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class ImageScale {
    public static class ImageScaleBuilder {
        private int width;

        public ImageScaleBuilder(int width) {
            super();
            this.width = width;
        }

        public ImageScale by(int height) {
            return new ImageScale(width, height);
        }
    }

    private int width;
    private int height;

    private ImageScale(int width, int height) {
        super();
        Preconditions.checkArgument(width >= 0 && height >= 0, "Width and height must be positive");
        
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static ImageScaleBuilder width(int width) {
        return new ImageScaleBuilder(width);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(width, height);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImageScale other = (ImageScale) obj;
        return Objects.equal(width, other.width)
            && Objects.equal(height, other.height);
    }

    @Override
    public String toString() {
        return String.format("%sx%s", width, height);
    }

}
