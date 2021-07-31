package org.jboss.resteasy.plugins.protobuf;

import java.io.IOException;

public class RunProtoc {

   public static void main(String[] args) {
      try {
         // Command to create an external process
         String command = "/usr/local/bin/protoc --java_out=target/generatedSources target/generatedSources/protobuf/idl/" + args[0] + ".proto";

         // Running the above command
         Runtime run  = Runtime.getRuntime();
         run.exec(command);
      } catch (IOException e) {
      }
   }
}
