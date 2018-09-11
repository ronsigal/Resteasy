package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget;

import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.WebTargetProcessor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathParamProcessor implements WebTargetProcessor
{
   private final String paramName;
   private final Boolean encodeSlashInPath;
   protected Type type;
   protected Annotation[] annotations;

   public PathParamProcessor(String paramName)
   {
      this.paramName = paramName;
      this.encodeSlashInPath = true;
   }

   public PathParamProcessor(String paramName, Boolean encodeSlashInPath)
   {
      this.paramName = paramName;
      this.encodeSlashInPath = encodeSlashInPath;
   }
   
   public PathParamProcessor(String paramName, Boolean encodeSlashInPath, Type genericType, Annotation[] annotations)
   {
      this.paramName = paramName;
      this.encodeSlashInPath = encodeSlashInPath;
      this.type = genericType;
      this.annotations = annotations;
   }
   
   @Override
   public WebTarget build(WebTarget target, Object param)
   {
      param = ResteasyProviderFactory.getInstance().toString(param, param.getClass(), type, annotations);
      return target.resolveTemplate(paramName, param, encodeSlashInPath);
   }
}
