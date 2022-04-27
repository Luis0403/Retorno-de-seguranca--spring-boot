package com.algamoney.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.algamoney.model.Roles;
import com.algamoney.model.User;
import com.algamoney.repository.RolesRepository;
import com.algamoney.repository.UserRepository;

@Service
public class UserServicesImpl implements UserServices, UserDetailsService {
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private UserRepository userRepository;
    //@Autowired
    private PasswordEncoder passwordEncoder=new  BCryptPasswordEncoder();

    @Override
    public User saveUser(User user) {
        // TODO Auto-generated method stub
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Roles saveRoles(Roles roles) {
        // TODO Auto-generated method stub
        return rolesRepository.save(roles);
    }

    @Override
    public void addRolesToUser(String username, String rolename) {
        // TODO Auto-generated method stub
        User user=userRepository.findByUsername(username);
        Roles roles=rolesRepository.findByName(rolename);
        user.getRoles().add(roles);

    }

    @Override
    public User getUser(String username) {
        // TODO Auto-generated method stub
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getusers() {
        // TODO Auto-generated method stub
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findByUsername(username);

        if(user==null) throw new UsernameNotFoundException("user not in the DataBase");
        else {
        }

        Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();

        user.getRoles().forEach(roles->{
            authorities.add(new SimpleGrantedAuthority(roles.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorities);
    }

}

