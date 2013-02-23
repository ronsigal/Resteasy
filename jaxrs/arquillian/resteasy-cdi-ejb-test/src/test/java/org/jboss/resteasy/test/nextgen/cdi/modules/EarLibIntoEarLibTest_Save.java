package org.jboss.resteasy.test.nextgen.cdi.modules;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.modules.Injectable;
import org.jboss.resteasy.cdi.modules.InjectableBinder;
import org.jboss.resteasy.cdi.modules.InjectableIntf;
import org.jboss.resteasy.cdi.modules.JaxRsActivator;
import org.jboss.resteasy.cdi.modules.ModulesResource;
import org.jboss.resteasy.cdi.modules.ModulesResourceIntf;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 18, 2012
 */
@RunWith(Arquillian.class)
public class EarLibIntoEarLibTest_Save
{
   @Inject Logger log;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      JavaArchive fromJar = ShrinkWrap.create(JavaArchive.class, "from.jar")
            .addClasses(InjectableBinder.class, InjectableIntf.class, Injectable.class)
            .add(EmptyAsset.INSTANCE, "META-INF/beans.xml");
      JavaArchive toJar = ShrinkWrap.create(JavaArchive.class, "to.jar")
            .addClasses(EarLibIntoEarLibTest_Save.class, JaxRsActivator.class, UtilityProducer.class)
            .addClasses(ModulesResourceIntf.class, ModulesResource.class)
            .add(EmptyAsset.INSTANCE, "META-INF/beans.xml");
      EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
            .addAsLibrary(fromJar)
            .addAsLibrary(toJar);
      System.out.println(fromJar.toString(true));
      System.out.println(toJar.toString(true));
      System.out.println(ear.toString(true));
      return ear;
   }

   @Test
   public void testModules() throws Exception
   {
      log.info("starting testModules()");

//      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/modules/test/");
//      ClientResponse<?> response = request.get();
      WebTarget target = ClientBuilder.newClient().target("http://localhost:8080/resteasy-cdi-ejb-test/rest/modules/test/");
      Response response = target.request().get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.close();
   }
}
