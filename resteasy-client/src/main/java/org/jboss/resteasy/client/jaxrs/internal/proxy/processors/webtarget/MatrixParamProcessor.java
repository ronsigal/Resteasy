package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatrixParamProcessor extends AbstractWebTargetCollectionProcessor
{

   public MatrixParamProcessor(String paramName)
   {
      super(paramName);
   }
   
   public MatrixParamProcessor(String paramName, Type type, Annotation[] annotations)
   {
      super(paramName, type, annotations);
   }

   @Override
   protected WebTarget apply(WebTarget target, Object object)
   {
      return target.matrixParam(paramName, object);
   }

}