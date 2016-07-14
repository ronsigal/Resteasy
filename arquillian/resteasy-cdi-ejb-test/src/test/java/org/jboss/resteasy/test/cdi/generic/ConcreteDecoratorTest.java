package org.jboss.resteasy.test.cdi.generic;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.generic.Animal;
import org.jboss.resteasy.cdi.generic.Australopithecus;
import org.jboss.resteasy.cdi.generic.ConcreteDecorator;
import org.jboss.resteasy.cdi.generic.ConcreteResource;
import org.jboss.resteasy.cdi.generic.ConcreteResourceIntf;
import org.jboss.resteasy.cdi.generic.GenericsProducer;
import org.jboss.resteasy.cdi.generic.HierarchyHolder;
import org.jboss.resteasy.cdi.generic.HierarchyTypedResource;
import org.jboss.resteasy.cdi.generic.HierarchyTypedResourceIntf;
import org.jboss.resteasy.cdi.generic.HolderBinding;
import org.jboss.resteasy.cdi.generic.JaxRsActivator;
import org.jboss.resteasy.cdi.generic.LowerBoundHierarchyHolder;
import org.jboss.resteasy.cdi.generic.LowerBoundTypedResource;
import org.jboss.resteasy.cdi.generic.LowerBoundTypedResourceIntf;
import org.jboss.resteasy.cdi.generic.NestedHierarchyHolder;
import org.jboss.resteasy.cdi.generic.NestedTypedResource;
import org.jboss.resteasy.cdi.generic.NestedTypedResourceIntf;
import org.jboss.resteasy.cdi.generic.ObjectHolder;
import org.jboss.resteasy.cdi.generic.ObjectTypedResource;
import org.jboss.resteasy.cdi.generic.ObjectTypedResourceIntf;
import org.jboss.resteasy.cdi.generic.Primate;
import org.jboss.resteasy.cdi.generic.UpperBoundHierarchyHolder;
import org.jboss.resteasy.cdi.generic.UpperBoundTypedResource;
import org.jboss.resteasy.cdi.generic.UpperBoundTypedResourceIntf;
import org.jboss.resteasy.cdi.generic.VisitList;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 14, 2012
 */
@RunWith(Arquillian.class)
public class ConcreteDecoratorTest
{
   @Inject private Logger log;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-cdi-ejb-test.war")
            .addClasses(JaxRsActivator.class, UtilityProducer.class, VisitList.class)
            .addClasses(GenericsProducer.class, HolderBinding.class)
            .addClasses(ConcreteResourceIntf.class, ConcreteResource.class)
            .addClasses(ObjectTypedResourceIntf.class, ObjectTypedResource.class)
            .addClasses(HierarchyTypedResourceIntf.class, HierarchyTypedResource.class)
            .addClasses(NestedTypedResourceIntf.class, NestedTypedResource.class)
            .addClasses(UpperBoundTypedResourceIntf.class, UpperBoundTypedResource.class)
            .addClasses(LowerBoundTypedResourceIntf.class, LowerBoundTypedResource.class)
            .addClasses(ObjectHolder.class, HierarchyHolder.class, NestedHierarchyHolder.class)
            .addClasses(UpperBoundHierarchyHolder.class, LowerBoundHierarchyHolder.class)
            .addClasses(Animal.class, Primate.class, Australopithecus.class)
            .addClasses(ConcreteDecorator.class)
            .addAsWebInfResource("generic/concrete_beans.xml", "beans.xml");
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   @Ignore
   public void testConcreteConcreteDecorator() throws Exception
   {
      log.info("starting testConcreteConcreteDecorator()");
      Client client = ClientBuilder.newClient();
//      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/concrete/decorators/clear");
//      ClientResponse<?>  response = request.get();
      Builder request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/concrete/decorators/clear").request();
      Response response = request.get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
//      request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/concrete/decorators/execute");
//      response = request.get();
      request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/concrete/decorators/execute").request();
      response = request.get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
//      request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/concrete/decorators/test");
      request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/concrete/decorators/test").request();
      response = request.get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.close();
   }
}
