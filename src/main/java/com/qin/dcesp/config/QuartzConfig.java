package com.qin.dcesp.config;


import com.qin.dcesp.jobs.ListeningSocketJob;
import com.qin.dcesp.jobs.SocketConnectionCheckJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail getListeningSocketJobDetail(){
        return JobBuilder.newJob(ListeningSocketJob.class).withIdentity("ListeningSocketJob").storeDurably().build();
    }

    @Bean
    public JobDetail getSocketConnectionCheckJobDeatail(){
        return JobBuilder.newJob(SocketConnectionCheckJob.class).withIdentity("SocketConnectionCheckJob").storeDurably().build();
    }

    @Bean
    public Trigger getListeningSocketJobTrigger(){
        SimpleScheduleBuilder scheduleBuilder =
                SimpleScheduleBuilder.simpleSchedule().
                withIntervalInSeconds(60).repeatForever();
        return TriggerBuilder.newTrigger().forJob(getListeningSocketJobDetail())
                .withIdentity("ListeningSocketJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Bean
    public Trigger getSocketConnectionCheckJob(){
        SimpleScheduleBuilder scheduleBuilder =
                SimpleScheduleBuilder.simpleSchedule().
                        withIntervalInSeconds(60).repeatForever();
        return TriggerBuilder.newTrigger().forJob(getSocketConnectionCheckJobDeatail())
                .withIdentity("SocketConnectionCheckJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
