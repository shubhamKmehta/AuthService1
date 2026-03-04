package org.example.service;

import lombok.Data;
import org.example.entities.UserInfo;
import org.example.eventProducer.UserInfoProducer;
import org.example.models.UserInfoDto;
import org.example.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoProducer userInfoProducer;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,UserInfoProducer userInfoProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userInfoProducer= userInfoProducer;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userRepository.findByUsername(username);
        if(userInfo == null){
            throw new UsernameNotFoundException("could not found user details");
        }
        return new CustomUserDetails(userInfo);
    }

    public UserInfo checkIfUserExist(UserInfoDto userInfoDto){
        return userRepository.findByUsername(userInfoDto.getUsername());
    }

    public String singUpUser(UserInfoDto userInfoDto){
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));

        if (Objects.nonNull(checkIfUserExist(userInfoDto))){
            return null;
        }
        String userId = UUID.randomUUID().toString();
        UserInfo userInfo = new UserInfo(userId,userInfoDto.getUsername(),userInfoDto.getPassword(),new HashSet<>());

        userRepository.save(userInfo);

        userInfoProducer.sendEventToKafka(userInfoDto);
        return  userId;

    }

    public String getUserByUsername(String username){
        return Optional.of(userRepository.findByUsername(username)).map(UserInfo::getUserId).orElse(null);
    }


}
