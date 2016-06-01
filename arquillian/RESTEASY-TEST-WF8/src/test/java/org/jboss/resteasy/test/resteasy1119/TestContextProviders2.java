package org.jboss.resteasy.test.resteasy1119;

import java.lang.annotation.Annotation;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.resteasy1119.Customer;
import org.jboss.resteasy.resteasy1119.CustomerForm;
import org.jboss.resteasy.resteasy1119.Name;
import org.jboss.resteasy.resteasy1119.TestApplication;
import org.jboss.resteasy.resteasy1119.TestResource;
import org.jboss.resteasy.resteasy1119.Xop;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class TestContextProviders2 extends TestContextProviders
{ 
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1119.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(Customer.class, CustomerForm.class, Name.class, Xop.class)
            .addClass(TestContextProviders.class)
            .addAsWebInfResource("1119/web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }

   @Override
   <T> T get(String path, Class<T> clazz, Annotation[] annotations) throws Exception
   {
      Response response = ResteasyClientBuilder.newClient().target("http://localhost:8080/RESTEASY-1119" + path).request().get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      T entity = response.readEntity(clazz, annotations);
      return entity;
   }
   
   @Override
   <S, T> T post1(String path, S payload, MediaType mediaType, Class<T> returnType, Annotation[] annotations) throws Exception
   {
      Builder request = ResteasyClientBuilder.newClient().target("http://localhost:8080/RESTEASY-1119" + path).request();
      Response response = request.post(Entity.entity(payload, mediaType, annotations));
      return response.readEntity(returnType);
   }
   
   @Override
   <S, T> T post2(String path, S payload, MediaType mediaType, GenericType<T> genericReturnType, Annotation[] annotations) throws Exception
   {
      Builder request = ResteasyClientBuilder.newClient().target("http://localhost:8080/RESTEASY-1119" + path).request();
      Response response = request.post(Entity.entity(payload, mediaType, annotations));
      return response.readEntity(genericReturnType);
   }
}
