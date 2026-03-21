package com.example.consumer;


import com.example.respository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;

public class AuthServiceConsumer {

    private UserRepository userRepository;

    AuthServiceConsumer(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @KafkaListener(topics ="",groupId = "")
    public void listen(Object eventDat){
        try{

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
