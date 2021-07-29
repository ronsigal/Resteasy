package org.jboss.resteasy.plugins.protobuf;

import com.google.protobuf.Message;

public interface TranslateFromJavabuf {

   Object assignFromJavabuf(Message message);
   void assignExistingFromJavabuf(Message message, Object obj);
}
