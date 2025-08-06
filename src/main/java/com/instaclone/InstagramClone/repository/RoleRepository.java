package com.instaclone.InstagramClone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.instaclone.InstagramClone.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

}
