/*
 * InterceptorPosition.java
 *
 * Created on December 11, 2006, 12:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gras.presley.interceptor;

import java.util.Comparator;

/**
 * Defines the position within the chain of interceptors.
 */
public enum InterceptorPosition {
    /**
     * The interceptor that really executes the code.
     * If possible try to avoid using it on business level.
     * This is the most deepest one.<br>
     */
    IMPLEMENTATION,

    /**
     * Closest to the implementation
     */
    CLOSEST,

    /**
     * Close to the implementation
     */
    CLOSE,

    /**
     *  Default
     */
    DEFAULT,

    /**
     * further away from the default interceptors
     */
    AROUND,

    /**
     * far way from the implementation
     */
    AWAY,

    /**
     * far away from the implementation
     */
    FARAWAY,

    /**
     * The starting point of interceptors. This is the first interceptor that is invoked.
     * If possible try to avoid using it on business level.
     * It is not required to have an interceptor with this position.
     */
    START;

    public static final Comparator<InterceptorInfo> interceptorInfoComparator = new Comparator<InterceptorInfo>() {
        @Override
        public int compare(InterceptorInfo i1, InterceptorInfo i2) {
            return Integer.compare(i1.getPosition().ordinal(), i2.getPosition().ordinal());
        }
    };
}
