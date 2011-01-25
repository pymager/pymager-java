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

public class UnknownUploadFailureException extends ImageServerException {
    private static final long serialVersionUID = 1L;

    private ImageId imageId;
    private ImageFormat imageFormat;

    public UnknownUploadFailureException(ImageId imageId,
            ImageFormat imageFormat, Exception e) {
        super(e);
        this.imageId = imageId;
        this.imageFormat = imageFormat;
    }

    public ImageId getImageId() {
        return imageId;
    }

    public ImageFormat getImageFormat() {
        return imageFormat;
    }

}
