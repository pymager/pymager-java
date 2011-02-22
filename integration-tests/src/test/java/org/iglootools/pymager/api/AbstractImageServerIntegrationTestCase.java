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

import org.iglootools.pymager.api.impl.HttpImageServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration(locations = { "classpath:/org/iglootools/pymager/tests/integration/appcontext.xml" })
public abstract class AbstractImageServerIntegrationTestCase extends
        AbstractJUnit4SpringContextTests {

    @Autowired
    protected HttpImageServer imageServer;

    public AbstractImageServerIntegrationTestCase() {
        super();
    }

}