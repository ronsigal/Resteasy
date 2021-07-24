package org.jboss.resteasy.plugins.protobuf;

import com.google.protobuf.Message;

public interface TranslateToJavabuf {

   Message assignToJavabuf(Object obj);
}
