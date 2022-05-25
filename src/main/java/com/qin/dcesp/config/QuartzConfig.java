package com.qin.dcesp.config;


import com.qin.dcesp.jobs.ListeningSocketJob;
import com.qin.dcesp.jobs.SocketConnectionCheckJob;
import com.qin.dcesp.jobs.TimeoutCountStutJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail getListeningSocketJobDetail(){
        return JobBuilder.newJob(ListeningSocketJob.class).withIdentity("ListeningSocketJob").storeDurably().build();
    }

    //@Bean
    //public JobDetail getSocketConnectionCheckJobDeatail(){
    //    return JobBuilder.newJob(SocketConnectionCheckJob.class).withIdentity("SocketConnectionCheckJob").storeDurably().build();
    //}

    //@Bean
    //public JobDetail getTimeoutCountStutJobDeatail(){
    //    return JobBuilder.newJob(TimeoutCountStutJob.class).withIdentity("TimeoutCountStutJob").storeDurably().build();
    //}

    @Bean
    public Trigger getListeningSocketJobTrigger(){
        SimpleScheduleBuilder scheduleBuilder =
                SimpleScheduleBuilder.simpleSchedule().
                withIntervalInSeconds(10).repeatForever();
        return TriggerBuilder.newTrigger().forJob(getListeningSocketJobDetail())
                .withIdentity("ListeningSocketJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }

    //@Bean
    //public Trigger getSocketConnectionCheckJob(){
    //    SimpleScheduleBuilder scheduleBuilder =
    //            SimpleScheduleBuilder.simpleSchedule().
    //                    withIntervalInSeconds(1).repeatForever();
    //    return TriggerBuilder.newTrigger().forJob(getSocketConnectionCheckJobDeatail())
    //            .withIdentity("SocketConnectionCheckJobTrigger")
    //            .withSchedule(scheduleBuilder)
    //            .build();
    //}

    //@Bean
    //public Trigger getTimeoutCountStutJob(){
    //    SimpleScheduleBuilder scheduleBuilder =
    //            SimpleScheduleBuilder.simpleSchedule()
    //                    .withIntervalInSeconds(1).repeatForever();
    //    return TriggerBuilder.newTrigger().forJob(getTimeoutCountStutJobDeatail())
    //            .withIdentity("TimeoutCountStutJobTrigger")
    //            .withSchedule(scheduleBuilder)
    //            .build();
    //}

}
