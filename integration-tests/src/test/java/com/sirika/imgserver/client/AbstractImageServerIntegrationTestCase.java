package com.sirika.imgserver.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.sirika.imgserver.client.impl.HttpImageServer;

@ContextConfiguration(locations = { "classpath:/com/sirika/imgserver/tests/integration/appcontext.xml"})
public abstract class AbstractImageServerIntegrationTestCase extends AbstractJUnit4SpringContextTests {

    @Autowired
    protected HttpImageServer imageServer;

    public AbstractImageServerIntegrationTestCase() {
	super();
    }

}