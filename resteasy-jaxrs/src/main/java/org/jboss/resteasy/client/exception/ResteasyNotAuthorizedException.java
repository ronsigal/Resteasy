package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import java.util.Collections;
import java.util.List;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;

/**
 * Wraps a {@link NotAuthorizedException} with a {@linkplain #sanitize(Response) sanitized} response
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyNotAuthorizedException extends NotAuthorizedException implements WebApplicationExceptionWrapper<NotAuthorizedException> {
    private final NotAuthorizedException wrapped;

    ResteasyNotAuthorizedException(final NotAuthorizedException wrapped) {
        super(wrapped.getMessage(), sanitize(wrapped.getResponse()), wrapped.getCause());
        this.wrapped = wrapped;
    }

    @Override
    public List<Object> getChallenges() {
        return Collections.emptyList();
    }

    @Override
    public NotAuthorizedException unwrap() {
        return wrapped;
    }
}
