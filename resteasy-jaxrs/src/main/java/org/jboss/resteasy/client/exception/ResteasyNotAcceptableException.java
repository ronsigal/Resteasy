package org.jboss.resteasy.client.exception;

import javax.ws.rs.core.Response;

/**
 * A runtime exception indicating a {@link javax.ws.rs.core.Response.Status#NOT_ACCEPTABLE
 * bad client request}.
 *
 * @author Sergey Beryozkin
 * @author Marek Potociar
 * @since 2.0
 */
public class ResteasyNotAcceptableException extends ResteasyClientErrorException {

   private static final long serialVersionUID = -529468413656617446L;

   /**
     * Construct a new bad client request exception.
     */
    public ResteasyNotAcceptableException() {
        super(Response.Status.NOT_ACCEPTABLE);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     */
    public ResteasyNotAcceptableException(final String message) {
        super(message, Response.Status.NOT_ACCEPTABLE);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 400}.
     */
    public ResteasyNotAcceptableException(final Response response) {
        super(validate(response, Response.Status.NOT_ACCEPTABLE));
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 400}.
     */
    public ResteasyNotAcceptableException(final String message, final Response response) {
        super(message, validate(response, Response.Status.NOT_ACCEPTABLE));
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param cause the underlying cause of the exception.
     */
    public ResteasyNotAcceptableException(final Throwable cause) {
        super(Response.Status.NOT_ACCEPTABLE, cause);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the underlying cause of the exception.
     */
    public ResteasyNotAcceptableException(final String message, final Throwable cause) {
        super(message, Response.Status.NOT_ACCEPTABLE, cause);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 400}.
     */
    public ResteasyNotAcceptableException(final Response response, final Throwable cause) {
        super(validate(response, Response.Status.NOT_ACCEPTABLE), cause);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 400}.
     */
    public ResteasyNotAcceptableException(final String message, final Response response, final Throwable cause) {
        super(message, validate(response, Response.Status.NOT_ACCEPTABLE), cause);
    }
}
