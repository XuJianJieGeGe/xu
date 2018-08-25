package com.gzcc.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by jie on 2018/4/23.
 */
public class CountStudentByProfession {
    private String studentProfession;//系别

    private int studentNumber;//学生人数

    public CountStudentByProfession(String studentProfession, int studentNumber) {
        this.studentProfession = studentProfession;
        this.studentNumber = studentNumber;
    }

    public int getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentProfession() {

        return studentProfession;
    }

    public void setStudentProfession(String studentProfession) {
        this.studentProfession = studentProfession;
    }

    @Override
    public String toString() {
        return "CountStudentByProfession{" +
                "studentProfession='" + studentProfession + '\'' +
                ", studentNumber=" + studentNumber +
                '}';
    }
}
