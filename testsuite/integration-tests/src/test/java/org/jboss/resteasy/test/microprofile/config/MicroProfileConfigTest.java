package org.jboss.resteasy.test.microprofile.config;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.microprofile.config.resource.MicroProfileConfigResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;

/**
 * @tpSubChapter MicroProfile Config
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-2131
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MicroProfileConfigTest {

   private static ResteasyClient client;
   private static OnlineManagementClient managementClient;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(MicroProfileConfigTest.class.getSimpleName())
            .setWebXML(MicroProfileConfigTest.class.getPackage(), "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      return TestUtil.finishContainerPrepare(war, null, MicroProfileConfigResource.class);
   }

   @BeforeClass
   public static void before() throws Exception {
      client = (ResteasyClient)ClientBuilder.newClient();
      managementClient = TestUtil.clientInit();
//      TestUtil.runCmd(managementClient, "/subsystem=microprofile-config-smallrye/config-source=props:"
//      		+ "add(properties={\"system\" = \"system-management\","
//      		+                 "\"management\" = \"management-management\"})\n");
//      TestUtil.runCmd(managementClient, "/subsystem=microprofile-config-smallrye/config-source=props:"
//        		+ "add(properties={system = system-management,"
//        		+                 "management = management-management})\n");
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
//      TestUtil.runCmd(managementClient, "/subsystem=microprofile-config-smallrye/config-source=props:remove");
//      managementClient.close();
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, MicroProfileConfigTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Verify system variables are accessible and have highest priority; get Config programmatically.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testSystemProgrammatic() throws Exception {
      Response response = client.target(generateURL("/system/prog")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("system-system", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify system variables are accessible and have highest priority; get Config by injection.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testSystemInject() throws Exception {
      Response response = client.target(generateURL("/system/inject")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("system-system", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify web.xml context params are accessible; get Config programmatically.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testManagementProgrammatic() throws Exception {
	   
	      TestUtil.runCmd(managementClient, "/subsystem=microprofile-config-smallrye/config-source=props:"
	        		+ "add(properties={system = system-management,"
	        		+                 "management = management-management})\n");
	      
      Response response = client.target(generateURL("/management/prog")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("management-management", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify web.xml context params are accessible; get Config by injection.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testManagementInject() throws Exception {
      Response response = client.target(generateURL("/management/inject")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("management-management", response.readEntity(String.class));
   }
   
   /**
    * @tpTestDetails Verify web.xml init params are accessible and have higher priority than context params; get Config programmatically.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testInitProgrammatic() throws Exception {
      Response response = client.target(generateURL("/init/prog")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("init-init", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify web.xml init params are accessible and have higher priority than context params; get Config by injection.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testInitInject() throws Exception {
      Response response = client.target(generateURL("/init/inject")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("init-init", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify web.xml context params are accessible; get Config programmatically.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testContextProgrammatic() throws Exception {
      Response response = client.target(generateURL("/context/prog")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("context-context", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify web.xml context params are accessible; get Config by injection.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testContextInject() throws Exception {
      Response response = client.target(generateURL("/context/inject")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("context-context", response.readEntity(String.class));
   }
}
