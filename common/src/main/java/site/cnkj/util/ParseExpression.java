package cn.migu.log.utils;

/*
 * @version 1.0 created by LXW on 2018/11/8 11:14
 */
public class ParseExpression {

    /**
     * 解析计划任务表达式，返回每天执行的次数
     *
     * @param task 计划任务表达式
     * @return 执行次数
     */
    public static Integer getTimesByDay(String task) {
        String[] tasks = task.split(" ");
        String hours = tasks[2];
        String[] hourss = hours.split("/");
        int stime = Integer.valueOf(hourss[1]);
        int time = 24 / stime;
        return time;
    }

}
