package com.example.bootcrud.services;

import com.example.bootcrud.dao.UserDao;
import com.example.bootcrud.dto.UserDto;
import com.example.bootcrud.entities.Role;
import com.example.bootcrud.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class UserServiceImplement implements UserService, UserDetailsService {

    private UserDao userDao;
    private UserConverter converter;

    @Autowired
    public void setConverter(UserConverter converter) {
        this.converter = converter;
    }

    @Autowired
    public void setUserRepo(UserDao userDao) {
        this.userDao = userDao;
    }

    public User findByFirstname(String firstname) {
        return userDao.findByFirstname(firstname);
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = findByFirstname(s);
        if(user == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", s));
        }


        return new org.springframework.security.core.userdetails.User(user.getFirstname(), user.getPassword()
                    , mapRoleToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRoleToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r-> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }


    @Override
    public void saveUser(UserDto userDto){
        userDao.save(converter.fromUserDtotoUser(userDto));
    }

    @Override
    public void updateUser(UserDto userDto){
        User user = userDao.read(userDto.getId());
        user.setFirstname(userDto.getFirstname());
        user.setLastname(userDto.getLastname());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setAge(userDto.getAge());
        user.setRoles(userDto.getRoles());
        userDao.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userDao.delete(id);
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userDao.findByFirstname(username);
        if (user != null) {
            return converter.fromUserToUserDto(user);
        }
        return null;
    }


    @Override
    public List<UserDto> findAll() {
        return userDao.index()
                .stream()
                .map(converter::fromUserToUserDto)
                .collect(Collectors.toList());
    }

//    private void validateUserDto(UserDto userDto) throws ValidationException{
//        if(isNull(userDto)) {
//            throw new ValidationException("Object user is null!");
//        }
//
//        if (isNull(userDto.getFirstname()) || userDto.getFirstname().isEmpty()) {
//
//            throw new ValidationException("Name is empty!");
//        }
//
//        if(isNull(userDto.getPassword()) || userDto.getPassword().isEmpty()) {
//            throw new ValidationException("Password is empty!");
//        }
//
//        if(isNull(userDto.getEmail()) || userDto.getEmail().isEmpty()) {
//            throw new ValidationException("Email is empty!");
//        }
//
//        if(isNull(userDto.getRoles()) || userDto.getRoles().isEmpty()) {
//            throw new ValidationException("Roles is empty!");
//        }
//    }
}
