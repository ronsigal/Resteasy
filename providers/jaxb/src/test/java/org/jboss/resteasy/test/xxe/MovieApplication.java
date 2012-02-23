package org.jboss.resteasy.test.xxe;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for RESTEASY-647.
 * 
 * Idea for test comes from Tim McCune: 
 * http://jersey.576304.n2.nabble.com/Jersey-vulnerable-to-XXE-attack-td3214584.html
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Jan 6, 2012
 */
public class MovieApplication extends Application {
   private Set<Object> singletons = new HashSet<Object>();
   private Set<Class<?>> empty = new HashSet<Class<?>>();

   public MovieApplication() {
      singletons.add(new MovieResource());
   }

   @Override
   public Set<Class<?>> getClasses() {
      return empty;
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}
