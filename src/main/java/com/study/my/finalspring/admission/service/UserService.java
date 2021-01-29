package com.study.my.finalspring.admission.service;

import com.study.my.finalspring.admission.dto.UserTo;
import com.study.my.finalspring.admission.model.Role;
import com.study.my.finalspring.admission.model.User;
import com.study.my.finalspring.admission.repository.UserRepository;
import com.study.my.finalspring.admission.util.UserUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private UserRepository repository;
    private BCryptPasswordEncoder passwordEncoder;


    public UserService(UserRepository repository, @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAll() {
        List<User> list = repository.findAll();
        return list.stream()
                .filter(user -> user.getRoles().contains(Role.ROLE_USER))
                .collect(Collectors.toList());
    }

    public Page<User> getAll(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, 10, Sort.by("lastName"));
        return repository.findByRoles(Role.ROLE_USER, pageable);
    }

    public User getByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    //TODO change this method return type to void, if user exists, throw exception
    public boolean create(User user) {
        if (user.getId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singleton(Role.ROLE_USER));
            repository.save(user);
            return true;
        }
        return false;
    }

    //TODO change this method return type to void, if user not exist, throw exception
    public boolean update(User user) {
        if (user.getId() == null) {
            return false;
        }
        repository.save(user);
        return true;
    }

    public boolean createFromTo(UserTo userTo) {
        User user = UserUtil.userFromTo(userTo);
        return create(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getByEmail(email);
        if (user == null) {
            throw new RuntimeException("user not found: " + email);
        }
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return user.getRoles();
            }

            @Override
            public String getPassword() {
                return user.getPassword();
            }

            @Override
            public String getUsername() {
                return user.getEmail();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return user.isEnabled();
            }
        };
    }
}