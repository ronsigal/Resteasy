package org.jboss.resteasy.test.validation.cdi.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ApplicationScopeRestServiceAppScoped implements ApplicationScopeIRestServiceAppScoped {

    public Response sendDto(ApplicationScopeMyDto myDto) {
        System.out.println("RestServiceAppScoped: Nevertheless: " + myDto);
        new Exception("RestServiceAppScoped").printStackTrace();
        return Response.ok(myDto == null ? "null" : myDto.getPath()).header("entered", "true").build();
    }
}
