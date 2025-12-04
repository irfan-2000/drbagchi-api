package com.drbagchisclasses.drbagchi_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


@Service
public class RazorpayService {


    @Value("${razorpay.key_id}")
    private   String razorpay_keyId;

    @Value("${razorpay.key_secret}")
    private   String razorpay_keySecret;

    private  static String razorpay_keySecret_static;


    private static final String RAZORPAY_SECRET = razorpay_keySecret_static; // your secret

    // Verify Razorpay Signature
    public boolean verifySignature(String orderId, String paymentId, String signature) throws Exception
    {razorpay_keySecret_static = razorpay_keySecret;
        String payload = orderId + "|" + paymentId;
        String generatedSignature = hmacSHA256(payload, razorpay_keySecret_static);
        return generatedSignature.equals(signature);
    }

    private String hmacSHA256(String data, String key) throws Exception
    {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes());

        // Convert bytes to hex manually
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Save payment to database (example)
    public void savePayment(int courseId, String plan, String amount, String orderId, String paymentId)
    {
        // TODO: insert into your payments table
        System.out.println("Saving payment: " + courseId + ", " + plan + ", " + amount + ", " + orderId + ", " + paymentId);
    }
}
