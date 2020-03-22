/*
 * PlayerNameInterceptor
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.example.interceptor;

import gras.presley.example.bean.Person;
import gras.presley.interceptor.PropertyDef;
import gras.presley.interceptor.PropertyInterceptor;
import gras.presley.interceptor.PropertyInvocation;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class PersonNameInterceptor implements PropertyInterceptor<Person, String> {

    @Override
    @PropertyDef(name = {"firstName", "lastName"})
    public void intercept(PropertyInvocation<Person, String> invocation) throws Exception {
        invocation.proceed(); // call the next interceptor

        Person person = invocation.getBean();
        person.name = person.firstName + " " + person.lastName;
    }
}
