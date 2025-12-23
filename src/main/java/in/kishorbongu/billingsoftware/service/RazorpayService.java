package in.kishorbongu.billingsoftware.service;

import com.razorpay.RazorpayException;

import in.kishorbongu.billingsoftware.io.RazorpayOrderResponse;

public interface RazorpayService {

    RazorpayOrderResponse createOrder(Double amount,String currency) throws RazorpayException;
}
