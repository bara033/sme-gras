/*
 * OperationRegistry
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.operation;

import gras.presley.ctx.ApplicationContext;

import java.util.List;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public interface OperationRegistry {

    <_B> List<TypeOperationInfo<_B>> getTypeOperations();

    <_B> List<BeanOperationInfo<_B>> getBeanOperations();

    <_B, _P> List<PropertyOperationInfo<_B, _P>> getPropertyOperations();

    static OperationRegistry instance() {
        return ApplicationContext.getOperationRegistry();
    }
}
