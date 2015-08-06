/**
 *
 */
package org.jboss.resteasy.resteasy1186;

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
public class AsyncTestResources {
	
	@Path("root")
	public interface AsyncRootResource {
		
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
	
	@RequestScoped
	public static class AsyncRootResourceImpl implements AsyncRootResource {

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
		public void getAll(@Suspended AsyncResponse asyncResponse, QueryBeanParamImpl beanParam) {
			System.out.println("beanParam#getParam valid? " + beanParam.getParam());
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
