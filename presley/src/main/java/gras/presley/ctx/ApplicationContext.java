/*
 * ApplicationContext
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.ctx;

import gras.presley.evaluator.EvaluatorRegistry;
import gras.presley.evaluator.StateManager;
import gras.presley.interceptor.InterceptorManager;
import gras.presley.interceptor.InterceptorRegistry;
import gras.presley.metadata.TypeManager;
import gras.presley.operation.OperationManager;
import gras.presley.operation.OperationRegistry;
import lombok.NonNull;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public abstract class ApplicationContext {

    @NonNull private static ApplicationContext instance = null;

    @NonNull static ApplicationContext instance() {
        return instance;
    }

    @NonNull
    public static TypeManager getTypeManager() {
        return instance().getTypeManagerImpl();
    }

    @NonNull
    protected abstract TypeManager getTypeManagerImpl();

    @NonNull
    public static InterceptorRegistry getInterceptorRegistry() {
        return instance().getInterceptorRegistryImpl();
    }

    @NonNull
    protected abstract InterceptorRegistry getInterceptorRegistryImpl();

    @NonNull
    public static InterceptorManager getInterceptorManager() {
        return instance().getInterceptorManagerImpl();
    }

    @NonNull
    protected abstract InterceptorManager getInterceptorManagerImpl();

    @NonNull
    public static EvaluatorRegistry getEvaluatorRegistry() {
        return instance().getEvaluatorRegistryImpl();
    }

    @NonNull
    protected abstract EvaluatorRegistry getEvaluatorRegistryImpl();

    @NonNull
    public static StateManager getStateManager() {
        return instance().getStateManagerImpl();
    }

    @NonNull
    protected abstract StateManager getStateManagerImpl();

    @NonNull
    public static OperationRegistry getOperationRegistry() {
        return instance().getOperationRegistryImpl();
    }

    @NonNull
    protected abstract OperationRegistry getOperationRegistryImpl();

    @NonNull
    public static OperationManager getOperationManager() {
        return instance().getOperationManagerImpl();
    }

    @NonNull
    protected abstract OperationManager getOperationManagerImpl();
}
