package com.bhaskarshashwath.expense.tracker.repository;

import com.bhaskarshashwath.expense.tracker.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {
    public UserInfo findByUsername(String username);
}
