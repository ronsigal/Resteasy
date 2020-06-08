package org.jboss.resteasy.experiment;

import java.lang.reflect.Method;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.x.y
 * @tpTestCaseDetails Regression test for RESTEASY-zzz
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProtobufWFTest {

   private Client client;

   @Deployment
   public static Archive<?> createTestArchive() {
      WebArchive war = TestUtil.prepareArchive(ProtobufWFTest.class.getSimpleName());
      war.addClass(Person.class);
      war.addClass(BigPerson.class);
      return TestUtil.finishContainerPrepare(war, null, ProtobufWFResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ProtobufWFTest.class.getSimpleName());
   }

   @Before
   public void setup() {
      client = ClientBuilder.newClient();
   }

   @After
   public void cleanup() {
      client.close();
   }

   @Test
   public void testProto() {
      doTest("protobuf");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doTest("protobuf");
      }
      System.out.println("protobuf (small): " + (System.currentTimeMillis() - start));
      Assert.fail("ok"); // Get details
   }

   int count = 1000;

   @Test
   public void testJSON() {
      doTest("json");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doTest("json");
      }
      System.out.println("json (small): " + (System.currentTimeMillis() - start));
      Assert.fail("ok");
   }

   private void doTest(String transport)
   {
      Builder request = client.target(generateURL("/" + transport)).request();
      Person ron = new Person(1, "ron", "ron@jboss.org");
      Response response = request.post(Entity.entity(ron, "application/" + transport));
      //    System.out.println("status: " + response.getStatus());
      Person person = response.readEntity(Person.class);
      Person tanicka = new Person(3, "tanicka", "a@b");
      Assert.assertEquals(tanicka, person);
   }

   @Test
   public void testBigProto() throws Exception {
      doBigTest("protobuf");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doBigTest("protobuf");
      }
      System.out.println("protobuf (big): " + (System.currentTimeMillis() - start));
      Assert.fail("ok");
   }

   @Test
   public void testBigJSON() throws Exception {
      doBigTest("json");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doBigTest("json");
      }
      System.out.println("json (big): " + (System.currentTimeMillis() - start));
      Assert.fail("ok");
   }

   private void doBigTest(String transport) throws Exception
   {
      Builder request = client.target(generateURL("/big/" + transport)).request();
      BigPerson ron = getBigPerson(1, "ron", "ron@jboss");
      Response response = request.post(Entity.entity(ron, "application/" + transport));
      BigPerson person = response.readEntity(BigPerson.class);
      BigPerson tanicka = getBigPerson(3, "tanicka", "a@b");
      Assert.assertEquals(tanicka.getName(), person.getName());
   }

   private static String abc = "abcdefghijklmnopqrstuvwxyz";

   public static BigPerson getBigPerson(int id, String name, String email) throws Exception
   {  
      BigPerson bp = new BigPerson();
      bp.setId(id);
      bp.setName(name);
      bp.setEmail(email);
      for (int i = 0; i < 25; i++)
      {
         Method m = BigPerson.class.getMethod("setS" + i, String.class);
         m.invoke(bp, abc.substring(i) + abc.substring(0, i));
      }
      return bp;
   }
}
