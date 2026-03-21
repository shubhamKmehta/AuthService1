package com.example.respository;

import com.example.entities.UserInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserInfoDto,String> {

    UserInfoDto findByUserId(String userId);
}
