package com.gzcc.repository;

import com.gzcc.entity.CountStudentByProfession;
import com.gzcc.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * Created by jie on 2018/4/14.
 */
public interface StudentRepository extends JpaRepository<Student,Integer> {
    public List<Student> findByStudentNameContaining(String StudentName);
    public Student findByStudentName(String StudentName);
    public Page<Student> findAll(Pageable pageable);

    @Query(value = "select student_profession ,count(*) from student group by student_profession",nativeQuery = true)
    public List<Map<String,Object>> findStudentProfessionGroupNumByStudentProfession();


}
