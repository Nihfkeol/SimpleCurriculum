package com.example.simplecurriculum.pojo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Course {
    //第一行，星期几的课
    @Getter
    @Setter
    private String Week;
    //一天中所有的课程
    @Getter
    @Setter
    private List<CourseInfo> courseList;

    /**
     * 先把信息列出来之后再分类
     */
    @SuppressWarnings("InnerClassMayBeStatic")
    @ToString
    public class CourseInfo {
        //课程时间,获取的数据第一列是课程时间表
        @Getter
        @Setter
        private String ClassTime;
        //课程名称、课程老师、课程周次和课程教室的字符串
        @Getter
        @Setter
        private String CourseInfoString;
    }
}
