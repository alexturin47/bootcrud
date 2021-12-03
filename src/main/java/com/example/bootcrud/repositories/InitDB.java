package com.example.bootcrud.repositories;

import com.example.bootcrud.entities.Role;
import com.example.bootcrud.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class InitDB {

    UserRepo userRepo;
    RoleRepo roleRepo;

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setRoleRepo(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    @PostConstruct
    private void saveUsers() {
        if (roleRepo.findByname("ADMIN") == null) {
            roleRepo.save(new Role("ADMIN"));
        }
        if (roleRepo.findByname("USER") == null) {
            roleRepo.save(new Role("USER"));
        }

        Set<Role> roles;
        User admin = userRepo.findByFirstname("admin");
        if ( admin == null ) {

            roles = new HashSet<>();
            roles.add(roleRepo.findByname("USER"));
            roles.add(roleRepo.findByname("ADMIN"));
            userRepo.save(new User("admin"
                    , "adminnov"
                    , "$2a$12$AFJsSpNrJlC04sp2qGPcYepkRMy6rs1k3hNxeTxRMj0qZJ/aUK6X2"
                    , "admin@mail.ru"
                    , 30
                    , roles));
        }


        User user = userRepo.findByFirstname("user");
        if ( user == null ) {
            roles = new HashSet<>();
            roles.add(roleRepo.findByname("USER"));
            userRepo.save(new User("user"
                    , "userov"
                    , "$2a$12$wblAIp0iNog81E3RpCdwBuEZ6mqlSIJ/BFfUfFdf4p0y6naziXoGK"
                    , "user@mail.ru"
                    , 30
                    , roles));
        }
    }
}

/*
CREATE table users(
id bigint auto_increment primary key ,
username varchar(50) not null ,
password varchar(60) not null ,
email varchar(50) UNIQUE,
age int
);

create table role(
id bigint auto_increment primary key ,
role varchar(50) not null
);

create table users_roles(
user_id bigint not null,
role_id bigint not null,
primary key (user_id, role_id),
foreign key (user_id) references users(id),
foreign key (role_id) references role(id)
);
*/