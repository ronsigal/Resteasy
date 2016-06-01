package org.jboss.resteasy.test.resteasy760;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.resteasy760.TestApplication;
import org.jboss.resteasy.resteasy760.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @author Achim Bitzer
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 3, 2012
 */
@RunWith(Arquillian.class)
public class FormParameterTest
{
   private static Client client;
   
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-760.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(FormParameterTest.class)
            .addAsWebInfResource("web.xml");
      System.out.println(war.toString(true));
      return war;
   }

   @BeforeClass
   public static void beforeClass()
   {
      client = ResteasyClientBuilder.newClient();
   }
   
   @AfterClass
   public static void afterClass()
   {
      client.close();
   }
   
   @Test
   public void testFormParamWithNoQueryParamPut() throws Exception
   {
      Builder request = client.target("http://localhost:9090/RESTEASY-760/put/noquery/").request();
      request.header("Content-Type", "application/x-www-form-urlencoded");
      Response response = request.put(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      String entity = response.readEntity(String.class);
      System.out.println("response: " + entity);
      assertEquals("abc xyz", entity);
   }

   @Test
   public void testFormParamWithNoQueryParamPutEncoded() throws Exception
   {
      Builder request = client.target("http://localhost:9090/RESTEASY-760/put/noquery/encoded").request();
      request.header("Content-Type", "application/x-www-form-urlencoded");
      Response response = request.put(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      String entity = response.readEntity(String.class);
      System.out.println("response: " + entity);
      assertEquals("abc+xyz", entity);
   }

   @Test
   public void testFormParamWithNoQueryParamPost() throws Exception
   {
      Builder request = client.target("http://localhost:9090/RESTEASY-760/post/noquery/").request();
      request.header("Content-Type", "application/x-www-form-urlencoded");
      Response response = request.post(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      String entity = response.readEntity(String.class);
      System.out.println("response: " + entity);
      assertEquals("abc xyz", entity);
   }

   @Test
   public void testFormParamWithNoQueryParamPostEncoded() throws Exception
   {
      Builder request = client.target("http://localhost:9090/RESTEASY-760/post/noquery/encoded").request();
      request.header("Content-Type", "application/x-www-form-urlencoded");
      Response response = request.post(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      String entity = response.readEntity(String.class);
      System.out.println("response: " + entity);
      assertEquals("abc+xyz", entity);
   }

   @Test
   public void testFormParamWithQueryParamPut() throws Exception
   {
      Builder request = client.target("http://localhost:9090/RESTEASY-760/put/query?query=xyz").request();
      request.header("Content-Type", "application/x-www-form-urlencoded");
      Response response = request.put(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      String entity = response.readEntity(String.class);
      System.out.println("response: " + entity);
      assertEquals("abc xyz", entity);
   }
   
   @Test
   public void testFormParamWithQueryParamPutEncoded() throws Exception
   {
      Builder request = client.target("http://localhost:9090/RESTEASY-760/put/query/encoded?query=xyz").request();
      request.header("Content-Type", "application/x-www-form-urlencoded");
      Response response = request.put(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      String entity = response.readEntity(String.class);
      System.out.println("response: " + entity);
      assertEquals("abc+xyz", entity);
   }

   @Test
   public void testFormParamWithQueryParamPost() throws Exception
   {
      Builder request = client.target("http://localhost:9090/RESTEASY-760/post/query?query=xyz").request();
      request.header("Content-Type", "application/x-www-form-urlencoded");
      Response response = request.post(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      String entity = response.readEntity(String.class);
      System.out.println("response: " + entity);
      assertEquals("abc xyz", entity);
   }
   
   @Test
   public void testFormParamWithQueryParamPostEncoded() throws Exception
   {
      Builder request = client.target("http://localhost:9090/RESTEASY-760/post/query/encoded?query=xyz").request();
      request.header("Content-Type", "application/x-www-form-urlencoded");
      Response response = request.post(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      String entity = response.readEntity(String.class);
      System.out.println("response: " + entity);
      assertEquals("abc+xyz", entity);
   }
}
