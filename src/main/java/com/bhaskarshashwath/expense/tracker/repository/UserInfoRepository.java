package com.bhaskarshashwath.expense.tracker.repository;

import com.bhaskarshashwath.expense.tracker.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, String> {
    public UserInfo findByUsername(String username);
}
