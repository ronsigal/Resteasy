package org.jboss.resteasy.test.nextgen.resteasy760;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.resteasy760.TestApplication;
import org.jboss.resteasy.resteasy760.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @author Achim Bitzer
 * @version $Revision: 1.1 $
 *
 * Copyright February 26, 2013
 */
@RunWith(Arquillian.class)
public class FormParameterTest
{
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

   @Test
   public void testFormParamWithNoQueryParamPut() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target("http://localhost:9090/RESTEASY-760/put/noquery/");
      Response response = target.request().put(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      System.out.println("response: " + response.readEntity(String.class));
      assertEquals("abc xyz", response.readEntity(String.class));
   }

   @Test
   public void testFormParamWithNoQueryParamPutEncoded() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target("http://localhost:9090/RESTEASY-760/put/noquery/encoded");
      Response response = target.request().put(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      System.out.println("response: " + response.readEntity(String.class));
      assertEquals("abc%20xyz", response.readEntity(String.class));
   }

   @Test
   public void testFormParamWithNoQueryParamPost() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target("http://localhost:9090/RESTEASY-760/post/noquery/");
      Response response = target.request().post(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      System.out.println("response: " + response.readEntity(String.class));
      assertEquals("abc xyz", response.readEntity(String.class));
   }

   @Test
   public void testFormParamWithNoQueryParamPostEncoded() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target("http://localhost:9090/RESTEASY-760/post/noquery/encoded");
      Response response = target.request().post(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      System.out.println("response: " + response.readEntity(String.class));
      assertEquals("abc%20xyz", response.readEntity(String.class));
   }

   @Test
   public void testFormParamWithQueryParamPut() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target("http://localhost:9090/RESTEASY-760/put/query?query=xyz");
      Response response = target.request().put(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      System.out.println("response: " + response.readEntity(String.class));
      assertEquals("abc xyz", response.readEntity(String.class));
   }
   
   @Test
   public void testFormParamWithQueryParamPutEncoded() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target("http://localhost:9090/RESTEASY-760/put/query/encoded?query=xyz");
      Response response = target.request().put(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      System.out.println("response: " + response.readEntity(String.class));
      assertEquals("abc%20xyz", response.readEntity(String.class));
   }

   @Test
   public void testFormParamWithQueryParamPost() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target("http://localhost:9090/RESTEASY-760/post/query?query=xyz");
      Response response = target.request().post(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      System.out.println("response: " + response.readEntity(String.class));
      assertEquals("abc xyz", response.readEntity(String.class));
   }
   
   @Test
   public void testFormParamWithQueryParamPostEncoded() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target("http://localhost:9090/RESTEASY-760/post/query/encoded?query=xyz");
      Response response = target.request().post(Entity.form(new Form("formParam", "abc xyz")));
      assertTrue(response != null);
      System.out.println("response: " + response.readEntity(String.class));
      assertEquals("abc%20xyz", response.readEntity(String.class));
   }
}
