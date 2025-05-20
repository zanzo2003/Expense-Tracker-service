package com.bhaskarshashwath.expense.tracker.services;

import com.bhaskarshashwath.expense.tracker.entities.UserInfo;
import com.bhaskarshashwath.expense.tracker.entities.UserRoles;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



public class CustomUserDetails extends UserInfo implements UserDetails {

    private String username;

    private String password;

    Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UserInfo user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        List<GrantedAuthority> authorityList = new ArrayList<>();
        for(UserRoles authority: user.getRoles()){
            authorityList.add(new SimpleGrantedAuthority(authority.getName().toUpperCase()));
        }
        this.authorities = authorityList;
    }

    @Override
    public String getPassword(){
        return this.password;
    }

    @Override
    public String getUsername(){
        return this.username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return this.authorities;
    }
}
