package com.xxfs.fsapischoolcrawler.service.imp;

import cn.hutool.core.text.CharSequenceUtil;
import com.xxfs.fsapicommon.model.dto.StudentDTO;
import com.xxfs.fsapicommon.model.vo.crawler.*;
import com.xxfs.fsapicommon.service.StudentService;
import com.xxfs.fsapischoolcrawler.manager.CookiesManager;
import com.xxfs.fsapischoolcrawler.utils.CrawlerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.xxfs.fsapicommon.constant.StudentConstants.*;

@DubboService
@Slf4j
public class StudentServiceImpl implements StudentService {
  @Resource
  private CookiesManager cookiesManager;

  @Resource
  private CrawlerUtil crawlerUtil;

  @Autowired
  private ThreadPoolExecutor executor;

  /**
   * @param student
   * @return
   * @throws IOException
   */
  @Override
  public Map<String, Object> loginAndGetCourse(StudentDTO student) throws IOException {
    login(student);
    Map<String, Object> course = getCourse(student.getYear(), student.getSemester());
    log.error(String.valueOf(course));
    return course;
  }

  @Override
  public Boolean checkStudent(StudentDTO student) {

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
    connect
            .data("username", student.getStudentNumber())
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
  public Map<String, Object> getCourse(String year, String semester) throws IOException {
    // 发送请求并获取响应结果
    Document doc = crawlerUtil.getDoc(INIT_URL + COURES_URL);

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

  /**
   * 获取学生信息
   *
   * @param student 学生信息
   * @throws IOException
   */
  @Override
  public Map<String, Object> getStudentInfo(StudentDTO student) throws IOException {
    // 登录并获取动态学生ID
    login(student);
    String studentID = getDynamicCode().get("studentID");
    // 通过getDynamicCode获取动态学生id
    Document studentInfoView = crawlerUtil.getDoc(INFO_URL + studentID);

    // 创建异步任务，分别获取必修课程列表、选修课程列表和学分情况列表
    CompletableFuture<ArrayList<ArrayList<ArrayList<String>>>> obligatoryCourseFuture =
            CompletableFuture.supplyAsync(() -> getObligatoryCourse(studentInfoView), executor);

    CompletableFuture<ArrayList<ArrayList<String>>> electiveCourseFuture =
            CompletableFuture.supplyAsync(() -> getElectiveCourse(studentInfoView), executor);

    CompletableFuture<ArrayList<String>> creditSituationFuture =
            CompletableFuture.supplyAsync(() -> getCreditSituation(studentInfoView), executor);

    // 使用 thenApply 方法转换异步任务结果的类型
    CompletableFuture<ArrayList<ArrayList<courseVo>>> obligatoryCourseListFuture =
            obligatoryCourseFuture.thenApply(
                    obligatoryCourse ->
                            obligatoryCourse.stream()
                                    .map(
                                            subList ->
                                                    subList.stream()
                                                            .map(
                                                                    courseData -> {
                                                                      courseVo course = new courseVo();
                                                                      course.setCourseCode(courseData.get(1));
                                                                      course.setCourseName(courseData.get(2));
                                                                      course.setCredits(courseData.get(3));
                                                                      course.setMethod(courseData.get(4));
                                                                      course.setScore(courseData.get(5));
                                                                      return course;
                                                                    })
                                                            .collect(Collectors.toCollection(ArrayList::new)))
                                    .collect(Collectors.toCollection(ArrayList::new)));

    CompletableFuture<ArrayList<courseVo>> electiveCourseListFuture =
            electiveCourseFuture.thenApply(
                    electiveCourse ->
                            electiveCourse.stream()
                                    .map(
                                            courseData -> {
                                              courseVo course = new courseVo();
                                              course.setCourseCode(courseData.get(0));
                                              course.setCourseName(courseData.get(1));
                                              course.setCredits(courseData.get(2));
                                              course.setMethod(courseData.get(3));
                                              course.setScore(courseData.get(4));
                                              return course;
                                            })
                                    .collect(Collectors.toCollection(ArrayList::new)));

    CompletableFuture<creditSituationVo> creditSituationVoFuture =
            creditSituationFuture.thenApply(
                    creditSituation ->
                            new creditSituationVo(
                                    creditSituation.get(0),
                                    creditSituation.get(1),
                                    creditSituation.get(2),
                                    creditSituation.get(3),
                                    creditSituation.get(4),
                                    creditSituation.get(5)));

    // 等待所有异步任务执行结束
    CompletableFuture<Void> allFutures =
            CompletableFuture.allOf(
                    obligatoryCourseListFuture, electiveCourseListFuture, creditSituationVoFuture);
    allFutures.join();

    // 获取异步任务的结果
    ArrayList<ArrayList<courseVo>> obligatoryCourseList = obligatoryCourseListFuture.join();
    ArrayList<courseVo> electiveCourseList = electiveCourseListFuture.join();
    creditSituationVo creditSituationVo = creditSituationVoFuture.join();

    // 整合结果并返回
    Map<String, Object> data = new HashMap<>();
    data.put("obligatoryCourse", obligatoryCourseList);
    data.put("electiveCourse", electiveCourseList);
    data.put("creditSituation", creditSituationVo);
    return data;
  }

  /**
   * 获取考勤信息
   *
   * @return
   */
  @Override
  public Map<String, Object> getAttendance(StudentDTO student) throws IOException {
    login(student);

    String studentId = getStudentId();
    String studentCode = student.getStudentNumber();
    String year = student.getYear();
    String semester = student.getSemester();
    String yearSemester = null;
    // 如果不提供学期信息则返回当前学期的考勤信息
    if (year == null || semester == null) {
      // 之后该为动态获取当前的时间
      yearSemester = "20222";
    } else {
      yearSemester = year + semester;
    }

    // 设置表单数据
    Map<String, String> data = new HashMap<>();
    data.put("studentID", studentId);
    data.put("studentCode", studentCode);
    data.put("yearSemester", yearSemester);

    HashMap<String, Object> attendance = new HashMap<>();
    //        //获取的动态的SutdentId和gzcode
    //        Map<String, String> dynamicCode = getDynamicCode();

    // 创建 Jsoup 连接对象并设置相关参数
    //        String url = new String(ATTENDANCE_URL + "&studentID=" + dynamicCode.get("studentID")
    // + "&gzcode=" + dynamicCode.get("gzcode"));
    // 获取document
    Document doc = crawlerUtil.getDoc(ATTENDANCE_URL, data, REQUEST_POST);

    // 获取学年学期累计旷课学时数
    String text = doc.getElementById("form1").child(2).child(4).select("td").get(2).text();
    String[] split = text.split("：");
    String truancyCount = split[1];
    attendance.put("truancyCount", truancyCount);

    // 获取每个课程的考勤信息
    List<Element> elements = doc.getElementById("table1").select("tbody>tr");

    ArrayList<AttendanceVo> attendanceVos = new ArrayList<>();

    // 处理数据存入对象
    for (Element element : elements) {
      Elements children = element.children();

      AttendanceVo attendanceVo = new AttendanceVo();
      attendanceVo.setCourseCode(children.get(0).text());
      attendanceVo.setCourseName(children.get(1).text());
      attendanceVo.setAttendanceStats(children.get(2).text());

      attendanceVos.add(attendanceVo);
    }

    attendance.put("attendance", attendanceVos);

    return attendance;
  }

  /**
   * 获取平时成绩
   *
   * @param student
   * @return
   */
  @Override
  public List<RegularGradeVo> getRegularGrade(StudentDTO student) throws IOException {
    login(student);
    // 获取document
    Document doc = crawlerUtil.getDoc(REGULAR_GRADE_URL + "/index.jsp");

    List<Element> elements = doc.select(".tableBodyleft");

    List<RegularGradeVo> regularGradeVoList = new ArrayList<>();

    // 处理数据存入对象
    doc.select(".tableBodyleft").parallelStream()
            .forEach(
                    element -> {
                      RegularGradeVo regularGradeVo = new RegularGradeVo();

                      String[] courseInfo = element.text().split(" ");
                      regularGradeVo.setCourseCode(courseInfo[0]);
                      regularGradeVo.setCourseName(courseInfo[1]);
                      // 获取地址
                      String href = element.selectFirst("a").attr("href");
                      // 获取成绩细节
                      List<RegularGradeDetailVo> detail = null;
                      try {
                        detail = getRegularGradeDetail(href);
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                      regularGradeVo.setRegularGradeDetail(detail);
                      //            regularGradeVo.setHref(href);

                      regularGradeVoList.add(regularGradeVo);
                    });
    //        for(Element element:elements)
    //        {
    //            RegularGradeVo regularGradeVo = new RegularGradeVo();
    //
    //            String[] courseInfo = element.text().split(" ");
    //            regularGradeVo.setCourseCode(courseInfo[0]);
    //            regularGradeVo.setCourseName(courseInfo[1]);
    //            //获取地址
    //            String href = element.selectFirst("a").attr("href");
    //            //获取成绩细节
    //            List<RegularGradeDetailVo> detail = getRegularGradeDetail(href);
    //            regularGradeVo.setRegularGradeDetail(detail);
    ////            regularGradeVo.setHref(href);
    //
    //            regularGradeVoList.add(regularGradeVo);
    //        }

    //        log.info(elements.toString());
    return regularGradeVoList;
  }

  /**
   * 获取平时成绩细节
   *
   * @param href
   * @return
   */
  @Override
  public List<RegularGradeDetailVo> getRegularGradeDetail(String href) throws IOException {
    // 获取document
    Document doc = crawlerUtil.getDoc(REGULAR_GRADE_URL + "/" + href);

    List<RegularGradeDetailVo> details = new ArrayList<>();

    List<Element> trs = doc.select(".table1 tr");
    for (Element element : trs) {
      RegularGradeDetailVo detail = new RegularGradeDetailVo();

      Elements tds = null;
      // 如果课程没有平时成绩返回空
      try {
        tds = element.select("td");
        String[] values = tds.stream().map(Element::text).toArray(String[]::new);
        detail.setSource(values[0]);
        detail.setRatio(values[1]);
        detail.setMaxGrade(values[2]);
        detail.setRealGrade(values[3]);
      } catch (Exception e) {
        detail = null;
        e.printStackTrace();
      }
      details.add(detail);
    }

    return details;
  }

  /**
   * @return
   * @throws IOException
   */
  @Override
  public Map<String, Object> getCourse() throws IOException {
    // 发送请求并获取响应结果
    Document doc = crawlerUtil.getDoc(INIT_URL + COURES_URL);

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

  // 获取学生信息界面
  @Deprecated
  private Document getStudentInfoView() throws IOException {
    // 发送请求并获取响应结果
    Document doc = crawlerUtil.getDoc(MAIN_URL);

    // 解析 DOM 并收集课表数据
    Element table1 = doc.selectFirst("table.table1");
    Elements tds = table1.getElementsByAttribute("onclick");
    String s = tds.first().attr("onclick");
    String[] split = s.split("id=");
    String[] split1 = split[1].split("'");

    return crawlerUtil.getDoc(INFO_URL + split1[0]);
    //        return Jsoup.connect(INFO_URL + split1[0])
    //                .cookies(cookiesManager.getCookies())
    //                .timeout(10000)
    //                .method(Connection.Method.GET)
    //                .execute()
    //                .parse();
  }

  /**
   * 获取动态代码StudentId和gzCode 代替了getStudentInfoView方法，获取参数更多，
   *
   * @return
   */
  private Map<String, String> getDynamicCode() throws IOException {
    // 发送请求并获取响应结果
    Document doc = crawlerUtil.getDoc(MAIN_URL);

    Element table1 = doc.selectFirst("table.table1");
    List<Element> tds = table1.getElementsByAttribute("onclick");
    String s = tds.get(3).attr("onclick");

    String[] split = s.split("ID=");
    String[] split1 = split[1].split("&gzcode=");
    String[] split2 = split1[1].split("'");

    Map<String, String> map = new HashMap<>();
    map.put("studentID", split1[0]);
    map.put("gzcode", split2[0]);

    return map;
  }

  /**
   * 获取学生id
   *
   * @return
   * @throws IOException
   */
  private String getStudentId() throws IOException {
    // 发送请求并获取响应结果
    Document doc = crawlerUtil.getDoc(MAIN_URL);

    Element table1 = doc.selectFirst("table.table1");
    List<Element> tds = table1.getElementsByAttribute("onclick");
    String s = tds.get(2).attr("onclick");
    String[] split = s.split("id=");
    String[] split1 = split[1].split("'");

    return split1[0];
  }

  private ArrayList<ArrayList<ArrayList<String>>> getObligatoryCourse(Document doc) {

    Element tbody1 = doc.selectFirst("table.table tbody");
    Elements trs = tbody1.getElementsByTag("tr");
    ArrayList<ArrayList<String>> objects = null;
    ArrayList<ArrayList<ArrayList<String>>> obligatoryCourse = new ArrayList<>();
    for (Element tr : trs) {
      Elements children = tr.children();
      if (StringUtils.hasText(children.get(0).text())) {
        if (objects != null) {
          obligatoryCourse.add(objects);
        }
        objects = new ArrayList<>();
      }
      Elements tdList = tr.select("tr > td:not(:nth-child(6), :nth-child(9), :nth-child(10))");
      ArrayList<String> strings = new ArrayList<>();
      for (Element td : tdList) {
        String text = td.text();
        strings.add(text);
      }
      objects.add(strings);
    }
    return obligatoryCourse;
  }

  private ArrayList<ArrayList<String>> getElectiveCourse(Document doc) {

    Elements tables = doc.getElementsByClass("table");
    Element tbody = tables.last().selectFirst("tbody");
    Elements trs = tbody.getElementsByTag("tr");
    ArrayList<ArrayList<String>> electiveCourse = new ArrayList<>();
    for (Element tr : trs) {
      Elements tdList =
              tr.select("tr > td:not(:nth-child(5), :nth-child(7), :nth-child(8), :nth-child(9))");
      ArrayList<String> strings = new ArrayList<>();
      for (Element td : tdList) {
        String text = td.text();
        strings.add(text);
      }
      electiveCourse.add(strings);
    }
    return electiveCourse;
  }

  private ArrayList<String> getCreditSituation(Document doc) {
    Element tbody = doc.getElementsByTag("table").last().selectFirst("tbody");
    Elements trs =
            tbody.select("tbody > tr:not(:nth-child(1), :nth-child(2), :nth-child(9), :nth-child(10))");
    ArrayList<String> creditSituation = new ArrayList<>();
    for (Element tr : trs) {
      String element = tr.lastElementChild().text();
      creditSituation.add(element);
    }
    return creditSituation;
  }
}
