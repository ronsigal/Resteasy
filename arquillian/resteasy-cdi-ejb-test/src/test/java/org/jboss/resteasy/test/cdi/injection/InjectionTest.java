package org.jboss.resteasy.test.cdi.injection;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.injection.Book;
import org.jboss.resteasy.cdi.injection.BookBag;
import org.jboss.resteasy.cdi.injection.BookBagLocal;
import org.jboss.resteasy.cdi.injection.BookCollection;
import org.jboss.resteasy.cdi.injection.BookReader;
import org.jboss.resteasy.cdi.injection.BookResource;
import org.jboss.resteasy.cdi.injection.BookWriter;
import org.jboss.resteasy.cdi.injection.DependentScoped;
import org.jboss.resteasy.cdi.injection.JaxRsActivator;
import org.jboss.resteasy.cdi.injection.NewBean;
import org.jboss.resteasy.cdi.injection.ResourceProducer;
import org.jboss.resteasy.cdi.injection.ScopeInheritingStereotype;
import org.jboss.resteasy.cdi.injection.ScopeStereotype;
import org.jboss.resteasy.cdi.injection.StatefulEJB;
import org.jboss.resteasy.cdi.injection.StereotypedApplicationScope;
import org.jboss.resteasy.cdi.injection.StereotypedDependentScope;
import org.jboss.resteasy.cdi.injection.UnscopedResource;
import org.jboss.resteasy.cdi.util.Constants;
import org.jboss.resteasy.cdi.util.Counter;
import org.jboss.resteasy.cdi.util.PersistenceUnitProducer;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This is a collection of tests addressed to the interactions of 
 * Resteasy, CDI, EJB, and so forth in the context of a JEE Application Server.
 * 
 * It tests the injection of a variety of beans into Resteasy objects.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@RunWith(Arquillian.class)
public class InjectionTest
{
   @Inject Logger log;
   
   private static int invocationCounter;
   
   static GenericType<Collection<Book>> BookCollectionType = new GenericType<Collection<Book>>() {};

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-cdi-ejb-test.war")
            .addClasses(Book.class, BookResource.class, JaxRsActivator.class, Constants.class, UtilityProducer.class, BookCollectionType.getClass())
            .addClasses(Counter.class, BookCollection.class, BookReader.class, BookWriter.class)
            .addClasses(DependentScoped.class, StatefulEJB.class, UnscopedResource.class)
            .addClasses(BookBagLocal.class, BookBag.class)
            .addClasses(NewBean.class)
            .addClasses(ScopeStereotype.class, ScopeInheritingStereotype.class)
            .addClasses(StereotypedApplicationScope.class, StereotypedDependentScope.class)
            .addClasses(Resource.class, ResourceProducer.class, PersistenceUnitProducer.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("injection/persistence.xml", "META-INF/persistence.xml");
      System.out.println(war.toString(true));
      return war;
   }

   @Before
   public void preparePersistenceTest() throws Exception
   {
      System.out.println("Dumping old records...");
      Builder request = ClientBuilder.newClient().target("http://localhost:8080/resteasy-cdi-ejb-test/rest/empty/").request();
      Response response = request.post(null);
      invocationCounter++;
      response.close();
   }

   /**
    * Addresses the correct handling of built-in scopes. E.g.
    * 
    * 1) Providers are in the application scope, whether they are annotated or not.
    * 2) Resources are in the request scope, annotated or not.
    * 3) Objects in the dependent scope, when injected into JAX-RS objects, are handled properly.
    * 4) Singletons in the application scope, when injected in request scoped JAX-RS resources as
    *    EJB proxies or Weld proxies, are handled properly.
    * 
    * A side effect of 3) and 4) is to test that beans managed by CDI (managed beans, singleton beans,
    * stateless EJBs) are injected properly into JAX-RS objects.
    */
   @Test
   public void testVerifyScopes() throws Exception
   {
      log.info("starting testVerifyScopes()");
      Builder request = ClientBuilder.newClient().target("http://localhost:8080/resteasy-cdi-ejb-test/rest/verifyScopes/").request();
      Response response = request.get();
      invocationCounter++;
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.close();
      response = request.get();
      invocationCounter++;
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.close();
   }

   /**
    * Addresses the injection of managed beans, singletons, and stateless EJBs into JAX-RS objects.
    * Uses a singleton (BookCollection) to interact with an EntityManager. 
    */
   @Test
   public void testEJBs() throws Exception
   {
      log.info("starting testEJBs()");
      Client client = ClientBuilder.newClient();
      
      // Create book.
      Builder request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/create/").request();
      Book book1 = new Book("RESTEasy: the Sequel");
      Response response = request.post(Entity.entity(book1, "application/test+xml"));
      invocationCounter++;
      assertEquals(200, response.getStatus());
      log.info("Status: " + response.getStatus());
      int id1 = response.readEntity(int.class);
      log.info("id: " + id1);
      Assert.assertEquals(Counter.INITIAL_VALUE, id1);

      // Create another book.
      request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/create/").request();
      Book book2 = new Book("RESTEasy: It's Alive");
      response = request.post(Entity.entity(book2, "application/test+xml"));
      invocationCounter++;
      assertEquals(200, response.getStatus());
      log.info("Status: " + response.getStatus());
      int id2 = response.readEntity(int.class);
      log.info("id: " + id2);
      Assert.assertEquals(Counter.INITIAL_VALUE + 1, id2);

      // Retrieve first book.
      request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/book/" + id1).request();
      request.accept("application/test+xml");
      response = request.get();
      invocationCounter++;
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      Book result = response.readEntity(Book.class);
      log.info("book: " + book1);
      Assert.assertEquals(book1, result);

      // Retrieve second book.
      request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/book/" + id2).request();
      request.accept("application/test+xml");
      response = request.get();
      invocationCounter++;
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      result = response.readEntity(Book.class);
      log.info("book: " + book2);
      Assert.assertEquals(book2, result);

      // Retrieve all books.
      request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/books/").request();
      request.accept(MediaType.APPLICATION_XML);
      response = request.get();
      invocationCounter++;
      log.info("Status: " + response.getStatus());
      Collection<Book> books  = response.readEntity(BookCollectionType);
      log.info("Collection: " + books);
      Assert.assertEquals(2, books.size());
      Iterator<Book> it = books.iterator();
      Book b1 = it.next();
      Book b2 = it.next();
      log.info("First book in list: " + b1);
      log.info("Second book in list: " + b2);
      Assert.assertTrue(book1.equals(b1) && book2.equals(b2) || book1.equals(b2) && book2.equals(b1));

      // Test EntityManager injected in BookResource
      request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/entityManager").request();
      response = request.post(null);
      invocationCounter++;
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.close();
   }

   /**
    * This test verifies that a session scoped SFSB survives throughout the course of a session and is
    * re-injected into the request scoped BookResource over the course of the session.  Also, it is destroyed
    * and replaced when an invocation is made on BookResource after the session ends.
    */
   @Test
   public void testSessionScope() throws Exception
   {
      log.info("starting testSessionScope()");

      // Need to supply each ClientRequest with a single ClientExecutor to maintain a single
      // cookie cache, which keeps the session alive.
      ClientHttpEngine engine = new ApacheHttpClient4Engine();
      Client client = new ResteasyClientBuilder().httpEngine(engine).build();

      // Create a book, which gets stored in the session scoped BookBag.
      Builder request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/session/add/").request();
      Book book1 = new Book(13, "Dead Man Napping");
      Response response = request.post(Entity.entity(book1, Constants.MEDIA_TYPE_TEST_XML));
      invocationCounter++;
      log.info("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      response.close();

      // Create another book, which should get stored in the same BookBag.
      request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/session/add/").request();
      Book book2 = new Book(Counter.INITIAL_VALUE, "Dead Man Dozing");
      response = request.post(Entity.entity(book2, Constants.MEDIA_TYPE_TEST_XML));
      invocationCounter++;
      log.info("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      response.close();

      // Get the current contents of the BookBag, and verify that it holds both of the books sent in the
      // previous two invocations.  When this method is called, the session is terminated.
      request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/session/get/").request();
      response = request.get();
      invocationCounter++;
      log.info("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      @SuppressWarnings("unchecked")
      Collection<Book> books = response.readEntity(BookCollectionType);
      log.info("Collection: " + books);
      Assert.assertEquals(2, books.size());
      Iterator<Book> it = books.iterator();
      Book b1 = it.next();
      Book b2 = it.next();
      log.info("First book in list: " + b1);
      log.info("Second book in list: " + b2);
      Assert.assertTrue(book1.equals(b1) && book2.equals(b2) || book1.equals(b2) && book2.equals(b1));

      // Verify that the BookBag has been replaced by a new, empty one for the new session.
      request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/session/test/").request();
      response = request.post(null);
      invocationCounter++;
      log.info("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }

   /**
    * Tests the injection of JMS Producers, Consumers, and Queues using producer fields and methods.
    */
   @Test
   public void testJMS() throws Exception
   {
      log.info("starting testJMS()");
      Client client = ClientBuilder.newClient();
      
      // Send a book title.
      Builder request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/produceMessage/").request();
      String title = "Dead Man Lounging";
      Book book = new Book(23, title);
      Response response = request.post(Entity.entity(book, Constants.MEDIA_TYPE_TEST_XML));
      invocationCounter++;
      log.info("status: " + response.getStatus());
      log.info(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      
      // Verify that the received book title is the one that was sent.
      request = client.target("http://localhost:8080/resteasy-cdi-ejb-test/rest/queue/consumeMessage/").request();
      log.info("consuming book");
      response = request.get();
      invocationCounter++;
      log.info("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(title, response.readEntity(String.class));
   }
   
   /**
    * Verifies that BookResource.postConstruct() and preDestroy() are called for each invocation.
    */
   @Test
   public void testPostConstructPreDestroy() throws Exception
   {
      log.info("starting testPostConstructPreDestroy()");
      
      // Send a book title.
      log.info("invocationCounter: " + invocationCounter);
      Builder request = ClientBuilder.newClient().target("http://localhost:8080/resteasy-cdi-ejb-test/rest/getCounters/").request();
      Response response = request.get();
      log.info("status: " + response.getStatus());
      String result = response.readEntity(String.class);
      log.info(result);
      Assert.assertEquals(200, response.getStatus());
      String[] counters = result.split(":");
      Assert.assertTrue(invocationCounter + 1 == Integer.valueOf(counters[0])); // invocations of postConstruct()
      Assert.assertTrue(invocationCounter == Integer.valueOf(counters[1]));     // invocations of preDestroy()
   }
   
   /**
    * Verifies that ResourceProducer disposer method has been called for Queue.
    */
   @Test
   public void testDisposer() throws Exception
   {
      log.info("starting testDisposer()");
      Builder request = ClientBuilder.newClient().target("http://localhost:8080/resteasy-cdi-ejb-test/rest/disposer/").request();
      Response response = request.get();
      log.info("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }
}
