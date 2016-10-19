package io.arusland.allinone;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by ruslan on 24.06.2016.
 */
public class Message {
    private final String message;
    private final String type;

    public Message(String message, String type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }
    
    public String getMessageEscaped() {
        return StringEscapeUtils.escapeHtml4(message);
    }

    public String getType() {
        return type;
    }
}
