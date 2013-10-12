package org.jboss.resteasy.test.nextgen.validation;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for RESTEASY-937
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date September 2, 2013
 * 
 * @version $Revision: 1 $
 */
public class TestValidationI18N
{
   private static Executor serverExecutor;
   private static CharArrayWriter writer;
   
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   public class Executor
   {
      private String command;
      private Writer writer;
      private boolean successful;
      private Process process;

      public Executor(String command, Writer writer)
      {
         this.command = command;
         this.writer = writer;
      }

      public boolean successful()
      {
         return successful;
      }

      public void start() throws Exception
      {
         executeCommand(command);
      }

      private void executeCommand(String command) throws Exception
      {
         final String finalCommand = command;

         new Thread()
         {
            public void run()
            {
               try
               {
//                  System.out.println("executing " + finalCommand);
                  final Process local = Runtime.getRuntime().exec(finalCommand);
//                  System.out.println("executed " + finalCommand);
                  process = local;

                  final BufferedReader errReader = new BufferedReader(new InputStreamReader(local.getErrorStream()));
                  final BufferedReader outReader = new BufferedReader(new InputStreamReader(local.getInputStream()));
                  final BufferedWriter outWriter = new BufferedWriter(writer);

                  new Thread()
                  {
                     public void run()
                     {
                        try
                        {
                           String errOut = null;
                           while((errOut = errReader.readLine()) != null)
                           {
//                              System.out.println("errorOut: " + errOut);
                              outWriter.append(errOut + "|");
                              outWriter.flush();
                           }
                        }
                        catch(IOException e)
                        {
                        }
                     }
                  }.start();

                  new Thread()
                  {
                     public void run()
                     {
                        try
                        {
                           String stdOut = null;
                           while((stdOut = outReader.readLine()) != null)
                           {
                              System.out.println("stdOut: " + stdOut);
                              outWriter.append(stdOut + "|");
                              outWriter.flush();
                           }
                        }
                        catch(IOException e)
                        {
                        }
                     }
                  }.start();

                  local.waitFor();
                  successful = (local.exitValue() == 0);
               }
               catch(Exception e)
               {
                  System.out.println("Error starting process: " + finalCommand + e.toString());
                  e.printStackTrace();
               }
            }
         }.start();
      }

      public void waitUntilReady()
      {
         ResteasyClient client = null;
         WebTarget target = null;
         Response response = null;
         
         while (true)
         {
            try
            {
               client = new ResteasyClientBuilder().build();
               target = client.target(generateURL("/ready"));
               System.out.println("Sending /ready");
               response = target.request().get();
               System.out.println("ready status: " + response.getStatus());
               if (response.getStatus() == 200)
               {
                  break;
               }
               System.out.println("Server not ready");
            }
            catch (Exception e)
            {
               System.out.println("Server not ready: " + e);
               try
               {
                  Thread.sleep(100);
               }
               catch (InterruptedException e1)
               {
                  // Blank
               }
            }
            finally
            {
               if (response != null)
               {
                  response.close();  
               }
               if (client != null && !client.isClosed())
               {
                  client.close();
               }
               System.out.println("Server ready");
            }
         }
      }

      public void destroy()
      {
         process.destroy();
         System.out.println("destroyed process");
      }
   }

   //////////////////////////////////////////////////////////////////////////////
   public void before(String language, String country, String variant) throws Exception
   {
      before(language, country, variant, null);
   }
   
   public void before(String language, String country, String variant, String script) throws Exception
   {
      System.out.println("org.jboss.byteman.dump.generated.classes: " + System.getProperty("org.jboss.byteman.dump.generated.classes"));
      String command = "java -cp \".:" +  System.getProperty("java.class.path") + "\" "
            + (script == null ? "" : "-javaagent:target/lib/byteman.jar" + "=script:src/test/resources/" + script + ",boot:target/lib/byteman.jar ")
//            + " -Dorg.jboss.byteman.dump.generated.classes=true "
            + "-Dorg.jboss.byteman.transform.all "
            + ResourceServerI18N.class.getName() + " "
            + language + " "
            + country + " "
            + variant;
//      System.out.println("command: " + command);
      writer = new CharArrayWriter(4096);
      serverExecutor = new Executor(command, writer);
      System.out.println("starting server");
      serverExecutor.start();
      System.out.println("waiting on server");
      serverExecutor.waitUntilReady();
      System.out.println("server is ready"); 
   }
   
   @After
   public void after() throws Exception
   {
      if (serverExecutor != null)
      {
         serverExecutor.destroy();
      }
   }

   @Test
   public void testNonExistentXX() throws Exception
   {
      doTest("XX", "", "", "INFO", "000004", "Unable to find ValidatorFactory that supports CDI. Using default ValidatorFactory.");
   }
   
   @Test
   public void testNonExistentXXYY() throws Exception
   {
      doTest("XX", "YY", "", "INFO", "000004", "Unable to find ValidatorFactory that supports CDI. Using default ValidatorFactory.");
   }

   @Test
   public void testNonExistentXXYYZZ() throws Exception
   {
      doTest("XX", "YY", "ZZ", "INFO", "000004", "Unable to find ValidatorFactory that supports CDI. Using default ValidatorFactory.");
   }
   
   @Test
   public void testEn() throws Exception
   {
      doTest("en", "", "", "INFO", "000004", "Unable to find ValidatorFactory that supports CDI. Using default ValidatorFactory.");
   }
   
   @Test
   public void testEnUs() throws Exception
   {
      doTest("en", "us", "", "INFO", "000004", "Unable to find ValidatorFactory that supports CDI. Using default ValidatorFactory.");
   }

   @Test
   public void testIt() throws Exception
   {
      doTest("it", "", "", "INFO", "000004", "Non si puo trovare ValidatorFactory che supporta CDI. Si usa default ValidatorFactory.");
   }
   
   @Test
   public void testItIt() throws Exception
   {
      doTest("it", "it", "", "INFO", "000004", "Non si puo trovare ValidatorFactory che supporta CDI. Si usa default ValidatorFactory.");
   }

   @Test
   public void testEs() throws Exception
   {
      doTest("es", "", "", "INFO", "000004", "Incapaz de encontrar ValidatorFactory que soporta CDI. Usando ValidatorFactory predeterminado.");
   }

   @Test
   public void testEsES() throws Exception
   {
      doTest("es", "es", "", "INFO", "000004", "Incapaz de encontrar ValidatorFactory que soporta CDI. Usando ValidatorFactory predeterminado.");
   }
 
   @Test
   public void testSupportsCDI_En() throws Exception
   {
      doTest("en", "", "", "INFO", "000003", "Using ValidatorFactory that supports CDI:", "validation.btm");
   }

   @Test
   public void testSupportsCDI_EnUS() throws Exception
   {
      doTest("en", "US", "", "INFO", "000003", "Using ValidatorFactory that supports CDI:", "validation.btm");
   }

   @Test
   public void testSupportsCDI_It() throws Exception
   {
      doTest("it", "", "", "INFO", "000003", "Usando ValidatorFactory che supporta CDI:", "validation.btm");
   }
   
   @Test
   public void testSupportsCDI_ItIt() throws Exception
   {
      doTest("it", "it", "", "INFO", "000003", "Usando ValidatorFactory che supporta CDI:", "validation.btm");
   }
   
   @Test
   public void testSupportsCDI_Es() throws Exception
   {
      doTest("es", "", "", "INFO", "000003", "Usando ValidatorFactory que soporta CDI:", "validation.btm");
   }

   @Test
   public void testSupportsCDI_EsEs() throws Exception
   {
      doTest("es", "es", "", "INFO", "000003", "Usando ValidatorFactory que soporta CDI:", "validation.btm");
   }
   
   protected void doTest(String language, String country, String variant, String level, String id, String message) throws Exception
   {
      doTest(language, country, variant, level, id, message, null);
   }
   
   protected void doTest(String language, String country, String variant, String level, String id, String message, String script) throws Exception
   {
      before(language, country, variant, script);
      System.out.println("\r===============================");
      System.out.println("testing " + language + (country == "" ? "" : "_" + country) + (variant == "" ? "_" : variant));
      ResteasyClient client = null;
      Response response = null;
      try
      {
         client = new ResteasyClientBuilder().build();
         WebTarget target = client.target(generateURL("/printlocale"));
         response = target.request().get();
         System.out.println("locale status: " + response.getStatus());
         Assert.assertEquals(204, response.getStatus());
         String output = new String(writer.toCharArray());
         printLocale(output);
         target = client.target(generateURL("/get"));
         response = target.request().get();
         System.out.println("get status: " + response.getStatus());
         output = new String(writer.toCharArray());
         Assert.assertTrue(testMessage("INFO", "RESTEASY" + id, message, output));
      }
      finally
      {
         response.close();
         client.close();
      }
   }
   
   protected static boolean testMessage(String level, String id, String message, String actual)
   {
      int pos1 = actual.indexOf("RESTEASY");
      if (pos1 < 0)
      {
         System.out.println("no RESTEASY in output");
         return false;
      }
      pos1 = pos1 - level.length() - 2;
      int pos2 = actual.indexOf(':', pos1 + 1);
      String actualLevel = actual.substring(pos1, pos2);
      int pos3 = actual.indexOf(':', pos2 + 1);
      String actualId = actual.substring(pos2 + 2, pos3);
      int pos4 = actual.indexOf("|", pos3);
      String actualMessage = actual.substring(pos3 + 2, pos4);
      System.out.println("prefix of actual message: " + actualLevel + ": " + actualId + ": " + actualMessage.substring(0, Math.min(actualMessage.length(), 64)));
      return level.equals(actualLevel) && id.equals(actualId) && actualMessage.startsWith(message);
   }
   
   protected static void printLocale(String output)
   {
      int start = output.indexOf(ResourceServerI18N.LOCALE_MARKER);
      int end = output.indexOf("|", start);
      System.out.println(output.substring(start, end));
   }
}
