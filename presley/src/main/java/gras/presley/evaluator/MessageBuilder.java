/*
 * MessageBuilder
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.evaluator;

import java.util.List;

/**
 * Just a draft,
 * here we can collect them with the severity also we should get back with the severity.
 *
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public interface MessageBuilder {

    void addMessage(String message, MessageSeverity severity);

    List<String> getMessages();

    MessageSeverity getHighestSeverity();
}
