package com.tiact.websocket.service.impl;


import com.tiact.websocket.entity.Chat;
import com.tiact.websocket.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

/**
 * @author Tia_ct
 */
@Service
public class ChatServceImpl implements ChatService {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Override
    public Chat send(Chat chat) {
        if(chat.getContent()!=null&&!"".equals(chat.getContent())){
        }else{
            chat.setType(Chat.MessageType.CHAT);
            chat.setSender("法海");
            chat.setContent("大威天龙");
        }
        System.out.println(chat.toString());
        messagingTemplate.convertAndSend("/topic/public", chat);
        return chat;
    }
}
