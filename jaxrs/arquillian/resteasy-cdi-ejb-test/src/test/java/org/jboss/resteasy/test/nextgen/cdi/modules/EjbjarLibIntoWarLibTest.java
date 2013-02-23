package org.jboss.resteasy.test.nextgen.cdi.modules;

import static org.junit.Assert.assertEquals;

import java.io.File;
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
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright February 22, 2013
 */
@RunWith(Arquillian.class)
public class EjbjarLibIntoWarLibTest
{
   @Inject Logger log;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      JavaArchive fromJar = ShrinkWrap.create(JavaArchive.class, "ejb-jar.jar")
            .addClasses(InjectableBinder.class, InjectableIntf.class, Injectable.class)
            .add(new FileAsset(new File("src/test/resources/modules/ejb-jar.xml")), "META-INF/ejb-jar.xml")
            .add(EmptyAsset.INSTANCE, "META-INF/beans.xml");
      JavaArchive toJar = ShrinkWrap.create(JavaArchive.class, "to.jar")
            .addClasses(EjbjarLibIntoWarLibTest.class, JaxRsActivator.class, UtilityProducer.class)
            .addClasses(ModulesResourceIntf.class, ModulesResource.class)
            .add(EmptyAsset.INSTANCE, "META-INF/beans.xml");
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-cdi-ejb-test.war")
            .addAsLibrary(toJar)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      
      EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
            .addAsLibrary(fromJar)
            .addAsModule(war);
      System.out.println(fromJar.toString(true));
      System.out.println(toJar.toString(true));
      System.out.println(war.toString(true));
      System.out.println(ear.toString(true));
      return ear;
   }

   /**
    */
   @Test
   public void testModules() throws Exception
   {
      log.info("starting testModules()");
      WebTarget target = ClientBuilder.newClient().target("http://localhost:8080/resteasy-cdi-ejb-test/rest/modules/test");
      Response response = target.request().get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.close();
   }
}
