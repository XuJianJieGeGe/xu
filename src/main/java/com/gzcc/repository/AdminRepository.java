package com.gzcc.repository;

import com.gzcc.entity.Admin;
import com.gzcc.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by jie on 2018/4/14.
 */
public interface AdminRepository extends JpaRepository<Admin,Integer> {
    public Admin findByAdminName(String AdminName);
    public Page<Admin> findAll(Pageable pageable);
}
