package org.jboss.resteasy.plugins.server.tjws;

import java.util.Hashtable;

import Acme.Serve.Serve;

/**
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * 
 * @version $Revision: 1.1 $
 * Created Feb 21, 2012
 */
public class ExtendedServe extends Serve
{
   private static final long serialVersionUID = -4632220353153579440L;
 
   private Hashtable<String,String> initParams;
   
   public String getInitParameter(String param)
   {
      if (initParams == null)
      {
         return null;
      }
      return initParams.get(param);
   }

   public Hashtable<String, String> getInitParams()
   {
      return initParams;
   }

   public void setInitParams(Hashtable<String, String> initParams)
   {
      this.initParams = initParams;
   }
}
