package com.gzcc.entity;

import org.hibernate.annotations.GeneratorType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by jie on 2018/4/14.
 */
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;//学生id

   private String studentNumber;//学号

    private String studentName;//学生的用户名

    private String studentPassword;//密码

    private String studentTruename;//学生的真实姓名

    private String sex;        //学生的性别

    private String studentSchool;//学生所在的学校

    private String  studentAcademy;//学生所在的学院

    private String studentProfession;//学生专业

    private String  studentClass;//学生所在的班级

    private String studentEmail;//学生邮箱

    private String studentImg;//学生的头像

    public Student() {
    }

    public Student(String studentNumber, String studentName, String studentPassword, String studentTruename, String sex, String studentSchool, String studentAcademy, String studentProfession, String studentClass, String studentEmail, String studentImg) {
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.studentPassword = studentPassword;
        this.studentTruename = studentTruename;
        this.sex = sex;
        this.studentSchool = studentSchool;
        this.studentAcademy = studentAcademy;
        this.studentProfession = studentProfession;
        this.studentClass = studentClass;
        this.studentEmail = studentEmail;
        this.studentImg = studentImg;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentPassword() {
        return studentPassword;
    }

    public void setStudentPassword(String studentPassword) {
        this.studentPassword = studentPassword;
    }

    public String getStudentTruename() {
        return studentTruename;
    }

    public void setStudentTruename(String studentTruename) {
        this.studentTruename = studentTruename;
    }

    public String getStudentSchool() {
        return studentSchool;
    }

    public void setStudentSchool(String studentSchool) {
        this.studentSchool = studentSchool;
    }

    public String getStudentAcademy() {
        return studentAcademy;
    }

    public void setStudentAcademy(String studentAcademy) {
        this.studentAcademy = studentAcademy;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentImg() {
        return studentImg;
    }

    public void setStudentImg(String studentImg) {
        this.studentImg = studentImg;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStudentProfession() {
        return studentProfession;
    }

    public void setStudentProfession(String studentProfession) {
        this.studentProfession = studentProfession;
    }
}
