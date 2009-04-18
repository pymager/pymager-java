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
package com.sirika.imgserver.httpclienthelpers;

import static com.sirika.imgserver.httpclienthelpers.DefaultHttpClientFactory.defaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.FactoryBean;
/**
 * Spring {@link FactoryBean} helper that eases the creation and configuration of HttpComponents' {@link HttpClient}.
 * Configuring HttpClient authentication, number of connections, ... cannot be done declaratively using HttpClient's 
 * native mechanisms. {@link HttpClientFactoryBean}'s goal is to expose these settings so they can be changed declaratively 
 * using Spring.
 * 
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
public class HttpClientFactoryBean implements FactoryBean {
    static class GzipDecompressingEntity extends HttpEntityWrapper {

        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }
    
        @Override
        public InputStream getContent()
            throws IOException, IllegalStateException {

            // the wrapped entity's getContent() decides about repeatability
            InputStream wrappedin = wrappedEntity.getContent();

            return new GZIPInputStream(wrappedin);
        }

        @Override
        public long getContentLength() {
            // length of ungzipped content is not known
            return -1;
        }

    } 

    private Map<AuthScope, Credentials> credentials = new HashMap<AuthScope, Credentials>();
    private Map<String,Object> params = new HashMap<String, Object>();
    private CookieStore cookieStore = null;
    private boolean shouldUseCookieStore = false;
    private boolean shouldUseGzipContentcompression = true;
    
    public Object getObject() throws Exception {
	DefaultHttpClient httpClient = defaultHttpClient();
	for(Entry<AuthScope, Credentials> e : credentials.entrySet()) {
	    httpClient.getCredentialsProvider().setCredentials(e.getKey(), e.getValue());    
	}
	
	for(Entry<String, Object> e : params.entrySet()) {
	    httpClient.getParams().setParameter(e.getKey(), e.getValue());
	}
	
	if(shouldUseCookieStore == true) {
	    httpClient.setCookieStore(cookieStore == null? new BasicCookieStore() : cookieStore);
	}
	
	if(shouldUseGzipContentcompression) {
	    handleGzipContentCompression(httpClient);

	}
	
	return httpClient;
    }

    private void handleGzipContentCompression(DefaultHttpClient httpClient) {
	httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
	    public void process(HttpRequest request, HttpContext context)
		    throws HttpException, IOException {
		if (!request.containsHeader("Accept-Encoding")) {
	                request.addHeader("Accept-Encoding", "gzip");
	            }
	    }

	    });
	    
	httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
	public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
	    HttpEntity entity = response.getEntity();
	    Header ceheader = entity.getContentEncoding();
	    if (ceheader != null) {
		HeaderElement[] codecs = ceheader.getElements();
		for (int i = 0; i < codecs.length; i++) {
		    if (codecs[i].getName().equalsIgnoreCase("gzip")) {
			response.setEntity(new GzipDecompressingEntity(response.getEntity())); 
			return;
		    }
		}
	    }
	}
	});
    }

    public Class getObjectType() {
	return HttpClient.class;
    }

    public boolean isSingleton() {
	return true;
    }

    public void setCredentials(Map<AuthScope, Credentials> credentials) {
        this.credentials = credentials;
    }

    public void setCookieStore(CookieStore cookieStore) {
	this.shouldUseCookieStore = true;
        this.cookieStore = cookieStore;
    }

    public void setShouldUseCookieStore(boolean shouldUseCookieStore) {
        this.shouldUseCookieStore = shouldUseCookieStore;
    }

    public void setShouldUseGzipContentcompression(boolean shouldUseGzipContentcompression) {
        this.shouldUseGzipContentcompression = shouldUseGzipContentcompression;
    }

}
