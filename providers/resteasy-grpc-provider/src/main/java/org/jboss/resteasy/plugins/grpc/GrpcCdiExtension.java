package org.jboss.resteasy.plugins.grpc;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class GrpcCdiExtension implements Extension {
   
   static private BeanManager beanManager;

   @SuppressWarnings("static-access")
   public void observeBeforeBeanDiscovery(@Observes BeforeBeanDiscovery event, BeanManager beanManager) {
      this.beanManager = beanManager;
   }
   
   static public BeanManager getBeanManager() {
      return beanManager;
   }
}
