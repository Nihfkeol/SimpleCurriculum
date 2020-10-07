package com.example.simplecurriculum.Utils;

import com.example.simplecurriculum.pojo.Course;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseUtils {

    private Elements trElements;
    private Document document;
    public ParseUtils(String html) {
        document = Jsoup.parse(html);
    }

    /**
     * 判断是否登录成功
     * @return true成功，则失败
     */
    public boolean parseIsLogin(){
        Elements font = document.select("font");
        if (!font.isEmpty()){
            String text = font.text();
            System.out.println(text);
            return !"请先登录系统".equals(text);
        }
        return true;
    }

    /**
     * 课程表版本，
     * 是否显示任课老师信息
     * @return 返回字符串
     */
    public String parseVersion(int i){
        trElements = document.select("tbody").last().getElementsByTag("tr");
        Elements input = trElements.select("input");
        return input.get(i).attr("name");
    }

    /**
     * 把网页解析，把课程信息存储到List
     * @param version
     * @return
     */
    public List<Course> parseCourse(String version){
        //先记录课程信息
        Map<Integer, Course.CourseInfo> courseInfoMap = new HashMap<>();
        //记录周几
        String[] weeks = null;
        //记录map的Integer的值
        int countMap = -1;
        //记录网页的列数，最后一列为空所以忽略
        int forIMax = trElements.size() - 1;
        for (int i = 0; i < forIMax; i++) {
            Element elementI = trElements.get(i);
            if (i == 0){
                //存放星期，并确定courseList的长度
                Elements thElements = elementI.getElementsByTag("th");
                weeks = new String[thElements.size()-2];
                for (int j = 0; j < weeks.length; j++) {
                    weeks[j] = thElements.get(j).text();
                }
            }else {
                for (int j = 0; j < weeks.length; j++) {
                    Course course = new Course();
                    Course.CourseInfo info = course.new CourseInfo();
                    //当j=0的时候显示的是上课时间
                    if (j == 0) {
                        Element e = elementI.getElementsByTag("th").first();
                        info.setClassTime(e.text());
                    } else {
                        Element e = elementI.getElementsByTag("td").get(j-1);
                        Elements input1 = e.getElementsByTag("input");
                        String courseVersion = null;
                        for (Element elementInputs : input1) {
                            if (elementInputs.attr("name").equals(version)) {
                                courseVersion = elementInputs.attr("value");
                                break;
                            }
                        }
                        Elements selectDIV = e.select("div#" + courseVersion);
                        info.setCourseInfoString(selectDIV.text());

                    }
                    countMap++;
                    courseInfoMap.put(countMap, info);
                }
            }
        }
        //用于储存周几的所有课程
        List<Course> courseList = new ArrayList<>();
        List<Course.CourseInfo> infoList;
        //noinspection ConstantConditions
        for (int i = 0; i < weeks.length; i++) {
            infoList = new ArrayList<>();
            for (Integer key : courseInfoMap.keySet()){
                if (key % 6 == i){
                    infoList.add(courseInfoMap.get(key));
                }
            }
            Course course = new Course();
            course.setWeek(weeks[i]);
            course.setCourseList(infoList);
            courseList.add(course);
        }

        return courseList;
    }


}
