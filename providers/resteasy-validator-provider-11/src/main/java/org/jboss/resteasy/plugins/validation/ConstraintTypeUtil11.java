package org.jboss.resteasy.plugins.validation;

import java.util.Iterator;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path.Node;

import org.jboss.resteasy.api.validation.ConstraintType;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.validation.ConstraintTypeUtil;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 6, 2012
 */
public class ConstraintTypeUtil11 implements ConstraintTypeUtil
{
   public ConstraintType.Type getConstraintType(Object o)
   {
      if (!(o instanceof ConstraintViolation))
      {
         throw new RuntimeException(Messages.MESSAGES.unknownObjectPassedAsConstraintViolation(o));
      }
      ConstraintViolation<?> v = ConstraintViolation.class.cast(o);

      Iterator<Node> nodes = v.getPropertyPath().iterator();
      Node firstNode = nodes.next();
      if (firstNode.getKind() == ElementKind.METHOD)
      {
         Node secondNode = nodes.next();

         if (secondNode.getKind() == ElementKind.PARAMETER || secondNode.getKind() == ElementKind.CROSS_PARAMETER)
         {
            return ConstraintType.Type.PARAMETER;
         }
         else if (secondNode.getKind() == ElementKind.RETURN_VALUE)
         {
            return ConstraintType.Type.RETURN_VALUE;
         }
         else
         {
            throw new RuntimeException(Messages.MESSAGES.unexpectedPathNodeViolation(secondNode.getKind()));
         }
      }

      if (firstNode.getKind() == ElementKind.BEAN)
      {
         return ConstraintType.Type.CLASS;
      }

      if (firstNode.getKind() == ElementKind.PROPERTY)
      {
         return ConstraintType.Type.PROPERTY;
      }

      throw new RuntimeException(Messages.MESSAGES.unexpectedPathNode(firstNode.getKind()));
   }
}
