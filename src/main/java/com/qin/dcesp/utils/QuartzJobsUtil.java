package com.qin.dcesp.utils;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class QuartzJobsUtil {

    private static final Logger logger = LoggerFactory.getLogger(QuartzJobsUtil.class);

    //启动一个任务
    public static void startAJob(Scheduler scheduler,String jobName) throws SchedulerException{
        try {
            String className = "com.qin.dcesp.jobs.TimeoutCountStutJob";
            Class<? extends Job> jobBean = (Class<? extends Job>) Class.forName(className);
            JobDetail jobDetail = JobBuilder.newJob(jobBean).withIdentity(jobName).build();
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("*/1 * * * * ?");
            Trigger jobTrigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                    .withIdentity(jobName)
                    .withSchedule(scheduleBuilder)
                    .build();
            scheduler.scheduleJob(jobDetail,jobTrigger);
        }catch (ClassNotFoundException e) {
            logger.error("找不到类!" + e.getMessage());
        }

    }


    //暂停一个任务
    public static void pauseAJob(Scheduler scheduler,String jobName){
        JobKey jobKey = JobKey.jobKey(jobName);
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            logger.error("暂停任务出错!" + e.getMessage());
        }

    }

    //恢复一个任务
    public static void reRunAJob(Scheduler scheduler,String jobName){
        JobKey jobKey = JobKey.jobKey(jobName);
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            logger.error("恢复任务出错!" + e.getMessage());
        }
    }


    //删除一个任务
    public static void deleteAJob(Scheduler scheduler,String jobName) {
        JobKey jobKey = JobKey.jobKey(jobName);
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            logger.error("删除任务出错!" + e.getMessage());
        }
    }

}
