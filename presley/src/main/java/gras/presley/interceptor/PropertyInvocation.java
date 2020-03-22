/*
 * PropertyInvocation
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.interceptor;

import gras.presley.metadata.PlainBeanProp;

import java.util.List;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class PropertyInvocation<_B, _P> {

    private final _B bean;
    private final _P originalValue;
    private final _P newValue;
    private final PlainBeanProp<_B, _P> propertyMetadata;
    private final List<PropertyInterceptorInfo<_B, _P>> interceptors;
    private int idx = 0;

    public PropertyInvocation(_B bean, _P originalValue, _P newValue, PlainBeanProp<_B, _P> propertyMetadata, List<PropertyInterceptorInfo<_B, _P>> interceptors) {
        this.bean = bean;
        this.originalValue = originalValue;
        this.newValue = newValue;
        this.propertyMetadata = propertyMetadata;
        this.interceptors = interceptors;
    }

    public final _B getBean() {
        return bean;
    }

    public final _P getOriginalValue() {
        return originalValue;
    }

    public final _P getNewValue() {
        return newValue;
    }

    public final PlainBeanProp<_B, _P> getPropertyMetadata() {
        return propertyMetadata;
    }

    public void proceed() throws Exception {
        PropertyInterceptorInfo<_B, _P> nextInterceptor;
        for (;;) {
            if (idx >= interceptors.size())
                return;
            idx++;

            nextInterceptor = interceptors.get(idx);
            if (nextInterceptor.supports(bean))
                break;
        }

        nextInterceptor.getInstance().intercept(this);
    }
}
