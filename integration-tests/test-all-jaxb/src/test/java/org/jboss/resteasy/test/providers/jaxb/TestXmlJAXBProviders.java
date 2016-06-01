package org.jboss.resteasy.test.providers.jaxb;

import org.junit.AfterClass;
import org.junit.Assert;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.providers.jaxb.data.Order;
import org.jboss.resteasy.test.providers.jaxb.generated.order.Ordertype;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

import static org.jboss.resteasy.test.TestPortProvider.createProxy;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * A TestXmlJAXBProviders.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class TestXmlJAXBProviders extends BaseResourceTest
{
   private static final String PATH = "/jaxb/orders";
   private static final String URL = generateURL(PATH);
   private static Client client;

   private XmlOrderClient xmlOrderlient;

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
   
   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(OrderResource.class);
      //RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      xmlOrderlient = createProxy(XmlOrderClient.class, PATH);
   }

   @Test
   public void testUnmarshalOrder() throws Exception
   {
      InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
              "orders/order_123.xml");
      Order order = JAXBHelper.unmarshall(Order.class, in).getValue();

      Assert.assertNotNull(order);
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
   }

   /**
    * This test was commented out in RESTEasy 2.2.
    * 
    * The file order_123.xml is an Order, not an Ordertype.
    */
//   @Test
   public void testUnmarshalOrdertype() throws Exception
   {
      InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
              "order_123.xml");
      JAXBContext jaxb = JAXBContext.newInstance(Ordertype.class);
      Unmarshaller u = jaxb.createUnmarshaller();
      Ordertype order = (Ordertype) u.unmarshal(in);
      Assert.assertNotNull(order);
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
   }

   /**
    * This test was commented out in RESTEasy 2.2.
    * 
    * It was fixed by assigning the result of client.getOrderById() to an
    * Order instead of an Ordertype.
    */
   @Test
   public void testGetOrder()
   {
      Order order = xmlOrderlient.getOrderById("order_123");
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
   }

   
   /**
    * This test is the RESTEasy client framework version of the original
    * testGetOrderWithParams().
    */
   @Test
   public void testGetOrderWithParams() throws Exception
   {
      Builder request = client.target(URL + "/order_123").request();
      request.header(JAXBHelper.FORMAT_XML_HEADER, "true");
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      ProviderHelper.writeTo(response.readEntity(InputStream.class), System.out);
      response.close();
   }
   
   /**
    * This test is new.
    * 
    * It is the RESTEasy client framework version of the original
    * testGetOrderWithParams(), except that it unmarshals the returned
    * order from an OutputStream and tests its value, instead of just
    * printing it out.
    */
   @Test
   public void testGetOrderAndUnmarshal() throws Exception
   {
      Builder request = client.target(URL + "/order_123").request();
      request.header(JAXBHelper.FORMAT_XML_HEADER, "true");
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      JAXBContext jaxb = JAXBContext.newInstance(Order.class);
      Unmarshaller u = jaxb.createUnmarshaller();
      Order order = (Order) u.unmarshal(response.readEntity(InputStream.class));
      Assert.assertNotNull(order);
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
      response.close();
   }

   /**
    * This test is new.
    * 
    * It is the RESTEasy client framework version of the original
    * testGetOrderWithParams(), except that it uses the client framework
    * to implicitly unmarshal the returned order and it tests its value,
    * instead of just printing it out.
    */
   @Test
   public void testGetOrderWithParamsToOrder() throws Exception
   {
      Builder request = client.target(URL + "/order_123").request();
      request.header(JAXBHelper.FORMAT_XML_HEADER, "true");
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      Order order = response.readEntity(Order.class);
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
   }
   
   /**
    * This test was commented out in RESTEasy 2.2.
    * 
    * It was fixed by assigning the result of JAXBHelper.unmarshall(() to an
    * Order instead of an Ordertype.  Also, an assert had to commented in
    * OrderResource.updateOrder().
    */
   @Test
   public void testUpdateOrder()
   {
      InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
              "orders/order_123.xml");
      Order order = JAXBHelper.unmarshall(Order.class, in).getValue();
      int initialItemCount = order.getItems().size();
      order = xmlOrderlient.updateOrder(order, "order_123");
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
      Assert.assertNotSame(initialItemCount, order.getItems().size());
      Assert.assertEquals(3, order.getItems().size());
   }

}
