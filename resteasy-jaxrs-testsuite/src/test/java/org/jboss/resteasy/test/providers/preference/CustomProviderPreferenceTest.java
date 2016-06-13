package org.jboss.resteasy.test.providers.preference;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.InMemoryClientExecutor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.providers.CustomValueInjectorTest.HelloResource;
import org.jboss.resteasy.test.providers.CustomValueInjectorTest.MyInjectorFactoryImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 *
 */
public class CustomProviderPreferenceTest {

   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      dispatcher.getProviderFactory().registerProvider(MyInjectorFactoryImpl.class);
      dispatcher.getRegistry().addPerRequestResource(HelloResource.class);
   }
   
   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }
   
    @Test
    public void testCustomProviderPreference() throws Exception {
       
        dispatcher.getRegistry().addPerRequestResource(UserResource.class);
        dispatcher.getProviderFactory().registerProvider(UserBodyWriter.class);
        Response result = ClientBuilder.newClient().target("http://localhost:8081/user").request().get();
        
        Assert.assertEquals(200, result.getStatus());
        Assert.assertEquals("jharting;email@example.com", result.readEntity(String.class));
    }
    
    @Test
    @Ignore
    public void testApplicationProvidedLessSpecificWriterOverBuiltinStringWriter() throws Exception {

        dispatcher.getRegistry().addPerRequestResource(StringResource.class);
        dispatcher.getProviderFactory().registerProvider(GeneralWriter.class);
        Response result = ClientBuilder.newClient().target("http://localhost:8081/test").request().get();
        
        Assert.assertEquals(200, result.getStatus());
        Assert.assertEquals("The resource returned: Hello world!", result.readEntity(String.class));
    }
}
