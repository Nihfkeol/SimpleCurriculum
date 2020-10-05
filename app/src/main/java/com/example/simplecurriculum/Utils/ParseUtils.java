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

    public boolean parseIsLogin(){
        Elements font = document.select("font");
        if (!font.isEmpty()){
            String text = font.text();
            return "".equals(text);
        }
        return true;
    }

    public String parseVersion(){
        trElements = document.select("tbody").last().getElementsByTag("tr");
        Elements input = trElements.select("input");
        //TODO get(i)可以在方法的获取参数选择
        String COURSE_VERSION_ONE = input.get(0).attr("name");
        String COURSE_VERSION_TWO = input.get(1).attr("name");
        return COURSE_VERSION_ONE;
    }

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
