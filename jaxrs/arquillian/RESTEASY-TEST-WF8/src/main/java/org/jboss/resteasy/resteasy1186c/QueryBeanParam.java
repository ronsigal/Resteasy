package org.jboss.resteasy.resteasy1186c;

import javax.validation.constraints.Size;

public interface QueryBeanParam {

   @Size(min = 2)
    String getParam();
    
}
