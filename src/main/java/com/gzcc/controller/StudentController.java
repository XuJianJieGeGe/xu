package com.gzcc.controller;

import antlr.debug.MessageAdapter;
import com.gzcc.entity.CountStudentByProfession;
import com.gzcc.entity.ExcelData;
import com.gzcc.entity.Message;
import com.gzcc.entity.Student;
import com.gzcc.repository.StudentRepository;
import com.gzcc.service.ImportService;
import com.gzcc.utils.EncryptOrDecryptPassordUtils;
import com.gzcc.utils.ExportExcelUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

/**
 * Created by jie on 2018/4/14.
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    //文件上传后的路径
    @Value("${web.upload-path}")
    private String fileUploadPath;


    @RequestMapping("/register")
    public String register(){
        return "register.btl";
    }



    /**
     * 返回学生首页的视图
     * @return
     */
    @RequestMapping("/students")
    public ModelAndView students(){
        ModelAndView  mv = new ModelAndView();
        mv.setViewName("students.btl");
        return mv;
    }


    /**
     * 查询所有学生的信息
     * @return
     */
    @GetMapping("/getlist")
    @ResponseBody
    public List<Student> findAllStudent(){
        List<Student> students = studentRepository.findAll();
        return students;
    }


    //分页查询
    @RequestMapping(value = "/findAllStudentByPage",method = RequestMethod.GET)
    @ResponseBody
    public Page<Student> findAllByPage(@RequestParam(value = "pageNum",defaultValue = "0")int pageNum){
        Sort sort = new Sort("studentId");
        PageRequest request = new PageRequest(pageNum,2,sort);
        Page<Student> page = studentRepository.findAll(request);
        return page;
    }

    /**
     * 根据学生的姓名模糊查询学生的信息
     */
    @GetMapping("/search")
    @ResponseBody
    public List<Student> findByStudentNameContaining(@RequestParam("studentName")String studentName){
        List<Student> students = studentRepository.findByStudentNameContaining(studentName);
        return students;
    }


    @GetMapping("/checkStudentName")
    @ResponseBody
    public Student checkStudentName(@RequestParam("studentName")String studentName){
        Student students = studentRepository.findByStudentName(studentName);
        return students;
    }

    /**
     * 添加学生的信息
     */
    @RequestMapping(value = "/addStudent")
    @ResponseBody
    public Student addStudent(Student student,@RequestParam(value = "file",required = false) MultipartFile file){
        //从数据库查找有没有该对象
        Student student1 =null;
        //判断是新增还是编辑
        if(student.getStudentId()!=0){
                    student1 = studentRepository.getOne(student.getStudentId());
                    //点击编辑的时候密码如果不变就不再加密
                    if (EncryptOrDecryptPassordUtils.encryptBasedDes(student.getStudentPassword()).equals(EncryptOrDecryptPassordUtils.encryptBasedDes(student1.getStudentPassword()))) {
                        student.setStudentPassword(student.getStudentPassword());
                    }else {
                        //密码加密
                        String encryptPassword = EncryptOrDecryptPassordUtils.encryptBasedDes(student.getStudentPassword());
                        student.setStudentPassword(encryptPassword);
                    }
           }else{
                 //新增，密码加密
                String encryptPassword = EncryptOrDecryptPassordUtils.encryptBasedDes(student.getStudentPassword());
                student.setStudentPassword(encryptPassword);
         }
        //不上传头像用默认头像
        if (file.isEmpty()) {
                if(student.getStudentId()==0){
                    student.setStudentImg("moRenTouXiang.jpg");
                }else{
//                    Student student1 = studentRepository.getOne(student.getStudentId());
                    student.setStudentImg(student1.getStudentImg());
                }
                studentRepository.save(student);
        }else {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            logger.info("上传的文件名为：" + fileName);
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            logger.info("上传的后缀名为：" + suffixName);

            // 解决中文问题，liunx下中文路径，图片显示问题
            fileName = UUID.randomUUID() + suffixName;
            File dest = new File(fileUploadPath + fileName);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                file.transferTo(dest);
                 //把图片保存到数据库
                student.setStudentImg(fileName);
                studentRepository.save(student);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("解密："+EncryptOrDecryptPassordUtils.decryptBasedDes(student.getStudentPassword()));

        return student;
    }






    /**
     * 修改学生的信息
     */
    @PostMapping(value = "/edit")
    @ResponseBody
    public Message updateStudent(Student student,@RequestParam("oldStudentName")String oldStudentName){

        Message message = new Message();
        Student student1 = studentRepository.findByStudentName(student.getStudentName());
        if(student1==null){
             //数据库里不存在修改后的用户名，直接修改
               studentRepository.save(student);
               message.setStatus(200);
        }else{
            //修改的用户名与修改用户名相同，更新该用户的其他信息
            if (oldStudentName.equals(student1.getStudentName())){
                studentRepository.save(student);
                message.setStatus(200);
            }else {
                //修改失败
                message.setStatus(500);
                message.setErrorMsg("用户名已经存在！");
            }
        }
        return message;
    }

    /**
     * 删除学生的信息
     */
    @GetMapping(value = "/delete")
    @ResponseBody
    public Message deleteStudent(@RequestParam("studentId") int studentId){
        studentRepository.deleteById(studentId);
        Message message = new Message();
        message.setStatus(200);
        return message;
    }


//    @Autowired
//    private CountStudentByProfessionRepository countStudentByProfessionRepository;
    /**
     * 学生的来源比例
     */
    @GetMapping("/studentAcademy")
    @ResponseBody
    public List<CountStudentByProfession> countStudentByProfession(){
        List<CountStudentByProfession> countStudentByProfessions = new ArrayList<>();

        String profession = null;
        int professionNum = 0;
        CountStudentByProfession countStudentByProfession = null;

        List<Map<String,Object>> maps = studentRepository.findStudentProfessionGroupNumByStudentProfession();

        //第一种：普遍使用，二次取值
        System.out.println("通过Map.keySet遍历key和value：");
        int i = 0;
        for(Map<String,Object> map:maps) {
            for (String key : map.keySet()) {
                  //System.out.println("key= " + key + " and value= " + map.get(key));
                    if(i==0){
                        //获取系别
                       profession = (String)map.get(key);
                    }else{
                        //获取人数
                        BigInteger bigInteger = (BigInteger)map.get(key);
                        professionNum = bigInteger.intValue();
                    }
                  i++;
            }
            i=0;
            countStudentByProfession = new CountStudentByProfession(profession,professionNum);
            countStudentByProfessions.add(countStudentByProfession);

        }

        for(CountStudentByProfession countStudentByProfession1:countStudentByProfessions){
            System.out.println(countStudentByProfessions);
        }
        return countStudentByProfessions;
    }


    @RequestMapping("/importExcel")
    public String index(){
        return "importExcel.btl";
    }



     @Autowired
     private ImportService importService;

    //导入excel
    @RequestMapping(value="/import",method= RequestMethod.POST)
    @ResponseBody
    public String  uploadExcel(HttpServletRequest request) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        InputStream inputStream =null;
        List<List<Object>> list = null;
        MultipartFile file = multipartRequest.getFile("filename");
        if(file.isEmpty()){
            return "文件不能为空";
        }
        inputStream = file.getInputStream();
        list = importService.getBankListByExcel(inputStream,file.getOriginalFilename());
        inputStream.close();
        //连接数据库部分
        for (int i = 0; i < list.size(); i++) {
            List<Object> lo = list.get(i);
            System.out.println(lo);
            Student student = new Student();
            student.setStudentNumber(String.valueOf(lo.get(0)));
            student.setStudentName(String.valueOf(lo.get(1)));
            student.setStudentPassword(String.valueOf(lo.get(2)));
            student.setStudentTruename(String.valueOf(lo.get(3)));
            student.setSex(String.valueOf(lo.get(4)));
            student.setStudentSchool(String.valueOf(lo.get(5)));
            student.setStudentAcademy(String.valueOf(lo.get(6)));
            student.setStudentProfession(String.valueOf(lo.get(7)));
            student.setStudentClass(String.valueOf(lo.get(8)));
            student.setStudentEmail(String.valueOf(lo.get(9)));
            student.setStudentImg(String.valueOf(lo.get(10)));
            //把数据保存到数据库里
            studentRepository.save(student);
        }
        return "上传成功";
    }


//    //导出excel
//    @RequestMapping(value = "/exportExcel", method = RequestMethod.GET)
//    public void excel(HttpServletResponse response) throws Exception {
//        ExcelData data = new ExcelData();
//        data.setName("学生信息表");
//        List<String> titles = new ArrayList();
//        titles.add("学号");
//        titles.add("用户名");
//        titles.add("密码");
//        titles.add("真实姓名");
//        titles.add("性别");
//        titles.add("学校");
//        titles.add("学院");
//        titles.add("专业");
//        titles.add("班级");
//        titles.add("邮箱");
//        titles.add("图片");
//        data.setTitles(titles);
//
//        List<List<Object>> rows = new ArrayList();
//        List<Object> row = new ArrayList();
//
//        List<Student> students = studentRepository.findAll();
//        for(Student student:students){
//            row.add(student.getStudentNumber());
//            row.add(student.getStudentName());
//            row.add(student.getStudentPassword());
//            row.add(student.getStudentTruename());
//            row.add(student.getSex());
//            row.add(student.getStudentSchool());
//            row.add(student.getStudentAcademy());
//            row.add(student.getStudentProfession());
//            row.add(student.getStudentClass());
//            row.add(student.getStudentEmail());
//            row.add(student.getStudentImg());
//
//        }
//        rows.add(row);
//        data.setRows(rows);
//
////        row.add("11111111111");
////        row.add("22222222222");
////        row.add("3333333333");
//        //生成本地
//        File f = new File("C:\\Users\\jie\\Desktop\\hello.xlsx");
//        FileOutputStream out = new FileOutputStream(f);
//        ExportExcelUtils.exportExcel(data, out);
//        out.close();
////        ExportExcelUtils.exportExcel(response,"hello.xlsx",data);
//    }


    //导出学生信息
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void downloadAllClassmate(HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("学生信息表");
        //查询所有的员工信息
        List<Student> students = studentRepository.findAll();
//        //设置文件输出的路径
//        File f = new File("C:\\Users\\jie\\Desktop\\员工信息.xlsx");
//        FileOutputStream out = new FileOutputStream(f);

        String fileName = "students"  + ".xls";//设置要导出的文件的名字
        //新增数据行，并且设置单元格数据
        int rowNum = 1;

        //headers表示excel表中第一行的表头
        String[] headers = { "学号", "用户名", "密码","真实姓名","性别","学院",
                             "学校", "专业","班级","邮箱","图片"};

        HSSFRow row = sheet.createRow(0);
        //在excel表中添加表头

        for(int i=0;i<headers.length;i++){
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

        //在表中存放查询到的数据放入对应的列
        for(Student student:students){
            HSSFRow row1 = sheet.createRow(rowNum);
            row1.createCell(0).setCellValue(student.getStudentNumber());
            row1.createCell(1).setCellValue(student.getStudentName());
            row1.createCell(2).setCellValue(student.getStudentPassword());
            row1.createCell(3).setCellValue(student.getStudentTruename());
            row1.createCell(4).setCellValue(student.getSex());
            row1.createCell(5).setCellValue(student.getStudentSchool());
            row1.createCell(6).setCellValue(student.getStudentAcademy());
            row1.createCell(7).setCellValue(student.getStudentProfession());
            row1.createCell(8).setCellValue(student.getStudentClass());
            row1.createCell(9).setCellValue(student.getStudentEmail());
            row1.createCell(10).setCellValue(student.getStudentImg());
            rowNum++;
        }
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        workbook.write(response.getOutputStream());
    }







}
