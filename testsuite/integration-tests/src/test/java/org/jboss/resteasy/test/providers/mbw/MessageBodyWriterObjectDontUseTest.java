package org.jboss.resteasy.test.providers.mbw;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.providers.mbw.resource.MessageBodyWriterObjectMessage;
import org.jboss.resteasy.test.providers.mbw.resource.MessageBodyWriterObjectMessageBodyWriter;
import org.jboss.resteasy.test.providers.mbw.resource.MessageBodyWriterObjectResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy MessageBodyWriter<Object>
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.1.4
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MessageBodyWriterObjectDontUseTest {

   static Client client;

   @BeforeClass
   public static void before() throws Exception {
      client = ClientBuilder.newClient();
      
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(MessageBodyWriterObjectDontUseTest.class.getSimpleName());
      war.addClasses(MessageBodyWriterObjectMessage.class);
      war.addAsWebInfResource(MessageBodyWriterObjectDontUseTest.class.getPackage(), "MessageBodyWriterObject_dont_use.xml", "web.xml");
      return TestUtil.finishContainerPrepare(war, null, MessageBodyWriterObjectResource.class, MessageBodyWriterObjectMessageBodyWriter.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, MessageBodyWriterObjectDontUseTest.class.getSimpleName());
   }

   @AfterClass
   public static void close() {
      client.close();
   }
   
   /**
    * @tpTestDetails Server is configured to not consider MessageBodyWriter<Object>'s when determining response
    *                entity media type.
    * @tpSince RESTEasy 3.1.4
    */
   @Test
   public void testDontUse() throws Exception {
      Invocation.Builder request = client.target(generateURL("/test")).request();
      Response response = request.get();
      Assert.assertEquals(500, response.getStatus());
      String entity = response.readEntity(String.class);
      Assert.assertTrue(entity.startsWith("Could not find MessageBodyWriter for response object of type"));
      request = client.target(generateURL("/test/used")).request();
      response = request.get();
      Assert.assertFalse(Boolean.parseBoolean(response.readEntity(String.class)));
   }
}
