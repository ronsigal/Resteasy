package org.jboss.resteasy.test.xxe;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.xml.bind.annotation.XmlRootElement;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Test;

/**
 * Unit tests for RESTEASY-647.
 * 
 * Idea for test comes from Tim McCune: 
 * http://jersey.576304.n2.nabble.com/Jersey-vulnerable-to-XXE-attack-td3214584.html
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Jan 6, 2012
 */
abstract public class AbstractTestXXE
{
   @XmlRootElement
   public static class FavoriteMovieXmlRootElement {
     private String _title;
     public String getTitle() {
       return _title;
     }
     public void setTitle(String title) {
       _title = title;
     }
   }

   abstract public void before(String expandEntityReferences) throws Exception;

   abstract public void before() throws Exception;
   
   abstract public void after() throws Exception;

//   @Test
   public void testXmlRootElementDefault() throws Exception
   {
      before();
      System.out.println("contacting: " + generateURL("/xmlRootElement"));
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      after();
   }
   
//   @Test
   public void testXmlRootElementWithoutExpansion() throws Exception
   {
      before("false");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }

   @Test
   public void testXmlRootElementWithExpansion() throws Exception
   {
      before("true");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>";

      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      after();
   }

   @Test
   public void testXmlTypeDefault() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovie><title>&xxe;</title></favoriteMovie>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      after();
   }
   
   @Test
   public void testXmlTypeWithoutExpansion() throws Exception
   {
      before("false");
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovie><title>&xxe;</title></favoriteMovie>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }

   @Test
   public void testXmlTypeWithExpansion() throws Exception
   {
      before("true");
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovie><title>&xxe;</title></favoriteMovie>";

      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      after();
   }
   
   @Test
   public void testJAXBElementDefault() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovieXmlType><title>&xxe;</title></favoriteMovieXmlType>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      after();
   }
   
   @Test
   public void testJAXBElementWithoutExpansion() throws Exception
   {
      before("false");
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovieXmlType><title>&xxe;</title></favoriteMovieXmlType>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   @Test
   public void testJAXBElementWithExpansion() throws Exception
   {
      before("true");
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovieXmlType><title>&xxe;</title></favoriteMovieXmlType>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      after();
   }
   
   @Test
   public void testListDefault() throws Exception
   {
      doCollectionTest(null, "list");
   }
   
   @Test
   public void testListWithoutExpansion() throws Exception
   {
      doCollectionTest(false, "list");
   }

   @Test
   public void testListWithExpansion() throws Exception
   {
      doCollectionTest(true, "list");
   }
   
   @Test
   public void testSetDefault() throws Exception
   {
      doCollectionTest(null, "set");
   }
   
   @Test
   public void testSetWithoutExpansion() throws Exception
   {
      doCollectionTest(false, "set");
   }

   @Test
   public void testSetWithExpansion() throws Exception
   {
      doCollectionTest(true, "set");
   }
   
   @Test
   public void testArrayDefault() throws Exception
   {
      doCollectionTest(null, "array");
   }
   
   @Test
   public void testArrayWithoutExpansion() throws Exception
   {
      doCollectionTest(false, "array");
   }

   @Test
   public void testArrayWithExpansion() throws Exception
   {
      doCollectionTest(true, "array");
   }

   @Test
   public void testMapDefault() throws Exception
   {
      doMapTest(null);
   }
   
   @Test
   public void testMapWithoutExpansion() throws Exception
   {
      doMapTest(false);
   }
   
   @Test
   public void testMapWithExpansion() throws Exception
   {
      doMapTest(true);
   }
   
   void doCollectionTest(Boolean expand, String path) throws Exception
   {
      if (expand == null)
      {
         before();
         expand = true;
      }
      else
      {
         before(Boolean.toString(expand));
      }
      ClientRequest request = new ClientRequest(generateURL("/" + path));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<collection>" +
                   "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>" +
                   "<favoriteMovieXmlRootElement><title>Le Regle de Jeu</title></favoriteMovieXmlRootElement>" +
                   "</collection>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      if (expand)
      {
         Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      }
      else
      {
         Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      }
      after();
   }
   
   void doMapTest(Boolean expand) throws Exception
   {
      if (expand == null)
      {
         before();
         expand = true;
      }
      else
      {
         before(Boolean.toString(expand));  
      }
      ClientRequest request = new ClientRequest(generateURL("/map"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<map>" +
                      "<entry key=\"american\">" +
                         "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>" +
                      "</entry>" +
                      "<entry key=\"french\">" +
                         "<favoriteMovieXmlRootElement><title>La Regle de Jeu</title></favoriteMovieXmlRootElement>" +
                      "</entry>" +
                   "</map>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      if (expand)
      {
         Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      }
      else
      {
         Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      }
      after();
   }
}
