package org.jboss.resteasy.test.xxe;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Unit tests for RESTEASY-647.
 * 
 * Idea for test comes from Tim McCune: 
 * http://jersey.576304.n2.nabble.com/Jersey-vulnerable-to-XXE-attack-td3214584.html
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Jan 6, 2012
 */
public class TestXXE_Jetty extends AbstractTestXXE
{
   protected static String eol = System.getProperty("line.separator");
   
   protected Server server;
   
   protected String webXmlStart =
         "<!DOCTYPE web-app PUBLIC" + eol +
         "   \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\"" + eol +
         "   \"http://java.sun.com/dtd/web-app_2_3.dtd\" >" + eol +
         "<web-app>" + eol +
         "   <display-name>XXE Test</display-name>" + eol;
   
   protected String contextParamTrue = 
         "   <context-param>" + eol +
         "      <param-name>resteasy.document.expand.entity.references</param-name>" + eol +
         "      <param-value>true</param-value>" + eol +
         "   </context-param>" +  eol;
   
   protected String contextParamFalse = 
         "   <context-param>" + eol +
         "      <param-name>resteasy.document.expand.entity.references</param-name>" + eol +
         "      <param-value>false</param-value>" + eol +
         "   </context-param>" + eol;
   
   protected String webXmlEnd =
         "   <servlet>" + eol +
         "      <servlet-name>Resteasy</servlet-name>" + eol +
         "      <servlet-class>" + eol +
         "         org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher" + eol +
         "      </servlet-class>" + eol +
         "      <init-param>" + eol +
         "         <param-name>javax.ws.rs.Application</param-name>" + eol +
         "         <param-value>org.jboss.resteasy.test.xxe.MovieApplication</param-value>" + eol +
         "      </init-param>" + eol +
         "   </servlet>" + eol +
         "   <servlet-mapping>" + eol +
         "      <servlet-name>Resteasy</servlet-name>" + eol +
         "      <url-pattern>/*</url-pattern>" + eol +
         "   </servlet-mapping>" + eol +
         "</web-app>" + eol;
   
   protected enum Behavior {NO_INSTRUCTION, EXPAND, NO_EXPAND};
   
   public void before(String expandEntityReferences) throws Exception
   {
      if (Boolean.parseBoolean(expandEntityReferences))
      {
         writeWebXml(Behavior.EXPAND);
      }
      else
      {
         writeWebXml(Behavior.NO_EXPAND);
      }
      startServer();
   }
   
   public void before() throws Exception
   {
      writeWebXml(Behavior.NO_INSTRUCTION);
      startServer();
   }
   
   public void after() throws Exception
   {
      server.stop();
   }
   
   protected void writeWebXml(Behavior behavior) throws IOException
   {
      FileOutputStream fos = new FileOutputStream("src/test/resources/WEB-INF/web.xml");
      DataOutputStream dos = new DataOutputStream(fos);
      dos.write(webXmlStart.getBytes());
      switch (behavior)
      {
         case NO_INSTRUCTION:
            break;
      
         case EXPAND:
            dos.write(contextParamTrue.getBytes());
            break;
            
         case NO_EXPAND:
            dos.write(contextParamFalse.getBytes());
            break;
      }
      dos.write(webXmlEnd.getBytes());
      dos.close();
   }
   
   protected void startServer() throws Exception
   {
      server = new Server(8081);
      WebAppContext context = new WebAppContext();
      context.setDescriptor("WEB-INF/web.xml");
      context.setResourceBase("src/test/resources");
      context.setContextPath("/");
      context.setParentLoaderPriority(true);
      server.setHandler(context);
      server.start();  
   }
}
