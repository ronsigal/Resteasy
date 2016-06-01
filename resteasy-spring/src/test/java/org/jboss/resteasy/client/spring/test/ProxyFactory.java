package org.jboss.resteasy.client.spring.test;

import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 * Replaces deprecated org.jboss.resteasy.client.ProxyFactory in 
 * org.jboss.resteasy.springmvc.test.client.BasicSpringTest.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date June 24, 2016
 */
public class ProxyFactory
{
   public static <T> T create(Class<T> clazz, String base)
   {
      ResteasyWebTarget target = (ResteasyWebTarget) ClientBuilder.newClient().target(base);
      return target.proxy(clazz);
   }
}
