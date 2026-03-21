package com.example.deserializer;

import com.example.entities.UserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class UserInfoDeserializer implements Deserializer<UserInfoDto> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public UserInfoDto deserialize(String arg0, byte[] arg1) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInfoDto userInfoDto = null;

        try{
            userInfoDto = objectMapper.readValue(arg1,UserInfoDto.class);
        }catch (Exception e){
            System.out.println("can not deserialize");
        }

        return userInfoDto;
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
