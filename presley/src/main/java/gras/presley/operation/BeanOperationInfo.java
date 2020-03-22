/*
 * BeanOperationInfo
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.operation;

import gras.presley.metadata.BeanType;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class BeanOperationInfo<_B> {

    final Class<BeanOperation<_B>> operation;
    final BeanType<_B> beanMetadata;

    BeanOperationInfo(Class<BeanOperation<_B>> operation) {
        this.operation = operation;
        this.beanMetadata = null; // here comes the reflective inspection of the given operation instance
    }

    public boolean supports(BeanType<?> metadata) {
        return metadata.isAssignableFrom(beanMetadata);
    }

    public boolean supports(_B bean) {
        return true;
    }

    BeanOperation<_B> createInstance(_B bean) {
        return null; // reflective createNew
    }
}
