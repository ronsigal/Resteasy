package org.jboss.resteasy.test.nextgen.cdi.inheritance;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.inheritence.Book;
import org.jboss.resteasy.cdi.inheritence.InheritanceResource;
import org.jboss.resteasy.cdi.inheritence.JaxRsActivator;
import org.jboss.resteasy.cdi.inheritence.SelectBook;
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
 * Copyright Feb 20, 2013
 */
@RunWith(Arquillian.class)
public class VanillaInheritanceTest
{
   @Inject Logger log;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-cdi-ejb-test.war")
            .addClasses(JaxRsActivator.class, UtilityProducer.class, Book.class, SelectBook.class, InheritanceResource.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testVanilla() throws Exception
   {
      log.info("starting testVanilla()");
      WebTarget target = ClientBuilder.newClient().target("http://localhost:8080/resteasy-cdi-ejb-test/rest/vanilla/");
      Response response = target.request().get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.close();
   }
}
