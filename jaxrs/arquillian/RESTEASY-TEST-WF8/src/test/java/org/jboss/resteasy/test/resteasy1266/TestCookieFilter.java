package org.jboss.resteasy.test.resteasy1266;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.resteasy1266.TestFilter;
import org.jboss.resteasy.resteasy1266.TestApplication;
import org.jboss.resteasy.resteasy1266.TestResource;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for RESTEASY-1266.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date May 4, 2016
 */
@RunWith(Arquillian.class)
public class TestCookieFilter
{
   private static final LogMessages log = LogMessages.LOGGER;
   private static final String OLD_COOKIE_NAME = "old-cookie";

   @Deployment
   public static Archive<?> createTestArchive()
   {
      System.out.println("entering createTestArchive()");
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1266.war")
            .addClasses(TestApplication.class, TestResource.class, TestFilter.class)
            .addAsWebInfResource("1266/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void test()
   {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target("http://localhost:8080/RESTEASY-1266/test/get");
      Response response = target.request().get();
      log.info("status: " + response.getStatus());
      NewCookie cookie = response.getCookies().get(OLD_COOKIE_NAME);
      client.close();
      
      client = ClientBuilder.newClient();
      target = client.target("http://localhost:8080/RESTEASY-1266/test/return");
      response = target.request().cookie(cookie).cookie(cookie).get();
      log.info("status: " + response.getStatus());
   }
   
   //@Test
   public void test2()
   {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target("http://localhost:8080/RESTEASY-1266/test/duplicates");
      Builder builder = target.request();
      NewCookie cookie = new NewCookie("cookie", "value");
      NewCookie cookie2 = new NewCookie("cookie2", "value2");
      Response response = builder.cookie(cookie).cookie(cookie2).get();
      log.info("status: " + response.getStatus());
   }
   
   //@Test
   public void test3()
   {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target("http://localhost:8080/RESTEASY-1266/test/duplicate");
      Builder builder = target.request();
      NewCookie cookie = new NewCookie("cookie", "value");
      NewCookie cookie2 = new NewCookie("cookie", "value2");
      Response response = builder.cookie(cookie).cookie(cookie2).get();
      log.info("status: " + response.getStatus());
   }
}
