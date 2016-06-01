package org.jboss.resteasy.test.providers.jackson;

import org.jboss.resteasy.annotations.providers.NoJackson;
import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;
import org.jboss.resteasy.plugins.providers.jackson.JacksonJsonpInterceptor;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.*;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JacksonTest extends BaseResourceTest
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

   @Path("/products")
   public interface JacksonProxy
   {
      @GET
      @Produces("application/json")
      @Path("{id}")
      Product getProduct();

      @GET
      @Produces("application/json")
      JacksonTest.Product[] getProducts();

      @POST
      @Produces("application/foo+json")
      @Consumes("application/foo+json")
      @Path("{id}")
      Product post(@PathParam("id") int id, Product p);
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

   @BeforeClass
   public static void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(JacksonService.class);
      dispatcher.getRegistry().addPerRequestResource(XmlService.class);
      deployment.getProviderFactory().register(JacksonJsonpInterceptor.class);
      //dispatcher.getRegistry().addPerRequestResource(JAXBService.class);
      client = ClientBuilder.newClient();
   }
   
   @AfterClass
   public static void afterClass()
   {
      client.close();
   }

   @Test
   public void testJacksonString() throws Exception
   {
      WebTarget target = client.target(generateURL("/products"));
      Response response = target.path("/333").request().get();
      String entity = response.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("{\"name\":\"Iphone\",\"id\":333}", entity);
      response.close();

      Response response2 = target.request().get();
      entity = response2.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response2.getStatus());
      Assert.assertEquals("[{\"name\":\"Iphone\",\"id\":333},{\"name\":\"macbook\",\"id\":44}]", entity);
      response2.close();

      Response response3 = target.path("/333").queryParam("callback", "product").request().get();
      entity = response3.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response3.getStatus());
      Assert.assertEquals("product({\"name\":\"Iphone\",\"id\":333})", entity);
      response3.close();

      Response response4 = target.queryParam("callback", "products").request().get();
      entity = response4.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response4.getStatus());
      Assert.assertEquals("products([{\"name\":\"Iphone\",\"id\":333},{\"name\":\"macbook\",\"id\":44}])", entity);
      response4.close();
   }

   @Test
   public void testXmlString() throws Exception
   {
      Response response = client.target(generateURL("/xml/products/333")).request().get();
      String entity = response.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(entity.startsWith("{\"product"));
      response.close();

      Response response2 = client.target(generateURL("/xml/products")).request().get();
      entity = response2.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response2.getStatus());
      Assert.assertTrue(entity.startsWith("[{\"product"));
      response2.close();

      Response response3 = client.target(generateURL("/xml/products/333")).queryParam("callback", "product").request().get();
      entity = response3.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response3.getStatus());
      Assert.assertTrue(entity.startsWith("product({\"product"));
      response3.close();

      Response response4 = client.target(generateURL("/xml/products")).queryParam("callback", "products").request().get();
      entity = response4.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(200, response4.getStatus());
      Assert.assertTrue(entity.startsWith("products([{\"product"));
      response4.close();
   }

   @Test
   public void testJackson() throws Exception
   {
      WebTarget target = client.target(generateURL("/products"));
      Response response = target.path("/333").request().get();
      Product p = response.readEntity(Product.class);
      Assert.assertEquals(333, p.getId());
      Assert.assertEquals("Iphone", p.getName());
      response.close();
      Response response2 = target.request().get();
      System.out.println(response2.readEntity(String.class));
      Assert.assertEquals(200, response2.getStatus());
      response2.close();

      response = target.path("/333").request().post(Entity.entity(p, "application/foo+json"));
      p = response.readEntity(Product.class);
      Assert.assertEquals(333, p.getId());
      Assert.assertEquals("Iphone", p.getName());
      response.close();


   }

/*
// todo figure out a nice way to support JAXB + Jackson

    @XmlRootElement
    public static class XmlResourceWithJAXB {
        String attr1;
        String attr2;

        @XmlElement(name = "attr_1")
        public String getAttr1() {
            return attr1;
        }

        public void setAttr1(String attr1) {
            this.attr1 = attr1;
        }

        @XmlElement
        public String getAttr2() {
            return attr2;
        }

        public void setAttr2(String attr2) {
            this.attr2 = attr2;
        }
    }


    public static class XmlResourceWithJacksonAnnotation {
        String attr1;
        String attr2;

        @JsonProperty("attr_1")
        public String getAttr1() {
            return attr1;
        }

        public void setAttr1(String attr1) {
            this.attr1 = attr1;
        }

        @XmlElement
        public String getAttr2() {
            return attr2;
        }

        public void setAttr2(String attr2) {
            this.attr2 = attr2;
        }
    }

    @Path("/jaxb")
    public static class JAXBService
    {

        @GET
        @Produces("application/json")
        public XmlResourceWithJAXB getJAXBResource() {
            XmlResourceWithJAXB resourceWithJAXB = new XmlResourceWithJAXB();
            resourceWithJAXB.setAttr1("XXX");
            resourceWithJAXB.setAttr2("YYY");
            return resourceWithJAXB;
        }


        @GET
        @Path(("/json"))
        @Produces("application/json")
        public XmlResourceWithJacksonAnnotation getJacksonAnnotatedResource() {
            XmlResourceWithJacksonAnnotation resource = new XmlResourceWithJacksonAnnotation();
            resource.setAttr1("XXX");
            resource.setAttr2("YYY");
            return resource;
        }

    }

    @Test
    public void testJacksonJAXB() throws Exception {
        {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(generateBaseUrl() + "/jaxb");
            HttpResponse response = client.execute(get);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.readEntity(String.class).getContent()));
            Assert.assertTrue(reader.readLine().contains("attr_1"));
        }

        {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(generateBaseUrl() + "/jaxb/json");
            HttpResponse response = client.execute(get);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.readEntity(String.class).getContent()));
            Assert.assertTrue(reader.readLine().contains("attr_1"));

        }

    }


*/
    @Test
   public void testJacksonProxy() throws Exception
   {
       JacksonProxy proxy = TestPortProvider.createProxy(JacksonProxy.class, "");
      Product p = new Product(1, "Stuff");
      p = proxy.post(1, p);
      Assert.assertEquals(1, p.getId());
      Assert.assertEquals("Stuff", p.getName());
   }
}
