package org.jboss.resteasy.test.nextgen;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProxyTest extends BaseResourceTest
{
   public static class Attachment {

      @HeaderParam("X-Atlassian-Token")
      @PartType("text/plain")
      private String multipartHeader = "nocheck";

      @FormParam("file")
      @PartType("text/plain")
      private byte[] data;

      public String getMultipartHeader() {
         return multipartHeader;
      }

      public void setMultipartHeader(String multipartHeader) {
         this.multipartHeader = multipartHeader;
      }

      public byte[] getData() {
         return data;
      }

      public void setData(byte[] data) {
         this.data = data;
      }

   }

   @Path("Api")
   public interface ApiService {

      @Path("test/{key}")
      @Consumes("multipart/form-data")
      @POST
      public void postAttachment(@MultipartForm Attachment attachment, @PathParam("key") String key);
   }

   public static class Resource implements ApiService {

      @Override
      public void postAttachment(Attachment attachment, String key)
      {

      }
   }

   @BeforeClass
   public static void reg()
   {
      addPerRequestResource(Resource.class);
   }


   private static final String TEST_URI = generateURL("");

   /**
    * ... here it doesn't work
    */
   @Test
   public void testNewBuilder() {
      ApiService apiService = new ResteasyClientBuilder().build().target(TEST_URI).proxy(ApiService.class);
      tryCall(apiService);
   }

   private void tryCall(ApiService apiService) {
      Attachment attachment = new Attachment();
      attachment.setData("foo".getBytes());

      apiService.postAttachment(attachment, "some-key");
   }


}
