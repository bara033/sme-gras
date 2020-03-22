/*
 * TypeOperationInfo
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
public class TypeOperationInfo<_B> {

    final Class<TypeOperation<_B>> operation;
    final BeanType<_B> type;

    TypeOperationInfo(Class<TypeOperation<_B>> operation) {
        this.operation = operation;
        this.type = null; // here comes the reflective inspection of the given operation instance
    }

    public boolean supports(BeanType<?> metadata) {
        return metadata.isAssignableFrom(type);
    }

    TypeOperation<_B> createInstance() {
        return null; // reflective createNew
    }
}
