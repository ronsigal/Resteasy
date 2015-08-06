package org.jboss.resteasy.resteasy1186;

import javax.validation.Valid;
import javax.ws.rs.container.AsyncResponse;

public interface AsyncValidResource {

   void getAll(AsyncResponse asyncResponse, @Valid QueryBeanParamImpl beanParam);

}
