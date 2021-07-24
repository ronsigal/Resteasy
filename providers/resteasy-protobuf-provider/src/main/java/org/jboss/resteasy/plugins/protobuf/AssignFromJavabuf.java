package org.jboss.resteasy.plugins.protobuf;

import com.google.protobuf.Message;

public interface AssignFromJavabuf {
   public void assign(Message message, Object object);
}
