package org.jboss.resteasy.experiment;

import javax.annotation.Priority;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(-1)
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

   @Override
   public Response toResponse(ConstraintViolationException exception) {
      System.out.println(this + ": exception: " + exception.getClass());
      System.out.println(this + ": message: " + exception.getMessage());
      return Response
            .status(Status.BAD_REQUEST)
            .type("text/plain")
            .entity(exception.getMessage())
            .build();
   }
}
