package org.jboss.resteasy.spring;

import static org.jboss.resteasy.test.TestPortProvider.createTarget;

import javax.ws.rs.core.Response;

import org.junit.Assert;

import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spring.beanprocessor.MyInterceptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This class tests a gamut of Spring related functionality including @Configuration
 * beans, @Autowired, scanned beans, interceptors and overall integration
 * between RESTEasy and the Spring ApplicationContext.
 * 
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @see SpringBeanProcessor
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-bean-processor-test.xml" })
public class TestSpringBeanProcessor
{

   @Test
   public void testAutoProxy() throws Exception
   {
      checkGet("/intercepted", "customer=bill");
      Assert.assertTrue(MyInterceptor.invoked);
   }

   @Test
   public void testProcessor() throws Exception
   {
      checkGet("", "customer=bill");
   }

   @Test
   public void testPrototyped() throws Exception
   {
      checkGet("/prototyped/1", "bill0");
      checkGet("/prototyped/1", "bill0");
   }

   @Test
   public void testRegistration() throws Exception
   {
      Response resp = createTarget("/registered/singleton/count").request().post(null);
      check(resp, 200, "0");
   }

	@Test
   // test for https://issues.jboss.org/browse/RESTEASY-1212
	public void testRegistrationViaSuper() throws Exception
   {
	   Response resp = createTarget("/registered/super/count").request().post(null);
		check(resp, 200, "0");
	}
   
   @Test
   public void testNotRegisteredAtRoot() throws Exception {
      Assert.assertEquals(404, createTarget("/count").request().post(null).getStatus());
   }

   @Test
   public void testScanned() throws Exception
   {
      checkGet("/scanned", "Hello");
   }

   @Test
   public void testAutowiredProvider() throws Exception
   {
      checkGet("/customer-name?name=Solomon", "customer=Solomon");
      checkGet("/customer-object?customer=Solomon", "Solomon");
   }

   private static void checkGet(String url, String expectedResponse) throws Exception
   {
      check(createTarget(url).request().get(), 200, expectedResponse);
   }

   private static void check(Response resp, int expectedStatus,
         String expectedResponse)
   {
      Assert.assertEquals(expectedStatus, resp.getStatus());
      Assert.assertEquals(resp.readEntity(String.class), expectedResponse);
   }

}
