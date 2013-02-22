package org.jboss.resteasy.test.nextgen.providers.atom;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.atom.BaseLink;
import org.jboss.resteasy.plugins.providers.atom.RelativeLink;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LinkTest extends BaseResourceTest
{

   @Path("/products")
   public static class ProductService
   {
      @GET
      @Produces("application/xml")
      @Path("{id}")
      public Product getProduct(@PathParam("id") int id)
      {
         Product p = new Product();
         p.setId(id);
         p.setName("iphone");
         p.getLinks().add(new RelativeLink("self", "/self"));
         p.getLinks().add(new BaseLink("create", "/products"));
         return p;
      }

   }

   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(ProductService.class);
   }

   @Test
   public void testRelativeLink() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target(generateURL("/products/333"));
      Response response = target.request().get();
      Product product = response.readEntity(Product.class);
      Assert.assertEquals(product.getLinks().get(0).getHref().getPath(), "/products/333/self");
      Assert.assertEquals(product.getLinks().get(1).getHref().getPath(), "/products");
      System.out.println();
   }

   @Test
   public void testRelativeLink2() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target(generateURL("/products/333"));
      Response response = target.request().get();
      System.out.println(response.readEntity(String.class));
   }
}
