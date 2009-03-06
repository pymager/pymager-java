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

import org.springframework.core.io.InputStreamSource;

/**
 * Represents the Image Server, and the operations we can call on it.
 * This is the starting point of Image Server's client.
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
public interface ImageServer extends UrlGenerator{
    InputStreamSource downloadImage(ImageReference imageReference) throws ResourceNotExistingException, UnknownDownloadFailureException;
    ImageReference uploadImage(ImageId id, ImageFormat imageFormat, InputStreamSource imageSource) throws UnknownUploadFailureException;
    void deleteImage(ImageId imageId) throws UnknownDeleteFailureException;
    void destroy() throws Exception;
}
