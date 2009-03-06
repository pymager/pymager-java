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
package org.apache.http.localserver;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;



/**
 * A handler that echos the incoming request entity.
 * 
 * @author <a href="mailto:rolandw at apache.org">Roland Weber</a>
 * @author <a href="mailto:oleg at ural.ru">Oleg Kalnichevski</a>
 *
 *
 * <!-- empty lines to avoid 'svn diff' problems -->
 * @version $Revision: 652950 $
 */
public class EchoHandler
    implements HttpRequestHandler {

    // public default constructor

    /**
     * Handles a request by echoing the incoming request entity.
     * If there is no request entity, an empty document is returned.
     *
     * @param request   the request
     * @param response  the response
     * @param context   the context
     *
     * @throws HttpException    in case of a problem
     * @throws IOException      in case of an IO problem
     */
    public void handle(final HttpRequest request, 
                       final HttpResponse response,
                       final HttpContext context)
        throws HttpException, IOException {

        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if (!"GET".equals(method) &&
            !"POST".equals(method) &&
            !"PUT".equals(method)
            ) {
            throw new MethodNotSupportedException
                (method + " not supported by " + getClass().getName());
        }

        HttpEntity entity = null;
        if (request instanceof HttpEntityEnclosingRequest)
            entity = ((HttpEntityEnclosingRequest)request).getEntity();

        // For some reason, just putting the incoming entity into
        // the response will not work. We have to buffer the message.
        byte[] data;
        if (entity == null) {
            data = new byte [0];
        } else {
            data = EntityUtils.toByteArray(entity);
        }

        ByteArrayEntity bae = new ByteArrayEntity(data);
        if (entity != null) {
            bae.setContentType(entity.getContentType());
        }
        entity = bae;

        response.setStatusCode(HttpStatus.SC_OK);
        response.setEntity(entity);

    } // handle


} // class EchoHandler
