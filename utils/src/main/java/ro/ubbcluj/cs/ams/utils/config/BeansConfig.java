package ro.ubbcluj.cs.ams.utils.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.jms.Queue;

@Component
public class BeansConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder WebClientBuilderBean() {

        return WebClient.builder();
    }

    //    @Bean
//    public Queue assignmentQueue() {
//
//        return new ActiveMQQueue("assignment-queue");
//    }
//
//    @Bean
//    public Queue attendanceQueue() {
//
//        return new ActiveMQQueue("attendance-queue");
//    }
//
//    @Bean
//    public Queue courseQueue() {
//
//        return new ActiveMQQueue("ro.ubbcluj.cs.ams.course-queue");
//    }
//
//    @Bean
//    public Queue gatewayQueue() {
//
//        return new ActiveMQQueue("gateway-queue");
//    }
//
    @Bean
    public Queue notificationQueue() {

        return new ActiveMQQueue("notification-queue");
    }

//
//    @Bean
//    public Queue studentQueue() {
//
//        return new ActiveMQQueue("student-queue");
//    }
//
//    @Bean
//    public List<Queue> queues(){
//
//        return Arrays.asList(
//                assignmentQueue(),
//                attendanceQueue(),
//                courseQueue(),
//                gatewayQueue(),
//                notificationQueue(),
//                studentQueue());
//    }
}
