package org.jboss.resteasy.plugins.protobuf;

import com.google.protobuf.DynamicMessage;

public interface AssignToJavabuf {
   public void assign(Object from, DynamicMessage.Builder builder);
}
