package ro.ubbcluj.cs.ams.utils.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import javax.jms.Queue;
import java.util.Arrays;
import java.util.List;

@Configuration
public class BeansConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder WebClientBuilderBean() {

        return WebClient.builder();
    }

    @Bean
    public Queue notificationQueue() {

        return new ActiveMQQueue("notification-queue");
    }

    @Bean
    public Queue participQueue() {

        return new ActiveMQQueue("particip-queue");
    }

    @Bean
    public Queue adminQueue() {

        return new ActiveMQQueue("admin-queue");
    }

    @Bean
    public static Queue authHQueue() {

        return new ActiveMQQueue("auth-hqueue");
    }

    @Bean
    public static Queue assignmentHQueue() {

        return new ActiveMQQueue("assignment-hqueue");
    }

    @Bean
    public static Queue attendanceHQueue() {

        return new ActiveMQQueue("attendance-hqueue");
    }

    @Bean
    public static Queue courseHQueue() {

        return new ActiveMQQueue("course-hqueue");
    }

    @Bean
    public static Queue gatewayHQueue() {

        return new ActiveMQQueue("gateway-hqueue");
    }

    @Bean
    public static Queue notificationHQueue() {

        return new ActiveMQQueue("notification-hqueue");
    }

    @Bean
    public static Queue studentHQueue() {

        return new ActiveMQQueue("student-hqueue");
    }

    @Bean
    public static Queue assignmentRQueue() {

        return new ActiveMQQueue("assignment-rqueue");
    }

    @Bean
    public static Queue attendanceRQueue() {

        return new ActiveMQQueue("attendance-rqueue");
    }

    @Bean
    public static Queue courseRQueue() {

        return new ActiveMQQueue("course-rqueue");
    }

    @Bean
    public static Queue gatewayRQueue() {

        return new ActiveMQQueue("gateway-rqueue");
    }

    @Bean
    public static Queue notificationRQueue() {

        return new ActiveMQQueue("notification-hqueue");
    }

    @Bean
    public static Queue studentRQueue() {

        return new ActiveMQQueue("student-rqueue");
    }


//    private static List<Queue> QUEUES = Arrays.asList(
//            assignmentHQueue(),
////            attendanceHQueue(),
//            authHQueue(),
//            courseHQueue(),
//            gatewayHQueue(),
//            notificationHQueue(),
//            studentHQueue()
//    );
//
//    public static List<Queue> queues() {
//
//        return QUEUES;
//    }

//    public List<Queue> queues() {
//
//        return Arrays.asList(
//                assignment_Queue(),
//                attendance_Queue()
//        );
//    }
}
