package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget;

import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.AbstractCollectionProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.WebTargetProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractWebTargetCollectionProcessor extends AbstractCollectionProcessor<WebTarget> implements WebTargetProcessor
{
   public AbstractWebTargetCollectionProcessor(String paramName)
   {
      super(paramName);
   }
   
   public AbstractWebTargetCollectionProcessor(String paramName, Type type, Annotation[] annotations)
   {
      super(paramName, type, annotations);
   }

   @Override
   public WebTarget build(WebTarget target, Object param)
   {
      return buildIt(target, param);
   }
}
