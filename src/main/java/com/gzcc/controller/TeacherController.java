package com.gzcc.controller;

import com.gzcc.entity.Student;
import com.gzcc.entity.Teacher;
import com.gzcc.repository.TeacherRepository;
import com.gzcc.service.ImportService;
import com.gzcc.utils.EncryptOrDecryptPassordUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

/**
 * Created by jie on 2018/4/14.
 */
@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    //文件上传后的路径
    @Value("${web.upload-path}")
    private String fileUploadPath;

    @RequestMapping("/importExcel")
    public String index(){
        return "importTeacherExcel.btl";
    }


    /**
     * 返回学生首页的视图
     * @return
     */
    @RequestMapping("/teachers")
    public ModelAndView students(){
        ModelAndView  mv = new ModelAndView();
        mv.setViewName("teachers.btl");
        return mv;
    }


//    /**
//     * 查询所有学生的信息
//     * @return
//     */
//    @GetMapping("/findAllStudent")
//    @ResponseBody
//    public List<Student> findAllStudent(){
//        List<Student> students = studentRepository.findAll();
//        return students;
//    }

    @RequestMapping(value = "/findAllTeacherByPage",method = RequestMethod.GET)
    @ResponseBody
    public Page<Teacher> findAllByPage(@RequestParam(value = "pageNum",defaultValue = "0")int pageNum){
        Sort sort = new Sort("teacherId");
        PageRequest request = new PageRequest(pageNum,2,sort);
        Page<Teacher> page = teacherRepository.findAll(request);
        return page;
    }

    /**
     * 根据教师的姓名模糊查询教师的信息
     */
    @GetMapping("/findByTeacherName")
    @ResponseBody
    public List<Teacher> findByTeacherNameContaining(@RequestParam("teacherName")String teacherName){
        List<Teacher> teachers = teacherRepository.findByTeacherNameContaining(teacherName);
        return teachers;
    }

    @GetMapping("/checkTeacherName")
    @ResponseBody
    public Teacher checkTeacherName(@RequestParam("teacherName")String teacherName){
        Teacher teacher = teacherRepository.findByTeacherName(teacherName);
        return teacher;
    }

    /**
     * 添加或编辑老师的信息
     */
    @RequestMapping(value = "/addTeacher")
    @ResponseBody
    public Teacher addTeacher(Teacher teacher,@RequestParam(value = "file",required = false) MultipartFile file){
        //从数据库查找有没有该对象
        Teacher teacher1 =null;
        //判断是新增还是编辑
        if(teacher.getTeacherId()!=0){
            teacher1 = teacherRepository.getOne(teacher.getTeacherId());
            //点击编辑的时候密码如果不变就不再加密
            if (EncryptOrDecryptPassordUtils.encryptBasedDes(teacher.getTeacherPassword()).equals(EncryptOrDecryptPassordUtils.encryptBasedDes(teacher1.getTeacherPassword()))) {
                  teacher.setTeacherPassword(teacher.getTeacherPassword());
            }else {
                //密码加密
                String encryptPassword = EncryptOrDecryptPassordUtils.encryptBasedDes(teacher.getTeacherPassword());
                teacher.setTeacherPassword(encryptPassword);
            }
        }else{
            //新增，密码加密
            String encryptPassword = EncryptOrDecryptPassordUtils.encryptBasedDes(teacher.getTeacherPassword());
            teacher.setTeacherPassword(encryptPassword);
        }
        //不上传头像用默认头像
        if (file.isEmpty()) {
            if(teacher.getTeacherId()==0){
                teacher.setTeacherImg("moRenTouXiang.jpg");
            }else{
                  teacher.setTeacherImg(teacher1.getTeacherImg());
            }
            teacherRepository.save(teacher);
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
                teacher.setTeacherImg(fileName);
                teacherRepository.save(teacher);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return teacher;
    }

    /**
     * 修改老师的信息
     */
    @PostMapping(value = "/updateTeacher")
    @ResponseBody
    public Teacher updateTeacher(Teacher teacher){
        teacherRepository.save(teacher);
        return teacher;
    }

    /**
     * 删除学生的信息
     */
    //删除用户
    @GetMapping(value = "/deleteTeacher")
    @ResponseBody
    public String deleteTeacher(@RequestParam("teacherId") int teacherId){
        teacherRepository.deleteById(teacherId);
        return "success";
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
            Teacher teacher = new Teacher();
            teacher.setTeacherName(String.valueOf(lo.get(0)));
            teacher.setTeacherPassword(String.valueOf(lo.get(1)));
            teacher.setTeacherTruename(String.valueOf(lo.get(2)));
            teacher.setSex(String.valueOf(lo.get(3)));
            teacher.setTeacherSchool(String.valueOf(lo.get(4)));
            teacher.setTeacherAcademy(String.valueOf(lo.get(5)));
            teacher.setTeacherPhoneNumber(String.valueOf(lo.get(6)));
            teacher.setTeacherEmail(String.valueOf(lo.get(5)));
            teacher.setTeacherImg(String.valueOf(lo.get(8)));
            //把数据保存到数据库里
            teacherRepository.save(teacher);
        }
        return "上传成功";
    }



    //导出教师的信息
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void downloadAllClassmate(HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("学生信息表");
        //查询所有的员工信息
         List<Teacher> teachers = teacherRepository.findAll();
//        //设置文件输出的路径
//        File f = new File("C:\\Users\\jie\\Desktop\\员工信息.xlsx");
//        FileOutputStream out = new FileOutputStream(f);

        String fileName = "teachers"  + ".xls";//设置要导出的文件的名字
        //新增数据行，并且设置单元格数据
        int rowNum = 1;

        //headers表示excel表中第一行的表头
        String[] headers = { "用户名", "密码","真实姓名","性别","学校",
                             "学院","电话号码","邮箱","图片"};

        HSSFRow row = sheet.createRow(0);
        //在excel表中添加表头

        for(int i=0;i<headers.length;i++){
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

        //在表中存放查询到的数据放入对应的列
        for(Teacher teacher:teachers){
            HSSFRow row1 = sheet.createRow(rowNum);
            row1.createCell(0).setCellValue(teacher.getTeacherName());
            row1.createCell(1).setCellValue(teacher.getTeacherPassword());
            row1.createCell(2).setCellValue(teacher.getTeacherTruename());
            row1.createCell(3).setCellValue(teacher.getSex());
            row1.createCell(4).setCellValue(teacher.getTeacherSchool());
            row1.createCell(5).setCellValue(teacher.getTeacherAcademy());
            row1.createCell(6).setCellValue(teacher.getTeacherPhoneNumber());
            row1.createCell(7).setCellValue(teacher.getTeacherEmail());
            row1.createCell(8).setCellValue(teacher.getTeacherImg());
            rowNum++;
        }
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        workbook.write(response.getOutputStream());
    }





}
