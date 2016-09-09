package org.jboss.resteasy.test.validation.cdi;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.validation.cdi.resource.ApplicationScopeContraintViolationExceptionMapper;
import org.jboss.resteasy.test.validation.cdi.resource.ApplicationScopeIRestServiceAppScoped;
import org.jboss.resteasy.test.validation.cdi.resource.ApplicationScopeIRestServiceReqScoped;
import org.jboss.resteasy.test.validation.cdi.resource.ApplicationScopeMyDto;
import org.jboss.resteasy.test.validation.cdi.resource.ApplicationScopeRestServiceAppScoped;
import org.jboss.resteasy.test.validation.cdi.resource.ApplicationScopeRestServiceReqScoped;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Validation
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1459
 * @tpSince RESTEasy 3.1.0.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ApplicationScopeValidationTest {
   
   @Deployment
   public static Archive<?> createTestArchive() {
       WebArchive war = TestUtil.prepareArchive(ApplicationScopeValidationTest.class.getSimpleName())
               .addClasses(ApplicationScopeIRestServiceAppScoped.class, ApplicationScopeIRestServiceReqScoped.class)
               .addClasses(ApplicationScopeContraintViolationExceptionMapper.class, ApplicationScopeMyDto.class)
               .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
       return TestUtil.finishContainerPrepare(war, null, ApplicationScopeRestServiceAppScoped.class, ApplicationScopeRestServiceReqScoped.class);
   }

   private String generateURL(String path) {
       return PortProviderUtil.generateURL(path, ApplicationScopeValidationTest.class.getSimpleName());
   }
   
   @Test
   public void testValidationApplicationScope()
   {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(generateURL("/testapp/send"));
      ApplicationScopeMyDto dto = new ApplicationScopeMyDto();
      dto.setPath("path");
      dto.setTest("test");
      Response response = target.request().post(Entity.entity(dto, MediaType.APPLICATION_JSON));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertNotNull(response.getHeaderString("entered"));
      response.close();
      
      response = target.request().post(Entity.entity(null, MediaType.APPLICATION_JSON));
      Assert.assertEquals(400, response.getStatus());
      Assert.assertNull(response.getHeaderString("entered"));
      response.close();
   }
   
   @Test
   public void testValidationRequestScope()
   {
      System.out.println("");
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(generateURL("/testreq/send"));
      ApplicationScopeMyDto dto = new ApplicationScopeMyDto();
      dto.setPath("path");
      dto.setTest("test");
      Response response = target.request().post(Entity.entity(dto, MediaType.APPLICATION_JSON));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertNotNull(response.getHeaderString("entered"));
      response.close();
      
      response = target.request().post(Entity.entity(null, MediaType.APPLICATION_JSON));
      Assert.assertEquals(400, response.getStatus());
      Assert.assertNull(response.getHeaderString("entered"));
      response.close();
   }
}
