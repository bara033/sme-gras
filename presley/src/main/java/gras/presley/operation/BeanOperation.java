/*
 * Beanoperation
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.operation;

/**
 * Base of operations that work on beans.
 *
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public interface BeanOperation<_B> {

    Object process(_B bean);
}
