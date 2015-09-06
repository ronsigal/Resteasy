package org.jboss.resteasy.resteasy1186c;



import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

/**
 *
 *
 */
public class AsyncTestResources2 {
   
   @Path("async")
   public interface AsyncRootResource extends AsyncValidResource {
      
      @GET
      @Override
      void getAll(@Suspended AsyncResponse asyncResponse, @BeanParam QueryBeanParamImpl beanParam);
      
      @Path("/sub")
      AsyncSubResource getSubResource();
      
   }
   
   public interface AsyncSubResource extends AsyncValidResource {

      @GET
      @Override
      void getAll(@Suspended AsyncResponse asyncResponse, @BeanParam QueryBeanParamImpl beanParam);
      
   }
   
   public interface AsyncValidResource {

      void getAll(AsyncResponse asyncResponse, @Valid QueryBeanParamImpl beanParam);

   }
   
   public abstract static class AbstractAsyncRootResource implements AsyncRootResource {
      
      @Override
      public void getAll(AsyncResponse asyncResponse, QueryBeanParamImpl beanParam) {
         System.out.println("abstract async#getAll: beanParam#getParam valid? " + beanParam.getParam());
         asyncResponse.resume(Response.ok().build());
      }
   }
   
   @RequestScoped
   public static class AsyncRootResourceImpl extends AbstractAsyncRootResource {

      @Inject
      private AsyncSubResource subResource;
      
      @Override
      public AsyncSubResource getSubResource() {
         return subResource;
      }

   }
   
   @RequestScoped
   public static class AsyncSubResourceImpl implements AsyncSubResource {

      @Override
      public void getAll(AsyncResponse asyncResponse, QueryBeanParamImpl beanParam) {
         System.out.println("sub#getAll: beanParam#getParam valid? " + beanParam.getParam());
         asyncResponse.resume(Response.ok().build());
      }
      
   }
   
   public interface QueryBeanParam {

      @Size(min = 2)
       String getParam();
       
   }
   
   public static class QueryBeanParamImpl implements QueryBeanParam {

      @QueryParam("foo")
       private String param;
      
      @Override
      public String getParam() {
         return param;
      }
   }

}