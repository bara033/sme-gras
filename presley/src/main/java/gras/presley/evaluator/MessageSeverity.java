/*
 * MessageSeverity
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.evaluator;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public enum MessageSeverity {
    FATAL,
    INVALID,
    ERROR,
    INTEGRITY,
    WARNING,
    TODO,
    INFO;

    boolean canSave() {
        return ordinal() > INTEGRITY.ordinal();
    }
}
