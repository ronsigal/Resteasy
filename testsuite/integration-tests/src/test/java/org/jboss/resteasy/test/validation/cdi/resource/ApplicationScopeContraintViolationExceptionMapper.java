package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationScopeContraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    public Response toResponse(ConstraintViolationException e) {
        List<String> fields = e.getConstraintViolations().stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toList());
        return Response.status(400).entity(fields).build();
    }
}
