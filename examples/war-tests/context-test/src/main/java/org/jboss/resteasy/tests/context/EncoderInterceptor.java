package org.jboss.resteasy.tests.context;

import org.junit.Assert;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Priority(0)
public class EncoderInterceptor implements WriterInterceptor
{
   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      Assert.assertTrue(context.getHeaders().containsKey("before-encoder"));
      Assert.assertFalse(context.getHeaders().containsKey("after-encoder"));
      Assert.assertFalse(context.getHeaders().containsKey("end"));
      context.getHeaders().add("encoder", "true");
      context.proceed();
   }
}