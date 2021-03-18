package org.jboss.resteasy.plugins.protobuf;

import java.io.IOException;

public class ProtobufToJavaCompiler
{
   public static int compile(String directory, String file)
   {
      Process proc = null;
      try
      { 
         // Command to create an external process 
//         String command = "/home/rsigal/bin/protoc -I=/tmp/tmp --java_out=/tmp/tmp/ " + file;
         String command = "/home/rsigal/bin/protoc -I=" + directory + "/target --java_out=" + directory + "/target/ " + file;
//         String command = "/home/rsigal/bin/protoc -I=/home/rsigal/tmp/git.master.proto/Resteasy/providers/resteasy-protobuf-provider/src/test/resources/ --java_out=/home/rsigal/tmp/git.master.proto/Resteasy/providers/resteasy-protobuf-provider/target/classes/ " + file;
//         System.out.println(command);
         // Running the above command 
         Runtime run  = Runtime.getRuntime(); 
         proc = run.exec(command);
         synchronized (proc)
         {
            proc.wait(2000);
         }
//         System.out.println("status: " + proc.exitValue());
         return proc.exitValue();
      } 
      catch (IOException | InterruptedException e) 
      { 
         e.printStackTrace();
         return proc.exitValue();
      }
   }
}
