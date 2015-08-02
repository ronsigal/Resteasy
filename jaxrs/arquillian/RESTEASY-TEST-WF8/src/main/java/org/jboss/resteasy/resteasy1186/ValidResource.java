package org.jboss.resteasy.resteasy1186;

import javax.validation.Valid;
import javax.ws.rs.core.Response;

public interface ValidResource {

   Response getAll(@Valid QueryBeanParamImpl beanParam);

}
