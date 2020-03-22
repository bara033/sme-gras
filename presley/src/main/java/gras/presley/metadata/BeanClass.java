/*
 * BeanClass
 * Create Date: 2020. 03. 20.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.metadata;

import lombok.NonNull;

import java.lang.annotation.*;

/**
 * Optional annotation for bean types.
 *
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
public @interface BeanClass {

    /**
     * Some user friendly name for the bean.
     */
    @NonNull String label();

    /**
     * If true, then the entity is generally visible for the user through the GUI.
     * This is only a first static visible attribute. Further evaluation may still define this entity to be
     * none-visible.<br>
     * If this is set to false, then the user will never be able to see the entity instances.<br>
     * NOTE: non-visible entities are not editable.
     */
    boolean visible() default true;

    /**
     * Defines if the entity is generally editable by the user through the GUI.
     * This is only a first static editable attribute. Further evaluation may still define this entity to be
     * none-editable.<br>
     * If this is set to false, then the user will never be able to edit the entity instances.<br>
     * NOTE: if the entity is evaluated non-visible based on {@link #visible()}, than user is not able to edit it.
     */
    boolean editable() default true;
}
