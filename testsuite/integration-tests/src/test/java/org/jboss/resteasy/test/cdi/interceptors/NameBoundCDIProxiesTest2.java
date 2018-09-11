package org.jboss.resteasy.test.cdi.interceptors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Interceptors test.
 * @tpSince RESTEasy 4.0.0
 */
public class NameBoundCDIProxiesTest2 {

   private String generateURL(String path) {
      return "http://localhost:8080/NameMatching" + path;
   }

   /**
    * @tpTestDetails One item is stored and load to collection in resources.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   @Ignore
   public void testNameBoundInterceptor() throws Exception {
      Client client = ClientBuilder.newClient();
      String answer = client.target(generateURL("/test")).request().get(String.class);
      Assert.assertEquals("in-test-out", answer);
      client.close();
   }
}
