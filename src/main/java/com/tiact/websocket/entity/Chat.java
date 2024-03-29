package com.tiact.websocket.entity;

import lombok.Data;

/**
 * @author Tia_ct
 */
@Data
public class Chat {
    private MessageType type;
    private String content;
    private String sender;

    public enum MessageType { //
        CHAT,
        JOIN,
        LEAVE
    }

}
