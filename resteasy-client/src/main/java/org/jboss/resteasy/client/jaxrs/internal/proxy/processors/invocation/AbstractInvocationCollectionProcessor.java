package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.AbstractCollectionProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.InvocationProcessor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractInvocationCollectionProcessor extends AbstractCollectionProcessor<ClientInvocation> implements InvocationProcessor
{
   public AbstractInvocationCollectionProcessor(String paramName)
   {
      super(paramName);
   }
   
   public AbstractInvocationCollectionProcessor(String paramName, Type type, Annotation[] annotations)
   {
      super(paramName, type, annotations);
   }

   @Override
   public void process(ClientInvocation invocation, Object param)
   {
      buildIt(invocation, param);
   }
}
