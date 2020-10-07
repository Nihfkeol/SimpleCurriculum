package com.example.simplecurriculum;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecurriculum.pojo.Course;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.SneakyThrows;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ItemViewHolder> {
    private List<Course> courses;
    private List<Course.CourseInfo> courseInfoList;
    private int LayoutWidth;
    private Context context;

    public CourseAdapter(Context context, List<Course> courses, int width) {
        this.courses = courses;
        this.LayoutWidth = width;
        this.context = context;
    }

    @NonNull
    @Override
    public CourseAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cell_course, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        itemViewHolder.courseInfoTV.setWidth(LayoutWidth);
        return itemViewHolder;
    }

    /**
     * 总共有courses.size() + courses.size()*courses.get(0).getCourseList().size()个item，
     * 而courseInfoList小于总数，所以每七个差值就会+1，
     * 所以用整形difference记录差值
     */
    private int difference;

    @SuppressWarnings("ConstantConditions")
    @SneakyThrows
    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.ItemViewHolder holder, int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        int countCourse = position % 7;
        if (countCourse == 0) {
            difference = position / 7 + 1;
            Course course = courses.get(position / 7);
            courseInfoList = course.getCourseList();
            if (position == 0) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                Date nowDate = new Date();
                nowDate = format.parse(format.format(nowDate));
                Date firstDate = format.parse("2020-9-12");
                long nd = 1000 * 24 * 60 * 60;
                long diff = nowDate.getTime() - firstDate.getTime();
                long countDay = (diff/nd)/6;
                String text = "第" + countDay + "周";
                holder.courseInfoTV.setText(text);
                alertDialogBuilder.setMessage(text);
            }else {
                String week = course.getWeek();
                holder.courseInfoTV.setText(week);
                alertDialogBuilder.setMessage(week);
            }
        } else {
            Course.CourseInfo courseInfo = courseInfoList.get(position % 7 - 1);
            if (difference == 1){
                String classTime = "<big><big>"+courseInfo.getClassTime()+"</big></big>";
                if (position == 1){
                    classTime += "\n8:30 - 9:10\n9:15 - 9:55";
                }else if (position == 2){
                    classTime += "\n10:10 - 10:50\n10:55 - 11:35";
                }else if (position == 3){
                    classTime += "\n11:40 - 12:20";
                }else if (position == 4){
                    classTime += "\n14:20 - 15:00\n15:05 - 15:45";
                }else if (position == 5){
                    classTime += "\n16:00 - 16:40\n16:45 - 17:25";
                }else if (position == 6){
                    classTime += "\n19:10 - 19:50\n20:00 - 20:40\n20:50 - 21:30";
                }
                holder.courseInfoTV.setTextSize(10);
                holder.courseInfoTV.setText(Html.fromHtml(classTime,Html.FROM_HTML_MODE_COMPACT));
                alertDialogBuilder.setMessage(classTime);
            }else {
                String courseInfoString = courseInfo.getCourseInfoString();
                holder.courseInfoTV.setText(courseInfoString);
                String[] split = courseInfoString.split("----------------------");
                alertDialogBuilder.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,split),null);
            }

        }
        holder.cardView.setOnClickListener(v -> {
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        //courses.size() -> 5       courseList -> 6
        return courses.size() + courses.size() * courses.get(0).getCourseList().size();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView courseInfoTV;
        private CardView cardView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            courseInfoTV = itemView.findViewById(R.id.courseInfoTextView);
            cardView = itemView.findViewById(R.id.LLayout);
        }
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) / 7 == 0) {
                outRect.left = 0;
            } else {
                outRect.left = space;
            }

            int spaceHalf = space / 2;
            if (parent.getChildAdapterPosition(view) % 7 == 0) {
                outRect.top = 0;
            }else {
                outRect.top = spaceHalf;
            }
        }
    }
}
