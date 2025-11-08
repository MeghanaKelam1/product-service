package com.app.product_service.repo;

import com.app.product_service.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepo extends JpaRepository<Users,Long> {
    Users findUserByUsername(String username);
}
