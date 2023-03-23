package com.xxfs.fsapischoolcrawler.service.imp;

import cn.hutool.core.text.CharSequenceUtil;
import com.xxfs.fsapicommon.model.dto.StudentDTO;
import com.xxfs.fsapicommon.service.StudentService;
import com.xxfs.fsapischoolcrawler.manager.CookiesManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

import static com.xxfs.fsapicommon.constant.StudentConstants.*;

@DubboService
@Slf4j
public class StudentServiceImpl implements StudentService {

    @Resource
    private CookiesManager cookiesManager;

    /**
     * @param student
     * @return
     * @throws IOException
     */
    @Override
    public Map<String, Object> loginAndGetCourse(StudentDTO student) throws IOException {
        login(student);
        Map<String, Object> course = getCourse();
        log.error(String.valueOf(course));
        return course;
    }

    @Override
    public Boolean checkStudent(StudentDTO student) throws IOException {

        try {
            login(student);
            getCourse();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param student
     * @return
     * @throws IOException
     */
    @Override
    public void login(StudentDTO student) throws IOException {
        // 1. 获取登录表单数据
        Map<String, String> loginData = getLoginFormData();

        // 2. 载入登录数据
        Connection connect = Jsoup.connect(INIT_URL + LOGIN_URL);
        connect.data("username", student.getStudentNumber())
                .data("password", student.getPassword())
                .data(RANDOM, loginData.get(RANDOM))
                .data(loginData.get("randomKey"), loginData.get("randomValue"))
                .timeout(CONNECT_TIMEOUT)
                .method(Connection.Method.POST);
        Connection.Response response = connect.execute();
        cookiesManager.setCookies(response.cookies());
    }

    // 获取登录表单数据
    private Map<String, String> getLoginFormData() throws IOException {
        Map<String, String> loginData = new HashMap<>();

        // 解析表单页面，获取所有输入框
        Document document = Jsoup.parse(new URL(INIT_URL), 3000);
        Elements inputs = document.getElementsByTag("input");

        // 遍历所有输入框
        for (Element input : inputs) {
            // 如果输入框 ID 是随机数字段，将其加入登录数据
            if (RANDOM.equals(input.id())) {
                loginData.put(input.id(), input.val());
            }
            // 如果输入框值的长度大于 20，则该输入框的 name 属性值是随机数字段，将其加入登录数据
            if (input.val().length() > 20) {
                Attributes attributes = input.attributes();
                String randomKey = attributes.get("name");
                loginData.put("randomKey", randomKey);
                loginData.put("randomValue", input.val());
            }
        }

        return loginData;
    }


    /**
     * @return
     * @throws IOException
     */
    @Override
    public Map<String, Object> getCourse() throws IOException {
        // 创建 Jsoup 连接对象并设置相关参数
        Connection conn = Jsoup.connect(INIT_URL + COURES_URL);
        conn.cookies(cookiesManager.getCookies())
                .timeout(10000)
                .method(Connection.Method.GET);

        // 发送请求并获取响应结果
        Connection.Response res = conn.execute();

        // 解析 DOM 并收集课表数据
        Document doc = res.parse();
        List<List<String>> courseLists = new ArrayList<>();

        // 获取姓名和课表数据
        String name = doc.selectFirst(".style16").text().split("\\s+")[3];
        Elements tbodys = doc.getElementsByTag("tbody");
        if (CharSequenceUtil.isBlank(String.valueOf(tbodys))) {
            return Collections.emptyMap();
        }
        Element studentTable = tbodys.last();

        // 解析课表数据并转换格式
        for (int i = 0; i < 9; i++) {
            List<String> courseList = new ArrayList<>();
            Element row = studentTable.child(i);
            row.children().forEach(cell -> courseList.add(cell.text()));
            courseLists.add(courseList);
        }

        // 修改数据格式方便前端展示
        List<String> weekList = courseLists.remove(0);
        weekList.set(0, "time");
        weekList.set(1, "one");
        weekList.set(2, "tow");
        weekList.set(3, "three");
        weekList.set(4, "four");
        weekList.set(5, "five");
        weekList.set(6, "six");
        weekList.set(7, "seven");

        List<Map<String, String>> lists = new ArrayList<>();
        for (List<String> courseList : courseLists) {
            Map<String, String> map = new LinkedHashMap<>();
            IntStream.range(0, courseList.size())
                    .forEach(j -> map.put(weekList.get(j), courseList.get(j)));
            lists.add(map);
        }

        // 整合并返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("table", lists);
        data.put("name", name);
        return data;
    }

}
