package org.jboss.resteasy.test.nextgen.exception;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Hashtable;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-1252
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Jan 23, 2016
 */
public class TestUnauthorizedExceptionLogging
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected static ByteArrayOutputStream out;
   protected static ByteArrayOutputStream err;
   protected static PrintStream out_orig;
   protected static PrintStream err_orig;

   @Path("")
   public static class TestResource
   {
      @GET
      @DenyAll
      @Path("denyall")
      public void denyAll()
      {
         return;
      }
      
      @GET
      @RolesAllowed("super")
      @Path("rolesallowed")
      public void rolesAllowed()
      {
         return;
      }
   }

   @Before
   public void before() throws Exception
   {
      out_orig = System.out;
      err_orig = System.err;
      out = new ByteArrayOutputStream();
      err = new ByteArrayOutputStream();
      System.setOut(new PrintStream(out));
      System.setErr(new PrintStream(err));
      
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      Hashtable<String,String> contextParams = new Hashtable<String,String>();
      contextParams.put("resteasy.role.based.security", "true");
      deployment = EmbeddedContainer.start(initParams, contextParams);
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testDenyAll() throws Exception
   {
      try
      {
         Client client = ClientBuilder.newClient();
         Response response = client.target(generateURL("/denyall")).request().get();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(403, response.getStatus());
         System.setOut(out_orig);
         System.setErr(err_orig);
         String s_out = new String(out.toByteArray());
         String s_err = new String(err.toByteArray());
         String s = s_out + s_err;
         System.out.println("------------");
         System.out.println("out: \"" + s_out + "\"");
         System.out.println("------------");
         System.out.println("err: \"" + s_err + "\"");
         Assert.assertFalse(s.contains("ERROR"));
         Assert.assertFalse(s.contains("RESTEASY002010: Failed to execute"));
      }
      catch (Exception e)
      {
         e.printStackTrace(); 
      }
   }
   
   @Test
   public void testRolesAllowed() throws Exception
   {
      try
      {
         Client client = ClientBuilder.newClient();
         Response response = client.target(generateURL("/rolesallowed")).request().get();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(403, response.getStatus());
         System.setOut(out_orig);
         System.setErr(err_orig);
         String s_out = new String(out.toByteArray());
         String s_err = new String(err.toByteArray());
         String s = s_out + s_err;
         System.out.println("------------");
         System.out.println("out: \"" + s_out + "\"");
         System.out.println("------------");
         System.out.println("err: \"" + s_err + "\"");
         Assert.assertFalse(s.contains("ERROR"));
         Assert.assertFalse(s.contains("RESTEASY002010: Failed to execute"));
      }
      catch (Exception e)
      {
         e.printStackTrace(); 
      }
   }
}
