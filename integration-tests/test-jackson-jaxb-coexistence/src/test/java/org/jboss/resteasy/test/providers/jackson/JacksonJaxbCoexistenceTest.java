package org.jboss.resteasy.test.providers.jackson;

import org.jboss.resteasy.annotations.providers.NoJackson;
import org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes;
import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JacksonJaxbCoexistenceTest extends BaseResourceTest
{
   private static Client client;
   
   public static class Product
   {
      protected String name;

      protected int id;

      public Product()
      {
      }

      public Product(int id, String name)
      {
         this.id = id;
         this.name = name;
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      public int getId()
      {
         return id;
      }

      public void setId(int id)
      {
         this.id = id;
      }
   }

   @XmlRootElement(name = "product")
   @NoJackson
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class XmlProduct
   {
      @XmlAttribute
      protected String name;

      @XmlAttribute
      protected int id;

      public XmlProduct()
      {
      }

      public XmlProduct(int id, String name)
      {
         this.id = id;
         this.name = name;
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      public int getId()
      {
         return id;
      }

      public void setId(int id)
      {
         this.id = id;
      }
   }

   @XmlRootElement(name = "product")
   @XmlAccessorType(XmlAccessType.FIELD)
   @IgnoreMediaTypes("application/*+json")
   public static class Product2
   {
      @XmlAttribute
      protected String name;

      @XmlAttribute
      protected int id;

      public Product2()
      {
      }

      public Product2(int id, String name)
      {
         this.id = id;
         this.name = name;
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      public int getId()
      {
         return id;
      }

      public void setId(int id)
      {
         this.id = id;
      }
   }

   @Path("/products")
   public static class JacksonService
   {

      @GET
      @Produces("application/json")
      @Path("{id}")
      public Product getProduct()
      {
         return new Product(333, "Iphone");
      }

      @GET
      @Produces("application/json")
      public Product[] getProducts()
      {

         Product[] products = {new Product(333, "Iphone"), new Product(44, "macbook")};
         return products;
      }

      @POST
      @Produces("application/foo+json")
      @Consumes("application/foo+json")
      @Path("{id}")
      public Product post(Product p)
      {
         return p;
      }

   }


   @Path("/xml/products")
   public static class XmlService
   {

      @GET
      @Produces("application/json")
      @Path("{id}")
      @BadgerFish
      public XmlProduct getProduct()
      {
         return new XmlProduct(333, "Iphone");
      }

      @GET
      @Produces("application/json")
      @NoJackson
      public XmlProduct[] getProducts()
      {

         XmlProduct[] products = {new XmlProduct(333, "Iphone"), new XmlProduct(44, "macbook")};
         return products;
      }

   }

   @Path("/jxml/products")
   public static class JacksonXmlService
   {

      @GET
      @Produces("application/json")
      @Path("{id}")
      public Product2 getProduct()
      {
         return new Product2(333, "Iphone");
      }

      @GET
      @Produces("application/json")
      public Product2[] getProducts()
      {

         Product2[] products = {new Product2(333, "Iphone"), new Product2(44, "macbook")};
         return products;
      }

   }

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void afterClass() throws Exception
   {
      client.close();
   }
   
   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(JacksonService.class);
      dispatcher.getRegistry().addPerRequestResource(XmlService.class);
      dispatcher.getRegistry().addPerRequestResource(JacksonXmlService.class);
   }

   @Test
   public void testJacksonString() throws Exception
   {
      Builder request = client.target(generateURL("/products/333")).request();
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("{\"name\":\"Iphone\",\"id\":333}", response.readEntity(String.class));

      request = client.target(generateURL("/products")).request();
      Response response2 = request.get();
      String entity = response2.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response2.getStatus());
      Assert.assertEquals("[{\"name\":\"Iphone\",\"id\":333},{\"name\":\"macbook\",\"id\":44}]", entity);

   }

   /**
    * Test that Jackson is picked
    *
    * @throws Exception
    */
   @Test
   public void testJacksonXmlString() throws Exception
   {
      Builder request = client.target(generateURL("/jxml/products/333")).request();
      Response response = request.get();
      String entity = response.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("{\"name\":\"Iphone\",\"id\":333}", entity);

      request = client.target(generateURL("/jxml/products")).request();
      Response response2 = request.get();
      entity = response2.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response2.getStatus());
      Assert.assertEquals("[{\"name\":\"Iphone\",\"id\":333},{\"name\":\"macbook\",\"id\":44}]", entity);

   }

   @Test
   public void testXmlString() throws Exception
   {
      Builder request = client.target(generateURL("/xml/products/333")).request();
      Response response = request.get();
      String entity = response.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(entity.startsWith("{\"product"));

      request = client.target(generateURL("/xml/products")).request();
      Response response2 = request.get();
      entity = response2.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response2.getStatus());
      Assert.assertTrue(entity.startsWith("[{\"product"));
   }

   @Test
   public void testJackson() throws Exception
   {
      Builder request = client.target(generateURL("/products/333")).request();
      Response response = request.get();
      Product p = response.readEntity(Product.class);
      Assert.assertEquals(333, p.getId());
      Assert.assertEquals("Iphone", p.getName());
      request = client.target(generateURL("/products")).request();
      Response response2 = request.get();
      System.out.println(response2.readEntity(String.class));
      Assert.assertEquals(200, response2.getStatus());

      request = client.target(generateURL("/products/333")).request();
      response = request.post(Entity.entity(p, "application/foo+json"));
      p = response.readEntity(Product.class);
      Assert.assertEquals(333, p.getId());
      Assert.assertEquals("Iphone", p.getName());


   }

}
