package org.jboss.resteasy.test.resource.param;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterConstructorClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterCookieResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterCookieResourceIntf;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterFromStringClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterHeaderResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterHeaderResourceIntf;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterMatrixResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterMatrixResourceIntf;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterMiscResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterMiscResourceIntf;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterParamConverter;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterParamConverterClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterParamConverterProvider;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterPathResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterPathResourceIntf;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterQueryResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterQueryResourceIntf;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterValueOfClass;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEasy extended support for multivalue @*Param (RESTEASY-1566 + RESTEASY-1746)
 *                    org.jboss.resteasy.test.resource.param.resource.MultiValuedParamPersonWithConverter class is used
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MultiValuedParamDefaultParamConverterProxyTest {

   private static ResteasyClient client;
   private static MultiValuedParamDefaultParamConverterCookieResourceIntf cookieProxy;
   private static MultiValuedParamDefaultParamConverterHeaderResourceIntf headerProxy;
   private static MultiValuedParamDefaultParamConverterMatrixResourceIntf matrixProxy;
//   private static MultiValuedParamDefaultParamConverterMiscResourceIntf   miscProxy;
   private static MultiValuedParamDefaultParamConverterPathResourceIntf   pathProxy;
   private static MultiValuedParamDefaultParamConverterQueryResourceIntf  queryProxy;
   
   private static List<MultiValuedParamDefaultParamConverterConstructorClass>         list_constructor         = new ArrayList<MultiValuedParamDefaultParamConverterConstructorClass>();
   private static Set<MultiValuedParamDefaultParamConverterConstructorClass>          set_constructor          = new HashSet<MultiValuedParamDefaultParamConverterConstructorClass>();
   private static SortedSet<MultiValuedParamDefaultParamConverterConstructorClass>    sortedSet_constructor    = new TreeSet<MultiValuedParamDefaultParamConverterConstructorClass>();
   private static List<MultiValuedParamDefaultParamConverterValueOfClass>             list_valueOf             = new ArrayList<MultiValuedParamDefaultParamConverterValueOfClass>();
   private static Set<MultiValuedParamDefaultParamConverterValueOfClass>              set_valueOf              = new HashSet<MultiValuedParamDefaultParamConverterValueOfClass>();
   private static SortedSet<MultiValuedParamDefaultParamConverterValueOfClass>        sortedSet_valueOf        = new TreeSet<MultiValuedParamDefaultParamConverterValueOfClass>();
   private static List<MultiValuedParamDefaultParamConverterFromStringClass>          list_fromString          = new ArrayList<MultiValuedParamDefaultParamConverterFromStringClass>();
   private static Set<MultiValuedParamDefaultParamConverterFromStringClass>           set_fromString           = new HashSet<MultiValuedParamDefaultParamConverterFromStringClass>();
   private static SortedSet<MultiValuedParamDefaultParamConverterFromStringClass>     sortedSet_fromString     = new TreeSet<MultiValuedParamDefaultParamConverterFromStringClass>();
   private static List<MultiValuedParamDefaultParamConverterParamConverterClass>      list_paramConverter      = new ArrayList<MultiValuedParamDefaultParamConverterParamConverterClass>();
   private static Set<MultiValuedParamDefaultParamConverterParamConverterClass>       set_paramConverter       = new HashSet<MultiValuedParamDefaultParamConverterParamConverterClass>();
   private static SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> sortedSet_paramConverter = new TreeSet<MultiValuedParamDefaultParamConverterParamConverterClass>();
   
   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(MultiValuedParamDefaultParamConverterProxyTest.class.getSimpleName());
      war.addClass(MultiValuedParamDefaultParamConverterConstructorClass.class);
      war.addClass(MultiValuedParamDefaultParamConverterFromStringClass.class);
      war.addClass(MultiValuedParamDefaultParamConverterParamConverterClass.class);
      war.addClass(MultiValuedParamDefaultParamConverterValueOfClass.class);
      war.addClass(MultiValuedParamDefaultParamConverterParamConverter.class);
      war.addClass(MultiValuedParamDefaultParamConverterCookieResourceIntf.class);
      war.addClass(MultiValuedParamDefaultParamConverterHeaderResourceIntf.class);
      war.addClass(MultiValuedParamDefaultParamConverterMatrixResourceIntf.class);
      war.addClass(MultiValuedParamDefaultParamConverterMiscResourceIntf.class);
      war.addClass(MultiValuedParamDefaultParamConverterPathResourceIntf.class);
      war.addClass(MultiValuedParamDefaultParamConverterQueryResourceIntf.class);
      return TestUtil.finishContainerPrepare(war, null, MultiValuedParamDefaultParamConverterParamConverterProvider.class,
         MultiValuedParamDefaultParamConverterCookieResource.class, MultiValuedParamDefaultParamConverterHeaderResource.class,
         MultiValuedParamDefaultParamConverterMatrixResource.class, MultiValuedParamDefaultParamConverterMiscResource.class,
         MultiValuedParamDefaultParamConverterPathResource.class, MultiValuedParamDefaultParamConverterQueryResource.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, MultiValuedParamDefaultParamConverterProxyTest.class.getSimpleName());
   }

   @BeforeClass
   public static void beforeClass() throws Exception {
      client = (ResteasyClient) ClientBuilder.newClient();
      ResteasyWebTarget target = client.target(generateURL(""));
      cookieProxy = target.proxy(MultiValuedParamDefaultParamConverterCookieResourceIntf.class);
      headerProxy = target.proxy(MultiValuedParamDefaultParamConverterHeaderResourceIntf.class);
      matrixProxy = target.proxy(MultiValuedParamDefaultParamConverterMatrixResourceIntf.class);
//      miscProxy   = target.proxy(MultiValuedParamDefaultParamConverterMiscResourceIntf.class);
      pathProxy   = target.proxy(MultiValuedParamDefaultParamConverterPathResourceIntf.class);
      queryProxy  = target.proxy(MultiValuedParamDefaultParamConverterQueryResourceIntf.class);
      
      MultiValuedParamDefaultParamConverterConstructorClass c1_constructor = new MultiValuedParamDefaultParamConverterConstructorClass("c1");
      MultiValuedParamDefaultParamConverterConstructorClass c2_constructor = new MultiValuedParamDefaultParamConverterConstructorClass("c2");
      list_constructor.add(c1_constructor);
      list_constructor.add(c2_constructor);
      set_constructor.add(c1_constructor);
      set_constructor.add(c2_constructor);
      sortedSet_constructor.add(c1_constructor);
      sortedSet_constructor.add(c2_constructor);
      
      MultiValuedParamDefaultParamConverterValueOfClass c1_valueOf = new MultiValuedParamDefaultParamConverterValueOfClass("c1");
      MultiValuedParamDefaultParamConverterValueOfClass c2_valueOf = new MultiValuedParamDefaultParamConverterValueOfClass("c2");
      list_valueOf.add(c1_valueOf);
      list_valueOf.add(c2_valueOf);
      set_valueOf.add(c1_valueOf);
      set_valueOf.add(c2_valueOf);
      sortedSet_valueOf.add(c1_valueOf);
      sortedSet_valueOf.add(c2_valueOf);
      
      MultiValuedParamDefaultParamConverterFromStringClass c1_fromString = new MultiValuedParamDefaultParamConverterFromStringClass("c1");
      MultiValuedParamDefaultParamConverterFromStringClass c2_fromString = new MultiValuedParamDefaultParamConverterFromStringClass("c2");
      list_fromString.add(c1_fromString);
      list_fromString.add(c2_fromString);
      set_fromString.add(c1_fromString);
      set_fromString.add(c2_fromString);
      sortedSet_fromString.add(c1_fromString);
      sortedSet_fromString.add(c2_fromString);
      
      MultiValuedParamDefaultParamConverterParamConverterClass c1_paramConverter = new MultiValuedParamDefaultParamConverterParamConverterClass("c1");
      MultiValuedParamDefaultParamConverterParamConverterClass c2_paramConverter = new MultiValuedParamDefaultParamConverterParamConverterClass("c2");
      list_paramConverter.add(c1_paramConverter);
      list_paramConverter.add(c2_paramConverter);
      set_paramConverter.add(c1_paramConverter);
      set_paramConverter.add(c2_paramConverter);
      sortedSet_paramConverter.add(c1_paramConverter);
      sortedSet_paramConverter.add(c2_paramConverter);
   }

   @AfterClass
   public static void afterClass() throws Exception {
      client.close();
   }

   /////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * @tpTestDetails CookieParam test
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testCookie() {

      Assert.assertEquals("c1|c2|", cookieProxy.cookieConstructorSeparatorList(list_constructor));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieConstructorSeparatorSet(set_constructor));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieConstructorSeparatorSortedSet(sortedSet_constructor));
      
      Assert.assertEquals("c1|c2|", cookieProxy.cookieConstructorDefaultList(list_constructor));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieConstructorDefaultSet(set_constructor));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieConstructorDefaultSortedSet(sortedSet_constructor)); 
      
      Assert.assertEquals("c1|c2|", cookieProxy.cookieValueOfSeparatorList(list_valueOf));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieValueOfSeparatorSet(set_valueOf));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieValueOfSeparatorSortedSet(sortedSet_valueOf));
      
      Assert.assertEquals("c1|c2|", cookieProxy.cookieValueOfDefaultList(list_valueOf));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieValueOfDefaultSet(set_valueOf));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieValueOfDefaultSortedSet(sortedSet_valueOf)); 
      
      Assert.assertEquals("c1|c2|", cookieProxy.cookieFromStringSeparatorList(list_fromString));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieFromStringSeparatorSet(set_fromString));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieFromStringSeparatorSortedSet(sortedSet_fromString));
      
      Assert.assertEquals("c1|c2|", cookieProxy.cookieFromStringDefaultList(list_fromString));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieFromStringDefaultSet(set_fromString));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieFromStringDefaultSortedSet(sortedSet_fromString)); 
      
      Assert.assertEquals("c1|c2|", cookieProxy.cookieParamConverterSeparatorList(list_paramConverter));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieParamConverterSeparatorSet(set_paramConverter));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieParamConverterSeparatorSortedSet(sortedSet_paramConverter));
      
      Assert.assertEquals("c1|c2|", cookieProxy.cookieParamConverterDefaultList(list_paramConverter));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieParamConverterDefaultSet(set_paramConverter));
      Assert.assertEquals("c1|c2|", cookieProxy.cookieParamConverterDefaultSortedSet(sortedSet_paramConverter));
   }
   
   /**
    * @tpTestDetails HeaderParam test
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testHeader() {

      Assert.assertEquals("c1|c2|", headerProxy.headerConstructorSeparatorList(list_constructor));
      Assert.assertEquals("c1|c2|", headerProxy.headerConstructorSeparatorSet(set_constructor));
      Assert.assertEquals("c1|c2|", headerProxy.headerConstructorSeparatorSortedSet(sortedSet_constructor));
      
      Assert.assertEquals("c1|c2|", headerProxy.headerConstructorDefaultList(list_constructor));
      Assert.assertEquals("c1|c2|", headerProxy.headerConstructorDefaultSet(set_constructor));
      Assert.assertEquals("c1|c2|", headerProxy.headerConstructorDefaultSortedSet(sortedSet_constructor)); 
      
      Assert.assertEquals("c1|c2|", headerProxy.headerValueOfSeparatorList(list_valueOf));
      Assert.assertEquals("c1|c2|", headerProxy.headerValueOfSeparatorSet(set_valueOf));
      Assert.assertEquals("c1|c2|", headerProxy.headerValueOfSeparatorSortedSet(sortedSet_valueOf));
      
      Assert.assertEquals("c1|c2|", headerProxy.headerValueOfDefaultList(list_valueOf));
      Assert.assertEquals("c1|c2|", headerProxy.headerValueOfDefaultSet(set_valueOf));
      Assert.assertEquals("c1|c2|", headerProxy.headerValueOfDefaultSortedSet(sortedSet_valueOf)); 
      
      Assert.assertEquals("c1|c2|", headerProxy.headerFromStringSeparatorList(list_fromString));
      Assert.assertEquals("c1|c2|", headerProxy.headerFromStringSeparatorSet(set_fromString));
      Assert.assertEquals("c1|c2|", headerProxy.headerFromStringSeparatorSortedSet(sortedSet_fromString));
      
      Assert.assertEquals("c1|c2|", headerProxy.headerFromStringDefaultList(list_fromString));
      Assert.assertEquals("c1|c2|", headerProxy.headerFromStringDefaultSet(set_fromString));
      Assert.assertEquals("c1|c2|", headerProxy.headerFromStringDefaultSortedSet(sortedSet_fromString)); 
      
      Assert.assertEquals("c1|c2|", headerProxy.headerParamConverterSeparatorList(list_paramConverter));
      Assert.assertEquals("c1|c2|", headerProxy.headerParamConverterSeparatorSet(set_paramConverter));
      Assert.assertEquals("c1|c2|", headerProxy.headerParamConverterSeparatorSortedSet(sortedSet_paramConverter));
      
      Assert.assertEquals("c1|c2|", headerProxy.headerParamConverterDefaultList(list_paramConverter));
      Assert.assertEquals("c1|c2|", headerProxy.headerParamConverterDefaultSet(set_paramConverter));
      Assert.assertEquals("c1|c2|", headerProxy.headerParamConverterDefaultSortedSet(sortedSet_paramConverter));
   }
   
   /**
    * @tpTestDetails MatrixParam test
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testMatrix() {

      Assert.assertEquals("c1|c2|", matrixProxy.matrixConstructorSeparatorList(list_constructor));
      Assert.assertEquals("c1|c2|", matrixProxy.matrixConstructorSeparatorSet(set_constructor));
      Assert.assertEquals("c1|c2|", matrixProxy.matrixConstructorSeparatorSortedSet(sortedSet_constructor));
      
      Assert.assertEquals("c1|c2|", matrixProxy.matrixConstructorDefaultList(list_constructor));
      Assert.assertEquals("c1|c2|", matrixProxy.matrixConstructorDefaultSet(set_constructor));
      Assert.assertEquals("c1|c2|", matrixProxy.matrixConstructorDefaultSortedSet(sortedSet_constructor)); 
      
      Assert.assertEquals("c1|c2|", matrixProxy.matrixValueOfSeparatorList(list_valueOf));
      Assert.assertEquals("c1|c2|", matrixProxy.matrixValueOfSeparatorSet(set_valueOf));
      Assert.assertEquals("c1|c2|", matrixProxy.matrixValueOfSeparatorSortedSet(sortedSet_valueOf));
      
      Assert.assertEquals("c1|c2|", matrixProxy.matrixValueOfDefaultList(list_valueOf));
      Assert.assertEquals("c1|c2|", matrixProxy.matrixValueOfDefaultSet(set_valueOf));
      Assert.assertEquals("c1|c2|", matrixProxy.matrixValueOfDefaultSortedSet(sortedSet_valueOf)); 
      
      Assert.assertEquals("c1|c2|", matrixProxy.matrixFromStringSeparatorList(list_fromString));
      Assert.assertEquals("c1|c2|", matrixProxy.matrixFromStringSeparatorSet(set_fromString));
      Assert.assertEquals("c1|c2|", matrixProxy.matrixFromStringSeparatorSortedSet(sortedSet_fromString));
      
      Assert.assertEquals("c1|c2|", matrixProxy.matrixFromStringDefaultList(list_fromString));
      Assert.assertEquals("c1|c2|", matrixProxy.matrixFromStringDefaultSet(set_fromString));
      Assert.assertEquals("c1|c2|", matrixProxy.matrixFromStringDefaultSortedSet(sortedSet_fromString)); 
      
      Assert.assertEquals("c1|c2|", pathProxy.pathParamConverterSeparatorList(list_paramConverter));
      Assert.assertEquals("c1|c2|", pathProxy.pathParamConverterSeparatorSet(set_paramConverter));
      Assert.assertEquals("c1|c2|", pathProxy.pathParamConverterSeparatorSortedSet(sortedSet_paramConverter));
      
      Assert.assertEquals("c1|c2|", pathProxy.pathParamConverterDefaultList(list_paramConverter));
      Assert.assertEquals("c1|c2|", pathProxy.pathParamConverterDefaultSet(set_paramConverter));
      Assert.assertEquals("c1|c2|", pathProxy.pathParamConverterDefaultSortedSet(sortedSet_paramConverter));
   }
   
   /**
    * @tpTestDetails PathParam test
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testPath() {

      Assert.assertEquals("c1|c2|", pathProxy.pathConstructorSeparatorList(list_constructor));
      Assert.assertEquals("c1|c2|", pathProxy.pathConstructorSeparatorSet(set_constructor));
      Assert.assertEquals("c1|c2|", pathProxy.pathConstructorSeparatorSortedSet(sortedSet_constructor));
      
      Assert.assertEquals("c1|c2|", pathProxy.pathConstructorDefaultList(list_constructor));
      Assert.assertEquals("c1|c2|", pathProxy.pathConstructorDefaultSet(set_constructor));
      Assert.assertEquals("c1|c2|", pathProxy.pathConstructorDefaultSortedSet(sortedSet_constructor)); 
      
      Assert.assertEquals("c1|c2|", pathProxy.pathValueOfSeparatorList(list_valueOf));
      Assert.assertEquals("c1|c2|", pathProxy.pathValueOfSeparatorSet(set_valueOf));
      Assert.assertEquals("c1|c2|", pathProxy.pathValueOfSeparatorSortedSet(sortedSet_valueOf));
      
      Assert.assertEquals("c1|c2|", pathProxy.pathValueOfDefaultList(list_valueOf));
      Assert.assertEquals("c1|c2|", pathProxy.pathValueOfDefaultSet(set_valueOf));
      Assert.assertEquals("c1|c2|", pathProxy.pathValueOfDefaultSortedSet(sortedSet_valueOf)); 
      
      Assert.assertEquals("c1|c2|", pathProxy.pathFromStringSeparatorList(list_fromString));
      Assert.assertEquals("c1|c2|", pathProxy.pathFromStringSeparatorSet(set_fromString));
      Assert.assertEquals("c1|c2|", pathProxy.pathFromStringSeparatorSortedSet(sortedSet_fromString));
      
      Assert.assertEquals("c1|c2|", pathProxy.pathFromStringDefaultList(list_fromString));
      Assert.assertEquals("c1|c2|", pathProxy.pathFromStringDefaultSet(set_fromString));
      Assert.assertEquals("c1|c2|", pathProxy.pathFromStringDefaultSortedSet(sortedSet_fromString)); 
      
      Assert.assertEquals("c1|c2|", pathProxy.pathParamConverterSeparatorList(list_paramConverter));
      Assert.assertEquals("c1|c2|", pathProxy.pathParamConverterSeparatorSet(set_paramConverter));
      Assert.assertEquals("c1|c2|", pathProxy.pathParamConverterSeparatorSortedSet(sortedSet_paramConverter));
      
      Assert.assertEquals("c1|c2|", pathProxy.pathParamConverterDefaultList(list_paramConverter));
      Assert.assertEquals("c1|c2|", pathProxy.pathParamConverterDefaultSet(set_paramConverter));
      Assert.assertEquals("c1|c2|", pathProxy.pathParamConverterDefaultSortedSet(sortedSet_paramConverter));
   }
   
   /**
    * @tpTestDetails QueryParam test
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testQuery() {

      Assert.assertEquals("c1|c2|", queryProxy.queryConstructorSeparatorList(list_constructor));
      Assert.assertEquals("c1|c2|", queryProxy.queryConstructorSeparatorSet(set_constructor));
      Assert.assertEquals("c1|c2|", queryProxy.queryConstructorSeparatorSortedSet(sortedSet_constructor));
      
      Assert.assertEquals("c1|c2|", queryProxy.queryConstructorDefaultList(list_constructor));
      Assert.assertEquals("c1|c2|", queryProxy.queryConstructorDefaultSet(set_constructor));
      Assert.assertEquals("c1|c2|", queryProxy.queryConstructorDefaultSortedSet(sortedSet_constructor)); 
      
      Assert.assertEquals("c1|c2|", queryProxy.queryValueOfSeparatorList(list_valueOf));
      Assert.assertEquals("c1|c2|", queryProxy.queryValueOfSeparatorSet(set_valueOf));
      Assert.assertEquals("c1|c2|", queryProxy.queryValueOfSeparatorSortedSet(sortedSet_valueOf));
      
      Assert.assertEquals("c1|c2|", queryProxy.queryValueOfDefaultList(list_valueOf));
      Assert.assertEquals("c1|c2|", queryProxy.queryValueOfDefaultSet(set_valueOf));
      Assert.assertEquals("c1|c2|", queryProxy.queryValueOfDefaultSortedSet(sortedSet_valueOf)); 
      
      Assert.assertEquals("c1|c2|", queryProxy.queryFromStringSeparatorList(list_fromString));
      Assert.assertEquals("c1|c2|", queryProxy.queryFromStringSeparatorSet(set_fromString));
      Assert.assertEquals("c1|c2|", queryProxy.queryFromStringSeparatorSortedSet(sortedSet_fromString));
      
      Assert.assertEquals("c1|c2|", queryProxy.queryFromStringDefaultList(list_fromString));
      Assert.assertEquals("c1|c2|", queryProxy.queryFromStringDefaultSet(set_fromString));
      Assert.assertEquals("c1|c2|", queryProxy.queryFromStringDefaultSortedSet(sortedSet_fromString)); 
      
      Assert.assertEquals("c1|c2|", queryProxy.queryParamConverterSeparatorList(list_paramConverter));
      Assert.assertEquals("c1|c2|", queryProxy.queryParamConverterSeparatorSet(set_paramConverter));
      Assert.assertEquals("c1|c2|", queryProxy.queryParamConverterSeparatorSortedSet(sortedSet_paramConverter));
      
      Assert.assertEquals("c1|c2|", queryProxy.queryParamConverterDefaultList(list_paramConverter));
      Assert.assertEquals("c1|c2|", queryProxy.queryParamConverterDefaultSet(set_paramConverter));
      Assert.assertEquals("c1|c2|", queryProxy.queryParamConverterDefaultSortedSet(sortedSet_paramConverter));
   }
   
   /**
    * @tpTestDetails 
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testMiscellaneous() {
      // none
   }
}
