//package pfe.example.enrollement_module.client;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
///**
// * Client Feign pour communiquer avec le microservice Notification
// */
//@FeignClient(
//        name = "notification-service",
//        url = "${notification.service.url:http://localhost:8084}",
//        fallback = NotificationClientFallback.class
//)
//public interface NotificationClient {
//
//    @PostMapping("/api/notifications/send")
//    void sendNotification(
//            @RequestParam("email") String email,
//            @RequestParam("subject") String subject,
//            @RequestParam("message") String message
//    );
//
//    @PostMapping("/api/notifications/send-sms")
//    void sendSMS(
//            @RequestParam("phone") String phone,
//            @RequestParam("message") String message
//    );
//}