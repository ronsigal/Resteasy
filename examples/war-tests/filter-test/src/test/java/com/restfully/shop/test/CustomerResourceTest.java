package com.restfully.shop.test;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CustomerResourceTest
{
   /**
    * RESTEASY-541
    *
    * @throws Exception
    */
   @Test
   public void testMethodNotFound() throws Exception
   {
      Client client = ResteasyClientBuilder.newClient();
      WebTarget target = client.target("http://localhost:9095/customers");

      String newCustomer = "<customer>"
              + "<first-name>Bill</first-name>"
              + "<last-name>Burke</last-name>"
              + "<street>256 Clarendon Street</street>"
              + "<city>Boston</city>"
              + "<state>MA</state>"
              + "<zip>02115</zip>"
              + "<country>USA</country>"
              + "</customer>";
      Response response = target.request().put(Entity.entity(newCustomer, MediaType.APPLICATION_XML));
      Assert.assertEquals(405, response.getStatus());

   }

   @Test
   public void testStaticResource() throws Exception
   {
      Client client = ResteasyClientBuilder.newClient();
      WebTarget target = client.target("http://localhost:9095/foo.html");
      String response = target.request().get(String.class);
      Assert.assertNotNull(response);
      Assert.assertTrue(response.indexOf("hello world") > -1);

   }

   @Test
   public void testCustomerResource() throws Exception
   {
      System.out.println("*** Create a new Customer ***");
      // Create a new customer
      String newCustomer = "<customer>"
              + "<first-name>Bill</first-name>"
              + "<last-name>Burke</last-name>"
              + "<street>256 Clarendon Street</street>"
              + "<city>Boston</city>"
              + "<state>MA</state>"
              + "<zip>02115</zip>"
              + "<country>USA</country>"
              + "</customer>";

      URL postUrl = new URL("http://localhost:9095/customers");
      HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
      connection.setDoOutput(true);
      connection.setInstanceFollowRedirects(false);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/xml");
      OutputStream os = connection.getOutputStream();
      os.write(newCustomer.getBytes());
      os.flush();
      Assert.assertEquals(HttpURLConnection.HTTP_CREATED, connection.getResponseCode());
      System.out.println("Location: " + connection.getHeaderField("Location"));
      connection.disconnect();


      // Get the new customer
      System.out.println("*** GET Created Customer **");
      URL getUrl = new URL("http://localhost:9095/customers/1");
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setRequestMethod("GET");
      System.out.println("Content-Type: " + connection.getContentType());

      BufferedReader reader = new BufferedReader(new
              InputStreamReader(connection.getInputStream()));

      String line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }
      Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
      connection.disconnect();

      // Update the new customer.  Change Bill's name to William
      String updateCustomer = "<customer>"
              + "<first-name>William</first-name>"
              + "<last-name>Burke</last-name>"
              + "<street>256 Clarendon Street</street>"
              + "<city>Boston</city>"
              + "<state>MA</state>"
              + "<zip>02115</zip>"
              + "<country>USA</country>"
              + "</customer>";
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("PUT");
      connection.setRequestProperty("Content-Type", "application/xml");
      os = connection.getOutputStream();
      os.write(updateCustomer.getBytes());
      os.flush();
      Assert.assertEquals(HttpURLConnection.HTTP_NO_CONTENT, connection.getResponseCode());
      connection.disconnect();

      // Show the update
      System.out.println("**** After Update ***");
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setRequestMethod("GET");

      System.out.println("Content-Type: " + connection.getContentType());
      reader = new BufferedReader(new
              InputStreamReader(connection.getInputStream()));

      line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }
      Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
      connection.disconnect();
   }
}
