package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterConstructorClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterFromStringClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterParamConverterClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterValueOfClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterParamConverter;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterParamConverterProvider;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterCookieResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterHeaderResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterMatrixResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterMiscResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterPathResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterQueryResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEasy extended support for multivalue @*Param (RESTEASY-1566 + RESTEASY-1746)
 *                    org.jboss.resteasy.test.resource.param.resource.MultiValuedParamPersonWithConverter class is used
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MultiValuedParamDefaultParamConverterTest {

   private static Client client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(MultiValuedParamDefaultParamConverterTest.class.getSimpleName());
      war.addClass(MultiValuedParamDefaultParamConverterConstructorClass.class);
      war.addClass(MultiValuedParamDefaultParamConverterFromStringClass.class);
      war.addClass(MultiValuedParamDefaultParamConverterParamConverterClass.class);
      war.addClass(MultiValuedParamDefaultParamConverterValueOfClass.class);
      war.addClass(MultiValuedParamDefaultParamConverterParamConverter.class);
      return TestUtil.finishContainerPrepare(war, null, MultiValuedParamDefaultParamConverterParamConverterProvider.class,
         MultiValuedParamDefaultParamConverterCookieResource.class, MultiValuedParamDefaultParamConverterHeaderResource.class,
         MultiValuedParamDefaultParamConverterMatrixResource.class, MultiValuedParamDefaultParamConverterMiscResource.class,
         MultiValuedParamDefaultParamConverterPathResource.class, MultiValuedParamDefaultParamConverterQueryResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, MultiValuedParamDefaultParamConverterTest.class.getSimpleName());
   }

   @BeforeClass
   public static void beforeClass() throws Exception {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void afterClass() throws Exception {
      client.close();
   }

   /////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * @tpTestDetails QueryParam test
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testCookie() {

      doTestCookie("constructor", "separator", "#", "list");
      doTestCookie("constructor", "separator", "#", "set");
      doTestCookie("constructor", "separator", "#", "sortedset");
      doTestCookie("constructor", "regex",     "#", "list");
      doTestCookie("constructor", "regex",     "#", "set");
      doTestCookie("constructor", "regex",     "#", "sortedset");
      doTestCookie("constructor", "default",   "-", "list");
      doTestCookie("constructor", "default",   "-", "set");
      doTestCookie("constructor", "default",   "-", "sortedset");

      doTestCookie("valueOf", "separator", "#", "list");
      doTestCookie("valueOf", "separator", "#", "set");
      doTestCookie("valueOf", "separator", "#", "sortedset");
      doTestCookie("valueOf", "regex",     "#", "list");
      doTestCookie("valueOf", "regex",     "#", "set");
      doTestCookie("valueOf", "regex",     "#", "sortedset");
      doTestCookie("valueOf", "default",   "-", "list");
      doTestCookie("valueOf", "default",   "-", "set");
      doTestCookie("valueOf", "default",   "-", "sortedset");

      doTestCookie("fromString", "separator", "#", "list");
      doTestCookie("fromString", "separator", "#", "set");
      doTestCookie("fromString", "separator", "#", "sortedset");
      doTestCookie("fromString", "regex",     "#", "list");
      doTestCookie("fromString", "regex",     "#", "set");
      doTestCookie("fromString", "regex",     "#", "sortedset");
      doTestCookie("fromString", "default",   "-", "list");
      doTestCookie("fromString", "default",   "-", "set");
      doTestCookie("fromString", "default",   "-", "sortedset");

      doTestCookie("paramConverter", "separator", "#", "list");
      doTestCookie("paramConverter", "separator", "#", "set");
      doTestCookie("paramConverter", "separator", "#", "sortedset");
      doTestCookie("paramConverter", "regex",     "#", "list");
      doTestCookie("paramConverter", "regex",     "#", "set");
      doTestCookie("paramConverter", "regex",     "#", "sortedset");
      doTestCookie("paramConverter", "default",   "-", "list");
      doTestCookie("paramConverter", "default",   "-", "set");
      doTestCookie("paramConverter", "default",   "-", "sortedset");
   }

   /**
    * @tpTestDetails QueryParam test
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testHeader() {

      doTestHeader("constructor", "separator", "-", "list");
      doTestHeader("constructor", "separator", "-", "set");
      doTestHeader("constructor", "separator", "-", "sortedset");
      doTestHeader("constructor", "regex",     "-", "list");
      doTestHeader("constructor", "regex",     "-", "set");
      doTestHeader("constructor", "regex",     "-", "sortedset");
      doTestHeader("constructor", "default",   ",", "list");
      doTestHeader("constructor", "default",   ",", "set");
      doTestHeader("constructor", "default",   ",", "sortedset");

      doTestHeader("valueOf", "separator", "-", "list");
      doTestHeader("valueOf", "separator", "-", "set");
      doTestHeader("valueOf", "separator", "-", "sortedset");
      doTestHeader("valueOf", "regex",     "-", "list");
      doTestHeader("valueOf", "regex",     "-", "set");
      doTestHeader("valueOf", "regex",     "-", "sortedset");
      doTestHeader("valueOf", "default",   ",", "list");
      doTestHeader("valueOf", "default",   ",", "set");
      doTestHeader("valueOf", "default",   ",", "sortedset");

      doTestHeader("fromString", "separator", "-", "list");
      doTestHeader("fromString", "separator", "-", "set");
      doTestHeader("fromString", "separator", "-", "sortedset");
      doTestHeader("fromString", "regex",     "-", "list");
      doTestHeader("fromString", "regex",     "-", "set");
      doTestHeader("fromString", "regex",     "-", "sortedset");
      doTestHeader("fromString", "default",   ",", "list");
      doTestHeader("fromString", "default",   ",", "set");
      doTestHeader("fromString", "default",   ",", "sortedset");

      doTestHeader("paramConverter", "separator", "-", "list");
      doTestHeader("paramConverter", "separator", "-", "set");
      doTestHeader("paramConverter", "separator", "-", "sortedset");
      doTestHeader("paramConverter", "regex",     "-", "list");
      doTestHeader("paramConverter", "regex",     "-", "set");
      doTestHeader("paramConverter", "regex",     "-", "sortedset");
      doTestHeader("paramConverter", "default",   ",", "list");
      doTestHeader("paramConverter", "default",   ",", "set");
      doTestHeader("paramConverter", "default",   ",", "sortedset");
   }

   /**
    * @tpTestDetails QueryParam test
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testMatrix() {

      doTestMatrix("constructor", "separator", "-", "list");
      doTestMatrix("constructor", "separator", "-", "set");
      doTestMatrix("constructor", "separator", "-", "sortedset");
      doTestMatrix("constructor", "regex",     "-", "list");
      doTestMatrix("constructor", "regex",     "-", "set");
      doTestMatrix("constructor", "regex",     "-", "sortedset");
      doTestMatrix("constructor", "default",   ",", "list");
      doTestMatrix("constructor", "default",   ",", "set");
      doTestMatrix("constructor", "default",   ",", "sortedset");

      doTestMatrix("valueOf", "separator", "-", "list");
      doTestMatrix("valueOf", "separator", "-", "set");
      doTestMatrix("valueOf", "separator", "-", "sortedset");
      doTestMatrix("valueOf", "regex",     "-", "list");
      doTestMatrix("valueOf", "regex",     "-", "set");
      doTestMatrix("valueOf", "regex",     "-", "sortedset");
      doTestMatrix("valueOf", "default",   ",", "list");
      doTestMatrix("valueOf", "default",   ",", "set");
      doTestMatrix("valueOf", "default",   ",", "sortedset");

      doTestMatrix("fromString", "separator", "-", "list");
      doTestMatrix("fromString", "separator", "-", "set");
      doTestMatrix("fromString", "separator", "-", "sortedset");
      doTestMatrix("fromString", "regex",     "-", "list");
      doTestMatrix("fromString", "regex",     "-", "set");
      doTestMatrix("fromString", "regex",     "-", "sortedset");
      doTestMatrix("fromString", "default",   ",", "list");
      doTestMatrix("fromString", "default",   ",", "set");
      doTestMatrix("fromString", "default",   ",", "sortedset");

      doTestMatrix("paramConverter", "separator", "-", "list");
      doTestMatrix("paramConverter", "separator", "-", "set");
      doTestMatrix("paramConverter", "separator", "-", "sortedset");
      doTestMatrix("paramConverter", "regex",     "-", "list");
      doTestMatrix("paramConverter", "regex",     "-", "set");
      doTestMatrix("paramConverter", "regex",     "-", "sortedset");
      doTestMatrix("paramConverter", "default",   ",", "list");
      doTestMatrix("paramConverter", "default",   ",", "set");
      doTestMatrix("paramConverter", "default",   ",", "sortedset");
   }

   /**
    * @tpTestDetails QueryParam test
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testPath() {

      doTestPath("constructor", "separator", "-", "list");
      doTestPath("constructor", "separator", "-", "set");
      doTestPath("constructor", "separator", "-", "sortedset");
      doTestPath("constructor", "regex",     "-", "list");
      doTestPath("constructor", "regex",     "-", "set");
      doTestPath("constructor", "regex",     "-", "sortedset");
      doTestPath("constructor", "default",   ",", "list");
      doTestPath("constructor", "default",   ",", "set");
      doTestPath("constructor", "default",   ",", "sortedset");

      doTestPath("valueOf", "separator", "-", "list");
      doTestPath("valueOf", "separator", "-", "set");
      doTestPath("valueOf", "separator", "-", "sortedset");
      doTestPath("valueOf", "regex",     "-", "list");
      doTestPath("valueOf", "regex",     "-", "set");
      doTestPath("valueOf", "regex",     "-", "sortedset");
      doTestPath("valueOf", "default",   ",", "list");
      doTestPath("valueOf", "default",   ",", "set");
      doTestPath("valueOf", "default",   ",", "sortedset");

      doTestPath("fromString", "separator", "-", "list");
      doTestPath("fromString", "separator", "-", "set");
      doTestPath("fromString", "separator", "-", "sortedset");
      doTestPath("fromString", "regex",     "-", "list");
      doTestPath("fromString", "regex",     "-", "set");
      doTestPath("fromString", "regex",     "-", "sortedset");
      doTestPath("fromString", "default",   ",", "list");
      doTestPath("fromString", "default",   ",", "set");
      doTestPath("fromString", "default",   ",", "sortedset");

      doTestPath("paramConverter", "separator", "-", "list");
      doTestPath("paramConverter", "separator", "-", "set");
      doTestPath("paramConverter", "separator", "-", "sortedset");
      doTestPath("paramConverter", "regex",     "-", "list");
      doTestPath("paramConverter", "regex",     "-", "set");
      doTestPath("paramConverter", "regex",     "-", "sortedset");
      doTestPath("paramConverter", "default",   ",", "list");
      doTestPath("paramConverter", "default",   ",", "set");
      doTestPath("paramConverter", "default",   ",", "sortedset");
   }

   /**
    * @tpTestDetails QueryParam test
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testQuery() {

      doTestQuery("constructor", "separator", "-", "list");
      doTestQuery("constructor", "separator", "-", "set");
      doTestQuery("constructor", "separator", "-", "sortedset");
      doTestQuery("constructor", "regex",     "-", "list");
      doTestQuery("constructor", "regex",     "-", "set");
      doTestQuery("constructor", "regex",     "-", "sortedset");
      doTestQuery("constructor", "default",   ",", "list");
      doTestQuery("constructor", "default",   ",", "set");
      doTestQuery("constructor", "default",   ",", "sortedset");

      doTestQuery("valueOf", "separator", "-", "list");
      doTestQuery("valueOf", "separator", "-", "set");
      doTestQuery("valueOf", "separator", "-", "sortedset");
      doTestQuery("valueOf", "regex",     "-", "list");
      doTestQuery("valueOf", "regex",     "-", "set");
      doTestQuery("valueOf", "regex",     "-", "sortedset");
      doTestQuery("valueOf", "default",   ",", "list");
      doTestQuery("valueOf", "default",   ",", "set");
      doTestQuery("valueOf", "default",   ",", "sortedset");

      doTestQuery("fromString", "separator", "-", "list");
      doTestQuery("fromString", "separator", "-", "set");
      doTestQuery("fromString", "separator", "-", "sortedset");
      doTestQuery("fromString", "regex",     "-", "list");
      doTestQuery("fromString", "regex",     "-", "set");
      doTestQuery("fromString", "regex",     "-", "sortedset");
      doTestQuery("fromString", "default",   ",", "list");
      doTestQuery("fromString", "default",   ",", "set");
      doTestQuery("fromString", "default",   ",", "sortedset");

      doTestQuery("paramConverter", "separator", "-", "list");
      doTestQuery("paramConverter", "separator", "-", "set");
      doTestQuery("paramConverter", "separator", "-", "sortedset");
      doTestQuery("paramConverter", "regex",     "-", "list");
      doTestQuery("paramConverter", "regex",     "-", "set");
      doTestQuery("paramConverter", "regex",     "-", "sortedset");
      doTestQuery("paramConverter", "default",   ",", "list");
      doTestQuery("paramConverter", "default",   ",", "set");
      doTestQuery("paramConverter", "default",   ",", "sortedset");
   }

   /**
    * @tpTestDetails QueryParam test
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testWhiteSpace() {
      String response = client.target(generateURL("/misc/whitespace")).queryParam("w", " w1 - w2 ").request().get(String.class);
      System.out.println("response(ws): " + response);
      Assert.assertEquals("w1|w2|", response);
   }

   /**
    * @tpTestDetails QueryParam test
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testPrecedence() {
      String response = client.target(generateURL("/misc/precedence")).queryParam("p", "p1;p2").request().get(String.class);
      System.out.println("response(p): " + response);
      Assert.assertEquals("p1|p2|", response);
   }

   /////////////////////////////////////////////////////////////////////////////////////////////
   void doTestCookie(String conversion, String format, String separator, String clazz) {
      String response = client.target(generateURL("/cookie/" + conversion + "/" + format + "/" + clazz)).request().cookie("c", "c1" + separator + "c2").get(String.class);
      System.out.println("response: " + response);
      Assert.assertEquals("c1|c2|", response);
   }

   void doTestHeader(String conversion, String format, String separator, String clazz) {
      String response = client.target(generateURL("/header/" + conversion + "/" + format + "/" + clazz)).request().header("h", "h1" + separator + "h2").get(String.class);
      System.out.println("response: " + response);
      Assert.assertEquals("h1|h2|", response);
   }

   void doTestMatrix(String conversion, String format, String separator, String clazz) {
      String response = client.target(generateURL("/matrix/" + conversion + "/" + format + "/" + clazz)).matrixParam("m", "m1" + separator + "m2").request().get(String.class);
      System.out.println("response: " + response);
      Assert.assertEquals("m1|m2|", response);
   }

   void doTestPath(String conversion, String format, String separator, String clazz) {
      String response = client.target(generateURL("/path/" + conversion + "/" + format + "/" + clazz + "/p1" + separator + "p2")).request().get(String.class);
      System.out.println("response: " + response);
      Assert.assertEquals("p1|p2|", response);
   }

   void doTestQuery(String conversion, String format, String separator, String clazz) {
      String response = client.target(generateURL("/query/" + conversion + "/" + format + "/" + clazz)).queryParam("q", "q1" + separator + "q2").request().get(String.class);
      System.out.println("response2: " + response);
      Assert.assertEquals("q1|q2|", response);
   }
}
