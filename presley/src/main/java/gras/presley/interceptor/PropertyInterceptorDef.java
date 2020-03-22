/*
 * JdoPropertyInterceptorDef.java
 *
 * Created on December 9, 2006, 2:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gras.presley.interceptor;

import java.lang.annotation.*;

/**
 * optional annotation for {@link PropertyInterceptor} instances which further limits the execution/invocation of the interceptor.
 * By default PropertyInterceptor instances can be executed for all bean properties.
 *
 * @author lie44
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PropertyInterceptorDef {
    
    /**
     * Optional list of property names that limits the PropertyInterceptor instance to only those properties that match one
     * of the given names.
     * The names may contain '*' and '?' wild cards, and may or may not contain the bean qualified class name part.
     * Whether to check the plain property name or the qualified class name plus the property name is indicated by having
     * at least one dot in the pattern. 
     * The names are always compared case sensitive.<br>
     * Examples:
     * <pre>
     * <code>*mod*</code>         - any property containing <i>mod</i>
     * <code>*Track*.*mod*</code> - any property containing <i>mod</i> 
     *                              and belongs to a JDO class with: 
     *                              <i>Track</i> in the name
     * </pre>
     * Defaults to any name.
     */
    String[] names() default {};
}
