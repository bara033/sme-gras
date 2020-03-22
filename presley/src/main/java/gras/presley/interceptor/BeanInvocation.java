/*
 * BeanInvocation
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.interceptor;

import gras.presley.metadata.BeanType;

import java.util.List;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class BeanInvocation<_B> {

    private final _B bean;
    private final BeanType<_B> beanMetadata;
    private final BeanOperation operation;
    private final List<BeanInterceptorInfo<_B>> interceptors;
    private int idx = 0;

    public BeanInvocation(_B bean, BeanType<_B> beanMetadata, BeanOperation operation, List<BeanInterceptorInfo<_B>> interceptors) {
        this.bean = bean;
        this.beanMetadata = beanMetadata;
        this.operation = operation;
        this.interceptors = interceptors;
    }

    public final _B getBean() {
        return bean;
    }

    public final BeanType<_B> getBeanMetadata() {
        return beanMetadata;
    }

    public final BeanOperation getOperation() {
        return operation;
    }

    public void proceed() throws Exception {
        BeanInterceptorInfo<_B> nextInterceptor;
        for (;;) {
            if (idx >= interceptors.size())
                return;
            idx++;

            nextInterceptor = interceptors.get(idx);
            if (nextInterceptor.supports(bean, operation))
                break;
        }

        nextInterceptor.getInterceptor().intercept(this);
    }
}
