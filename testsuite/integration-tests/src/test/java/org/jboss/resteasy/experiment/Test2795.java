package org.jboss.resteasy.experiment;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class Test2795 {

   @Deployment()
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(Test2795.class.getSimpleName());
      war.addClass(Test2795Constraint.class);
      war.addClass(Test2795Exception.class);
      war.addClass(Test2795Validator.class);
      war.addClass(PortProviderUtil.class);
      war.addClass(ConstraintViolationExceptionMapper.class);
      return TestUtil.finishContainerPrepare(war, null, Test2795Resource.class, ConstraintViolationExceptionMapper.class);
   }
   
   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, Test2795.class.getSimpleName());
   }

   private static Client client; 

   @BeforeClass
   public static void init() throws Exception {
      client = (ResteasyClient) ResteasyClientBuilder.newClient();
   }

   @AfterClass
   public static void stop() throws Exception {
   }

   @Test
   public void testInvalid() throws Exception {
      Response response = client.target(generateURL("/test")).request().get();
      String answer = response.readEntity(String.class);
      System.out.println("answer: " + answer);
      assertEquals(400, response.getStatus());
      Assert.fail("ok");
   }
   
//   @Test
   public void testThrow() throws Exception {
      Response response = client.target(generateURL("/throw")).request().get();
      String answer = response.readEntity(String.class);
      System.out.println("answer: " + answer);
      assertEquals(400, response.getStatus());
      Assert.fail("ok");
   }
}
