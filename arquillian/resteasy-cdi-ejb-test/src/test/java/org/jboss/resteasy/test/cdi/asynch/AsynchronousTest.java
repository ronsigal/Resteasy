package org.jboss.resteasy.test.cdi.asynch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.asynch.AsynchronousResource;
import org.jboss.resteasy.cdi.asynch.AsynchronousStateless;
import org.jboss.resteasy.cdi.asynch.AsynchronousStatelessLocal;
import org.jboss.resteasy.cdi.asynch.JaxRsActivator;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 22, 2012
 */
@RunWith(Arquillian.class)
public class AsynchronousTest
{
   @Inject private Logger log;
   @Inject private AsynchronousStatelessLocal stateless;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-ejb-test.war")
      .addClasses(JaxRsActivator.class, UtilityProducer.class) 
      .addClasses(AsynchronousStatelessLocal.class, AsynchronousStateless.class)
      .addClasses(AsynchronousResource.class)
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testAsynchJaxRs() throws Exception
   {
      log.info("starting testAsynch()");
      Builder request = ClientBuilder.newClient().target("http://localhost:8080/resteasy-ejb-test/rest/asynch/simple").request();
      long start = System.currentTimeMillis();
      log.info("calling resource");
      Response clientResponse = request.get();
      log.info("status: " + clientResponse.getStatus());
      assertEquals(200, clientResponse.getStatus());
      assertTrue(System.currentTimeMillis() - start > 2000);
   }
   
   @Test
   public void testAsynchEJBLocal() throws Exception
   {
      log.info("starting testAsynchEJB()");
      long start = System.currentTimeMillis();
      log.info("calling EJB");
      stateless.asynch();
      System.out.println("current - start: " + (System.currentTimeMillis() - start));
      assertTrue(System.currentTimeMillis() - start >= 2000);
   }
   
   @Test
   public void testAsynchResourceAsynchEJB() throws Exception
   {
      log.info("starting testAsynchResourceAsynchEJB()");
      Builder request = ClientBuilder.newClient().target("http://localhost:8080/resteasy-ejb-test/rest/asynch/ejb").request();
      long start = System.currentTimeMillis();
      log.info("calling resource");
      Response clientResponse = request.get();
      log.info("status: " + clientResponse.getStatus());
      assertEquals(200, clientResponse.getStatus());
      assertTrue(System.currentTimeMillis() - start > 2000);
   }
}
