package org.jboss.resteasy.test.client;


import java.io.UnsupportedEncodingException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.test.client.resource.AbortMessageResourceFilter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpTestCaseDetails RESTEASY-1540
 * @tpSince RESTEasy 3.0.20.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({NotForForwardCompatibility.class})
public class AbortMessageTest {
   static Client client;

   @BeforeClass
   public static void setup() {
       client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void close() {
       client.close();
   }

   @Deployment
   public static Archive<?> deploy() {
       WebArchive war = TestUtil.prepareArchive(AbortMessageTest.class.getSimpleName());
       return TestUtil.finishContainerPrepare(war, null, AbortMessageResourceFilter.class);
   }

   private String generateURL(String path) {
       return PortProviderUtil.generateURL(path, AbortMessageTest.class.getSimpleName());
   }

    /**
     * @tpTestDetails Send response with "Aborted"
     * @tpSince RESTEasy 3.0.20.Final
     */
   @Test
   public void testAbort() throws UnsupportedEncodingException {
      WebTarget target = client.target(generateURL("/showproblem"));
      Response response = target.request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("aborted", response.readEntity(String.class));
   }
}