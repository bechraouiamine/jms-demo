package com.bechraoui.jms.sender;

import com.bechraoui.jms.configuration.JmsConfig;
import com.bechraoui.jms.model.HelloWorldMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloSender {

    private final JmsTemplate jmsTemplate;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage(){

        System.out.println("I'ma sending a message");

        HelloWorldMessage message = HelloWorldMessage.builder().
                id(UUID.randomUUID()).message("Hello World !!!").build();

        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);

        System.out.println("Message Sent !!! ");
    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {

        System.out.println("I'ma sending a message");

        HelloWorldMessage message = HelloWorldMessage.builder().
                id(UUID.randomUUID()).message("Hello World !!!").build();

        Message msg = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message helloMessage = null;
                try {
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                } catch (JsonProcessingException e) {
                    throw new JMSException("jms boom");
                }
                helloMessage.setStringProperty("_type",
                        "com.bechraoui.jms.model.HelloWorldMessage");

                return helloMessage;
            }
        });

        System.out.println(msg.getBody(String.class));
    }
}
