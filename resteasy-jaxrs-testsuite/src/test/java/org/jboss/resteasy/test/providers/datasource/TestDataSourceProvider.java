/**
 *
 */
package org.jboss.resteasy.test.providers.datasource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.LocateTestData;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a> Jun 23,
 *         2008
 */
public class TestDataSourceProvider extends BaseResourceTest
{

   private static final String TEST_URI = generateURL("/jaf");
   private static Client client;
   
   @BeforeClass
   public static void beforeClass()
   {
      client = ClientBuilder.newClient();
   }
   
   @AfterClass
   public static void afterClass()
   {
      client.close();
   }

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(DataSourceResource.class);
   }

   @Test
   public void testPostDataSource() throws Exception
   {
      //File file = new File("./src/test/test-data/harper.jpg");
      File file = LocateTestData.getTestData("harper.jpg");
      Assert.assertTrue(file.exists());
      Response response = client.target(TEST_URI).request().post(Entity.entity(file, "image/jpeg"));
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      Assert.assertEquals("image/jpeg", response.readEntity(String.class));      
   }

   @Test
   public void testEchoDataSourceBigData() throws Exception
   {
      File file = LocateTestData.getTestData("harper.jpg");
      Assert.assertTrue(file.exists());
      Response response = client.target(TEST_URI + "/echo").request().post(Entity.entity(file, "image/jpeg"));
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      
      InputStream ris = null;
      InputStream fis = null;
      try
      {
         ris = response.readEntity(InputStream.class);
         fis = new FileInputStream(file);
         int fi;
         int ri;
         do
         {
            fi = fis.read();
            if (fi == -1) break;
            ri = ris.read();
            if (fi != ri)
               Assert.fail("The sent and received stream is not identical.");
         } while (fi != -1);         
      }
      finally
      {
         if (ris != null)
            ris.close();
         if (fis != null)
            fis.close();
      }
   }

   @Test
   public void testEchoDataSourceSmallData() throws Exception
   {
      byte[] input = "Hello World!".getBytes("utf-8");
      Response response = client.target(TEST_URI + "/echo").request().post(Entity.entity(input, MediaType.APPLICATION_OCTET_STREAM));
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      
      InputStream ris = null;
      InputStream bis = null;
      try
      {
         ris = response.readEntity(InputStream.class);
         bis = new ByteArrayInputStream(input);
         int fi;
         int ri;
         do
         {
            fi = bis.read();
            ri = ris.read();
            if (fi != ri)
               Assert.fail("The sent and recived stream is not identical.");
         } while (fi != -1);
      }
      finally
      {
         if (ris != null)
            ris.close();
         if (bis != null)
            bis.close();
      }
   }

   @Test
   public void testGetDataSource() throws Exception
   {
      String value = "foo";
      Response response = client.target(TEST_URI + "/" + value).request().get();
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      Assert.assertEquals(value, response.readEntity(String.class));
   }
}
