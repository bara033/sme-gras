/*
 * BeanProperty
 * Create Date: 2020. 03. 21.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.metadata;

import lombok.NonNull;

import java.lang.annotation.*;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Documented
@Inherited
public @interface BeanProp {

    /**
     * Zero based Unique index of the property within a bean.
     * Allows defining a property order within the type.
     * The framework allows to not set indexes on bean properties and assigns method-order index to the them,
     * but once a property index is set, there must not be any gap between the indexes.
     * Non-indexed properties will be listed after the indexed ones in method-order.
     */
    int index() default -1;

    /**
     * Some user friendly name for the bean property.
     * The default ("") uses the reflective Java property name as the label. I.e. the Java beans property name
     * of the annotated getter or field.
     */
    @NonNull String label() default "";

    /**
     * Property default value String representation.
     */
    @NonNull String defValue() default "";

    /**
     * If true, then the property is generally visible for the user through the GUI.
     * This is only a first static visible attribute. Further evaluation may still define this property to be
     * none-visible.<br>
     * If this is set to false, then the user will never be able to see the property.
     */
    boolean visible() default true;

    /**
     * Defines if the property is generally editable by the user through the GUI.
     * This is only a first static editable attribute. Further evaluation may still define this property to be
     * none-editable.<br>
     * NOTE: this attribute applies to the property also in case of collection types, and not to the content
     * of the collection. So it will be set to false in most collection type cases to avoid that the
     * whole collection can be replaced.
     * <br>
     * BUT: if this is set to false, then the user will never be able to edit the property.
     */
    boolean editable() default true;

    /**
     * If true, then the property can not be null in the application.
     * For primitive type values, this attribute will be always (automatic) set to true. So it is not necessary
     * to set this to true for primitive types.<br>
     * <p> NOTE: For persistent properties this can be {@code true} if dbMandatory is {@code false}
     * and be {@code false} if dbMandatory is {@code true}.
     */
    boolean mandatory() default false;
}
