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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.FactoryBean;
/**
 * Spring {@link FactoryBean} helper that eases the creation and configuration of HttpComponents' {@link HttpClient}.
 * Configuring HttpClient authentication, number of connections, ... cannot be done declaratively using HttpClient's 
 * native mechanisms. {@link HttpClientFactoryBean}'s goal is to expose these settings so they can be changed declaratively 
 * using Spring.
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
public class HttpClientFactoryBean implements FactoryBean {
    
    private Map<AuthScope, Credentials> credentials;
    
    public Object getObject() throws Exception {
	DefaultHttpClient httpClient = DefaultHttpClientFactory.defaultHttpClient();
	for(Entry<AuthScope, Credentials> e : credentials.entrySet()) {
	    httpClient.getCredentialsProvider().setCredentials(e.getKey(), e.getValue());    
	}
	return httpClient;
    }

    public Class getObjectType() {
	return HttpClient.class;
    }

    public boolean isSingleton() {
	return true;
    }

}
