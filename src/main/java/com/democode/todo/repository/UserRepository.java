package com.democode.todo.repository;


import com.democode.todo.entity.Role;
import com.democode.todo.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    List<Users> findByRole(Role role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
