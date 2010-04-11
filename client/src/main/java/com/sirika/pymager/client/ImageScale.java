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
package com.sirika.pymager.client;

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
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException(
                    "Width and height must be positive");
        }
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
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + width;
        return result;
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
        if (height != other.height)
            return false;
        if (width != other.width)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("%sx%s", width, height);
    }

}
