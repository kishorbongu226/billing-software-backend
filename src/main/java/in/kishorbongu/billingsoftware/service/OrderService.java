package in.kishorbongu.billingsoftware.service;

import java.time.LocalDate;
import java.util.List;

import in.kishorbongu.billingsoftware.io.OrderRequest;
import in.kishorbongu.billingsoftware.io.OrderResponse;
import in.kishorbongu.billingsoftware.io.PaymentVerificationRequest;

public interface OrderService {
    

    OrderResponse createOrder(OrderRequest request);

    void deleteOrder(String orderId);

    List<OrderResponse> getLatestOrders();

    OrderResponse verifyPayment(PaymentVerificationRequest request);

    Double sumSalesByDate(LocalDate date);

    Long countByOrderDate(LocalDate date);

    List<OrderResponse> findRecentOrders();




}
