package com.gzcc.repository;

import com.gzcc.entity.Student;
import com.gzcc.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by jie on 2018/4/14.
 */
public interface TeacherRepository extends JpaRepository<Teacher,Integer> {
    public List<Teacher> findByTeacherNameContaining(String teacherName);
    public Page<Teacher> findAll(Pageable pageable);
    public Teacher findByTeacherName(String StudentName);
}
