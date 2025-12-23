package in.kishorbongu.billingsoftware.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;

import in.kishorbongu.billingsoftware.io.OrderResponse;
import in.kishorbongu.billingsoftware.io.PaymentRequest;
import in.kishorbongu.billingsoftware.io.PaymentVerificationRequest;
import in.kishorbongu.billingsoftware.io.RazorpayOrderResponse;
import in.kishorbongu.billingsoftware.service.OrderService;
import in.kishorbongu.billingsoftware.service.RazorpayService;
import lombok.RequiredArgsConstructor;




@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;
    private final OrderService orderService;


    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    public RazorpayOrderResponse createRazorpayOrder(@RequestBody PaymentRequest request) throws RazorpayException {
        return razorpayService.createOrder(request.getAmount(), request.getCurrency());
    }


     @PostMapping("/verify")
    public OrderResponse verifyPayment(@RequestBody PaymentVerificationRequest request) {
        return orderService.verifyPayment(request);

    }
}
