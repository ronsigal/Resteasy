package org.jboss.resteasy.context;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.context.spi.ContextManagerProvider;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

@Provider
public class ContextFeature implements Feature
{
   @Override
   public boolean configure(FeatureContext context)
   {
      // this is tied to the deployment, which is what we want for the reactive context
      if(context.getConfiguration().getRuntimeType() == RuntimeType.CLIENT)
         return false;
      Dispatcher dispatcher = ResteasyProviderFactory.getContextData(Dispatcher.class);
      if(dispatcher == null) {
         // this can happen, but it means we're not able to find a deployment
         return false;
      }
      // Make sure we have context propagation for this class loader
      ContextManagerProvider.instance().getContextManager();
      return true;
   }

}
