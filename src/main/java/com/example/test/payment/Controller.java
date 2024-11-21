package com.example.test.payment;


import com.example.test.user.User;
import com.example.test.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class Controller {
    @Autowired
    private VNPAYService vnPayService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    // Endpoint trả về chuỗi đơn giản
    @GetMapping({"", "/"})
    public String home() {
        return "Welcome to VNPay Payment Gateway";
    }

    // Xử lý gửi đơn hàng và trả về URL VNPay
//    @PostMapping("/submitOrder")
//    public String submitOrder(@RequestParam("amount") int orderTotal,
//                              @RequestParam("orderInfo") String orderInfo,
//                              HttpServletRequest request) {
//        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
//        String vnpayUrl = vnPayService.createOrder(request, orderTotal, orderInfo, baseUrl);
//        return vnpayUrl; // Trả về URL VNPay dưới dạng chuỗi
//    }

    @GetMapping("/submitOrder")
    public String submitOrder(@RequestParam("amount") int orderTotal,
                              @RequestParam("orderInfo") String orderInfo,
                              @RequestHeader("Authorization") String authorizationHeader,
                              HttpServletRequest request) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return "You must log in to proceed with the payment.";
        }

        // Lấy token từ header
        String token = authorizationHeader.substring(7); // Lấy phần sau "Bearer "

        // Giải mã token và lấy userId
        Long userId = jwtService.getUserIdFromToken(token);

        if (userId == null) {
            return "Invalid token or expired. Please log in again.";
        }

        // Tiến hành tạo đơn hàng và chuyển hướng đến VNPay
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(request, orderTotal, orderInfo, baseUrl, userId);

        return "Redirecting to: " + vnpayUrl;
    }



    // Xử lý kết quả thanh toán
    @GetMapping("/vnpay-payment-return")
    public String paymentCompleted(HttpServletRequest request) {
        int paymentStatus = vnPayService.orderReturn(request);

        // Lấy thông tin từ request
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");


        // Chuyển đổi dữ liệu thời gian thanh toán
        LocalDateTime payDate = LocalDateTime.parse(paymentTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userIdStr = request.getParameter("userId");
        Long userId = null;

        // Lấy thông tin người dùng từ UserRepository (giả sử đã có thông tin người dùng trong hệ thống)
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            // Lưu thông tin thanh toán vào CSDL
            Payment transaction = new Payment();
            transaction.setOrderInfo(orderInfo);
            transaction.setTransactionNo(transactionId);
            transaction.setAmount(Double.parseDouble(totalPrice) / 100); // VNPay gửi giá trị *100
            transaction.setPayDate(payDate);
            transaction.setStatus(paymentStatus == 1 ? "SUCCESS" : "FAILED");
            transaction.setUserId(user.getId()); // Lưu userId

            paymentRepository.save(transaction);
            // Tạo phản hồi dạng JSON
            return """
                    {
                        "orderId": "%s",
                        "totalPrice": "%s",
                        "paymentTime": "%s",
                        "transactionId": "%s",
                        "status": "%s"
                    }
                    """.formatted(orderInfo, totalPrice, paymentTime, transactionId,
                    paymentStatus == 1 ? "SUCCESS" : "FAILED");
        }else{
            return "không có user";
        }
    }
}