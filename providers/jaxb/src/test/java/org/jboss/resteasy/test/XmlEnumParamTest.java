package org.jboss.resteasy.test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * RESTEASY-428
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class XmlEnumParamTest extends BaseResourceTest
{
   @XmlEnum
   public static enum Location
   {

      @XmlEnumValue("north")
      NORTH("north"),
      @XmlEnumValue("south")
      SOUTH("south"),
      @XmlEnumValue("east")
      EAST("east"),
      @XmlEnumValue("west")
      WEST("west");
      private final String value;

      Location(String v)
      {
         value = v;
      }

      public String value()
      {
         return value;
      }

      public static Location fromValue(String v)
      {
         for (Location c : Location.values())
         {
            if (c.value.equals(v))
            {
               return c;
            }
         }
         throw new IllegalArgumentException(v.toString());
      }

   }

   @Path("enum")
   public static class LocationResource
   {
      @GET
      @Produces("text/plain")
      public String get(@QueryParam("loc") Location loc)
      {
         return loc.toString();
      }
   }

   @BeforeClass
   public static void init() throws Exception
   {
      addPerRequestResource(LocationResource.class);

   }


   @Test
   public void testXmlEnumParam() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target(TestPortProvider.generateURL("/enum")).queryParam("loc", "north");
      String res = target.request().get(String.class);
      Assert.assertEquals("NORTH", res.toUpperCase());

   }

}
