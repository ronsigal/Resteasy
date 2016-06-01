package org.jboss.resteasy.test.nextgen.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-756
 *  
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @author Achim Bitzer
 * @version $Revision: 1.1 $
 *
 * Copyright Oct 12, 2012
 */
public class ParameterListTest
{
   protected ResteasyDeployment deployment;
   
   public interface TestInterface
   {
      @GET
      @Path("matrix/list")
      public abstract Response matrixList(@MatrixParam("m1") List<String> list);

      @GET
      @Path("matrix/set")
      public abstract Response matrixSet(@MatrixParam("m1") Set<String> set);
      
      @GET
      @Path("matrix/sortedset")
      public abstract Response matrixSortedSet(@MatrixParam("m1") SortedSet<String> set);
      
      @PUT
      @Consumes("text/plain")
      @Path("matrix/entity")
      public abstract Response matrixWithEntity(@MatrixParam("m1") List<String> list, String entity);

      @GET
      @Path("query/list")
      public abstract Response queryList(@QueryParam("q1") List<String> list);
      
      @GET
      @Path("query/set")
      public abstract Response querySet(@QueryParam("q1") Set<String> set);
      
      @GET
      @Path("query/sortedset")
      public abstract Response querySortedSet(@QueryParam("q1") SortedSet<String> set);
      
      @PUT
      @Consumes("text/plain")
      @Path("query/entity")
      public abstract Response queryWithEntity(@QueryParam("q1") List<String> list, String entity);
      
      @PUT
      @Consumes("text/plain")
      @Path("matrix/query/entity")
      public abstract Response matrixQueryWithEntity(@MatrixParam("m1") List<String> matrixParams, @QueryParam("q1") List<String> queryParams, String entity);
   }
   
   @Path("/")
   static public class TestResource implements TestInterface
   {
      @Override
      @GET
      @Path("matrix/list")
      public Response matrixList(@MatrixParam("m1") List<String> list)
      {
         System.out.println("entered matrixList()");
         StringBuilder sb = new StringBuilder();
         for (Iterator<String> it = list.iterator(); it.hasNext(); )
         {
            sb.append(it.next()).append(":");
         }
         return Response.ok().entity(sb.toString()).build();
      }
      
      @Override
      @GET
      @Path("matrix/set")
      public Response matrixSet(@MatrixParam("m1") Set<String> set)
      {
         System.out.println("entered matrixSet()");
         List<String> list = new ArrayList<String>(set);
         Collections.sort(list);
         StringBuilder sb = new StringBuilder();
         for (Iterator<String> it = list.iterator(); it.hasNext(); )
         {
            sb.append(it.next()).append(":");
         }
         return Response.ok().entity(sb.toString()).build();
      }
      
      @Override
      @GET
      @Path("matrix/sortedset")
      public Response matrixSortedSet(@MatrixParam("m1") SortedSet<String> set)
      {
         System.out.println("entered matrixSortedSet()");
         StringBuilder sb = new StringBuilder();
         for (Iterator<String> it = set.iterator(); it.hasNext(); )
         {
            sb.append(it.next()).append(":");
         }
         return Response.ok().entity(sb.toString()).build();
      }
      
      @Override
      @PUT
      @Consumes("text/plain")
      @Path("matrix/entity")
      public Response matrixWithEntity(@MatrixParam("m1") List<String> list, String entity)
      {
         System.out.println("entered matrixWithEntity()");
         StringBuilder sb = new StringBuilder(entity + ":");
         for (Iterator<String> it = list.iterator(); it.hasNext(); )
         {
            sb.append(it.next()).append(":");
         }
         return Response.ok().entity(sb.toString()).build();
      }
      
      @Override
      @GET
      @Path("query/list")
      public Response queryList(@QueryParam("q1") List<String> list)
      {
         System.out.println("entered queryList()");
         StringBuilder sb = new StringBuilder();
         for (Iterator<String> it = list.iterator(); it.hasNext(); )
         {
            sb.append(it.next()).append(":");
         }
         return Response.ok().entity(sb.toString()).build();
      }
      
      @Override
      @GET
      @Path("query/set")
      public Response querySet(@QueryParam("q1") Set<String> set)
      {
         System.out.println("entered querySet()");
         List<String> list = new ArrayList<String>(set);
         Collections.sort(list);
         StringBuilder sb = new StringBuilder();
         for (Iterator<String> it = list.iterator(); it.hasNext(); )
         {
            sb.append(it.next()).append(":");
         }
         return Response.ok().entity(sb.toString()).build();
      }
      
      @Override
      @GET
      @Path("query/sortedset")
      public Response querySortedSet(@QueryParam("q1") SortedSet<String> set)
      {
         System.out.println("entered querySortedSet()");
         StringBuilder sb = new StringBuilder();
         for (Iterator<String> it = set.iterator(); it.hasNext(); )
         {
            sb.append(it.next()).append(":");
         }
         return Response.ok().entity(sb.toString()).build();
      }
      
      @Override
      @PUT
      @Consumes("text/plain")
      @Path("query/entity")
      public Response queryWithEntity(@QueryParam("q1") List<String> list, String entity)
      {
         System.out.println("entered queryWithEntity()");
         StringBuilder sb = new StringBuilder(entity + ":");
         for (Iterator<String> it = list.iterator(); it.hasNext(); )
         {
            sb.append(it.next()).append(":");
         }
         return Response.ok().entity(sb.toString()).build();
      }
      
      @Override
      @PUT
      @Consumes("text/plain")
      @Path("matrix/query/entity")
      public Response matrixQueryWithEntity(@MatrixParam("m1") List<String> matrixParams, @QueryParam("q1") List<String> queryParams, String entity)
      {
         System.out.println("entered matrixQueryWithEntity()");
         StringBuilder sb = new StringBuilder(entity + ":");
         for (Iterator<String> it = matrixParams.iterator(); it.hasNext(); )
         {
            sb.append(it.next()).append(":");
         }
         for (Iterator<String> it = queryParams.iterator(); it.hasNext(); )
         {
            sb.append(it.next()).append(":");
         }
         return Response.ok().entity(sb.toString()).build();
      }
   }
   
   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      deployment = null;
   }

   @Test
   public void testMatrix() throws Exception
   {
      WebTarget target = ResteasyClientBuilder.newClient().target("http://localhost:8081/matrix;m1=a/list;m1=b;p2=c");
      System.out.println("Sending request");
      Response response = target.matrixParam("m1", "d").request().get();
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("a:b:d:", entity);
   }
   
   @Test
   public void testQuery() throws Exception
   {
      WebTarget target = ResteasyClientBuilder.newClient().target("http://localhost:8081/query/list?q1=a&q2=b&q1=c");
      System.out.println("Sending request");
      Response response = target.queryParam("q1", "d").request().get();
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("a:c:d:", entity);
   }
   
   @Test
   public void testMatrixProxyList() throws Exception
   {  
      ResteasyWebTarget target = (ResteasyWebTarget) ResteasyClientBuilder.newClient().target("http://localhost:8081");
      TestInterface client = target.proxy(TestInterface.class);
      ArrayList<String> list = new ArrayList<String>();
      list.add("a");
      list.add("b");
      list.add("c");
      Response response = client.matrixList(list);
      System.out.println("Sending request");
      System.out.println("status: " + response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("a:b:c:", entity);
   }
   
   @Test
   public void testMatrixProxySet() throws Exception
   {
      ResteasyWebTarget target = (ResteasyWebTarget) ResteasyClientBuilder.newClient().target("http://localhost:8081");
      TestInterface client = target.proxy(TestInterface.class);
      HashSet<String> set = new HashSet<String>();
      set.add("a");
      set.add("b");
      set.add("c");
      Response response = client.matrixSet(set);
      System.out.println("Sending request");
      System.out.println("status: " + response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("a:b:c:", entity);
   }

   @Test
   public void testMatrixProxySortedSet() throws Exception
   {
      ResteasyWebTarget target = (ResteasyWebTarget) ResteasyClientBuilder.newClient().target("http://localhost:8081");
      TestInterface client = target.proxy(TestInterface.class);
      TreeSet<String> set = new TreeSet<String>();
      set.add("a");
      set.add("b");
      set.add("c");
      Response response = client.matrixSortedSet(set);
      System.out.println("Sending request");
      System.out.println("status: " + response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("a:b:c:", entity);
   }

   @Test
   public void testMatrixWithEntityProxy() throws Exception
   {  
      ResteasyWebTarget target = (ResteasyWebTarget) ResteasyClientBuilder.newClient().target("http://localhost:8081");
      TestInterface client = target.proxy(TestInterface.class);
      ArrayList<String> list = new ArrayList<String>();
      list.add("a");
      list.add("b");
      list.add("c");
      Response response = client.matrixWithEntity(list, "entity");
      System.out.println("Sending request");
      System.out.println("status: " + response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("entity:a:b:c:", entity);
   }

   @Test
   public void testQueryProxyList() throws Exception
   {  
      ResteasyWebTarget target = (ResteasyWebTarget) ResteasyClientBuilder.newClient().target("http://localhost:8081");
      TestInterface client = target.proxy(TestInterface.class);
      ArrayList<String> list = new ArrayList<String>();
      list.add("a");
      list.add("b");
      list.add("c");
      Response response = client.queryList(list);
      System.out.println("Sending request");
      System.out.println("status: " + response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("a:b:c:", entity);
   }
   
   @Test
   public void testQueryProxySet() throws Exception
   {  
      ResteasyWebTarget target = (ResteasyWebTarget) ResteasyClientBuilder.newClient().target("http://localhost:8081");
      TestInterface client = target.proxy(TestInterface.class);
      HashSet<String> set = new HashSet<String>();
      set.add("a");
      set.add("b");
      set.add("c");
      Response response = client.querySet(set);
      System.out.println("Sending request");
      System.out.println("status: " + response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("a:b:c:", entity);
   }

   @Test
   public void testQueryProxySortedSet() throws Exception
   {  
      ResteasyWebTarget target = (ResteasyWebTarget) ResteasyClientBuilder.newClient().target("http://localhost:8081");
      TestInterface client = target.proxy(TestInterface.class);
      TreeSet<String> set = new TreeSet<String>();
      set.add("a");
      set.add("b");
      set.add("c");
      Response response = client.querySortedSet(set);
      System.out.println("Sending request");
      System.out.println("status: " + response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("a:b:c:", entity);
   }

   @Test
   public void testQueryWithEntityProxy() throws Exception
   {  
      ResteasyWebTarget target = (ResteasyWebTarget) ResteasyClientBuilder.newClient().target("http://localhost:8081");
      TestInterface client = target.proxy(TestInterface.class);
      ArrayList<String> list = new ArrayList<String>();
      list.add("a");
      list.add("b");
      list.add("c");
      Response response = client.queryWithEntity(list, "entity");
      System.out.println("Sending request");
      System.out.println("status: " + response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("entity:a:b:c:", entity);
   }
   
   @Test
   public void testMatrixQueryWithEntityProxy() throws Exception
   {  
      ResteasyWebTarget target = (ResteasyWebTarget) ResteasyClientBuilder.newClient().target("http://localhost:8081");
      TestInterface client = target.proxy(TestInterface.class);
      ArrayList<String> matrixParams = new ArrayList<String>();
      matrixParams.add("a");
      matrixParams.add("b");
      matrixParams.add("c");
      ArrayList<String> queryParams = new ArrayList<String>();
      queryParams.add("x");
      queryParams.add("y");
      queryParams.add("z");
      Response response = client.matrixQueryWithEntity(matrixParams, queryParams, "entity");
      System.out.println("Sending request");
      System.out.println("status: " + response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("entity:a:b:c:x:y:z:", entity);
   }
}
