package com.popple.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.popple.auth.entity.User;

@Repository
public interface UserRespository extends JpaRepository<User, Long>{
	
}
