package org.jboss.resteasy.test.providers.jaxb.collection;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JsonMapTest extends BaseResourceTest
{
   @XmlRootElement
   public static class Foo
   {
      @XmlAttribute
      private String name;

      public Foo()
      {
      }

      public Foo(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }
   }

   @Path("/map")
   public static class MyResource
   {
      @POST
      @Produces("application/json")
      @Consumes("application/json")
      public Map<String, Foo> post(Map<String, Foo> map)
      {
         Assert.assertEquals(2, map.size());
         Assert.assertNotNull(map.get("bill"));
         Assert.assertNotNull(map.get("monica"));
         Assert.assertEquals(map.get("bill").getName(), "bill");
         Assert.assertEquals(map.get("monica").getName(), "monica");
         return map;
      }

      @POST
      @Produces("application/json")
      @Consumes("application/json")
      @Path("empty")
      public Map<String, Foo> postEmpty(Map<String, Foo> map)
      {
         Assert.assertEquals(0, map.size());
         return map;
      }

      @GET
      @Produces("application/json")
      public Map<String, Foo> get()
      {
         HashMap<String, Foo> map = new HashMap<String, Foo>();
         map.put("bill", new Foo("bill"));
         map.put("monica", new Foo("monica"));
         return map;
      }
   }

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(MyResource.class);

   }

   @Test
   public void testProvider() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target(generateURL("/map"));
      Response response = target.request().get();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println(entity);

      response = target.request().post(Entity.entity(entity, "application/json"));
      Assert.assertEquals(200, response.getStatus());
      response.close();

      entity = "{\"monica\":{\"foo\":{\"@name\":\"monica\"}},\"bill\":{\"foo\":{\"@name\":\"bill\"}}}";
      response = target.request().post(Entity.entity(entity, "application/json"));
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }

   @Test
   public void testEmptyMap() throws Exception
   {
      Builder request = ClientBuilder.newClient().target(generateURL("/map/empty")).request();
      Response response = request.post(Entity.entity("{}", "application/json"));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("{}", response.readEntity(String.class));

   }
}