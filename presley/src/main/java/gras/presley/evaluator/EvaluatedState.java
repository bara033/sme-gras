/*
 * State
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.evaluator;

import java.util.EnumSet;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public enum EvaluatedState {

    CONFIDENTIAL(true, false, EnumSet.noneOf(EvaluatedState.class)),
    READABLE(false, true, EnumSet.noneOf(EvaluatedState.class)),
    VISIBLE(false, true, EnumSet.of(CONFIDENTIAL, READABLE)),

    WRITEABLE(false, true, EnumSet.of(READABLE)),
    EDITABLE(false, true, EnumSet.of(CONFIDENTIAL, READABLE, WRITEABLE)),

    MANDATORY(true, false, EnumSet.noneOf(EvaluatedState.class)),

    REMOVABLE(false, true, EnumSet.noneOf(EvaluatedState.class)),
    CREATABLE(false, true, EnumSet.noneOf(EvaluatedState.class)),

    WARNINGS(false, false, EnumSet.noneOf(EvaluatedState.class)),
    ERRORS(false, false, EnumSet.noneOf(EvaluatedState.class));

    private final boolean initial;
    private final boolean dflt;
    private final EnumSet<EvaluatedState> dependsOn;

    EvaluatedState(boolean initial, boolean dflt, EnumSet<EvaluatedState> dependsOn) {
        this.initial = initial;
        this.dflt = dflt;
        this.dependsOn = dependsOn;
    }

    public boolean isInitial() {
        return initial;
    }

    public boolean getDefault() {
        return dflt;
    }

    public EnumSet<EvaluatedState> getDependsOn() {
        return dependsOn;
    }

    public boolean isMessageState() {
        return this == WARNINGS || this == ERRORS;
    }
}
