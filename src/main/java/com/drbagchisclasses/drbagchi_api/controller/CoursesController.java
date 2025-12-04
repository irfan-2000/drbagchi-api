package com.drbagchisclasses.drbagchi_api.controller;

import com.drbagchisclasses.drbagchi_api.config.JwtAuthenticationFilter;
import com.drbagchisclasses.drbagchi_api.dto.*;
import com.drbagchisclasses.drbagchi_api.repository.AllCoursesRepository;
import com.drbagchisclasses.drbagchi_api.repository.LoginSignUpRepository;
import com.drbagchisclasses.drbagchi_api.service.RazorpayService;
import com.drbagchisclasses.drbagchi_api.util.APIResponseHelper;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import io.jsonwebtoken.Jwt;
import jakarta.annotation.security.PermitAll;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CoursesController
{
    @Autowired
    private AllCoursesRepository allCourses;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Value("${razorpay.key_id}")
    private String razorpay_keyId;

    @Value("${razorpay.key_secret}")
    private String razorpay_keySecret;


    @Autowired
    private RazorpayService razorpayService;



    @GetMapping("GetAllCourses")
    @PreAuthorize("isAuthenticated()")
    public APIResponseHelper<List<AllCourseDetails>> GetAllCourses()
    {
        try {

            List<AllCourseDetails> result = allCourses.GetAllCourses();

            if (result != null && !result.isEmpty())
            {
                return new APIResponseHelper<>(200, "Success", result);
            } else {
                return new APIResponseHelper<>(204, "No subjects found", null);
            }
        } catch (Exception ex)
        {
            String errorMessage = (ex.getMessage() != null) ? ex.getMessage() : "Unknown error";

            return new APIResponseHelper<>(500, "Internal Server Error" + ex.getMessage(), null);
        }

    }


    @GetMapping("GetCourseById")
    @PreAuthorize("isAuthenticated()")
    public APIResponseHelper<CourseById> GetCourseById(@RequestParam  String CourseId)
    {
        int Id = Integer.parseInt(CourseId);
         try {

            CourseById result = allCourses.CourseById(Id);

            if (result != null )
            {
                return new APIResponseHelper<>(200, "Success", result);
            } else {
                return new APIResponseHelper<>(204, "No subjects found", null);
            }
        } catch (Exception ex)
        {
            String errorMessage = (ex.getMessage() != null) ? ex.getMessage() : "Unknown error";

            return new APIResponseHelper<>(500, "Internal Server Error" + ex.getMessage(), null);
        }
    }


    @GetMapping("GetPaymentMethod")
    @PreAuthorize("isAuthenticated()")
    public APIResponseHelper<PaymentMethodDto>GetPaymentMethod(@RequestParam  String CourseId)
    {
        int Id = Integer.parseInt(CourseId);

        try
        {
            var result = allCourses.GetPaymentTypeandstatus(Id);

            if (result != null )
            {
                return new APIResponseHelper<>(200, "Success", result);
            } else {
                return new APIResponseHelper<>(204, "No subjects found", null);
            }
        }catch (Exception ex)
        {
            String errorMessage = (ex.getMessage() != null) ? ex.getMessage():"unknownerror occured";
            return new APIResponseHelper<>( 500,"Internal server Error"+ex.getMessage(),null);
        }
    }

    @PostMapping("Createorder_razorpay_NewOrder_subscription")
    @PreAuthorize("isAuthenticated()")
    public APIResponseHelper<Object> Createorder_razorpay_NewOrder_subscription(String paymentType,
                                                                                String courseId, String selectedPlan,
                                                                                String Isweb,String batchId,String DiscountCode)
    {

        String userId = jwtAuthenticationFilter.UserId;

        try {
            var result = allCourses.GetPaymentTypeandstatus(Integer.parseInt(courseId));
            String amountStr = "";
            var id  =generateRandomOrderId().substring(0, 10);
            String receiptId = "subscription_" + id ;

            // Validate subscription and plan
            if (result.PaymentType.equalsIgnoreCase("subscription"))
            {

                if (selectedPlan == null || selectedPlan.isEmpty()) {
                    return new APIResponseHelper<>(204, "No plan selected", null);
                }

                switch (selectedPlan.toLowerCase()) {
                    case "monthly":
                        amountStr = result.MonthlyAmount;
                        break;
                    case "quarterly":
                        amountStr = result.QuarterlyAmount;
                        break;
                    case "halfyearly":
                        amountStr = result.HalfYearlyAmount;
                        break;
                    case "yearly":
                        amountStr = result.YearlyAmount;
                        break;
                    default:
                        return new APIResponseHelper<>(204, "Invalid plan selected", null);
                }

            } else
            {
                return new APIResponseHelper<>(204, "This course is not a subscription plan", null);
            }

            // Convert amount to paise
            BigDecimal amountBD = new BigDecimal(amountStr);
            BigDecimal amountInPaise = amountBD.multiply(new BigDecimal(100));

            // Razorpay client
            RazorpayClient razorpay = new RazorpayClient(razorpay_keyId, razorpay_keySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise.intValue());  // amount in paise (int)
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receiptId);
            //orderRequest.put("payment_capture", 1); // auto capture

            // ---- Add Metadata (Notes) ----
            JSONObject notes = new JSONObject();
            notes.put("courseId", courseId);
            notes.put("selectedPlan", selectedPlan);
            notes.put("paymentType", paymentType);
            notes.put("Isweb", Isweb);
            notes.put("userId",userId);
            notes.put("batchid",batchId);
            notes.put("DiscountCode", DiscountCode != null ? DiscountCode : "");

            orderRequest.put("notes", notes);


             Order order = razorpay.orders.create(orderRequest);

            // Prepare success response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("orderId", order.get("id"));
            responseData.put("amount", amountBD.toString());
            responseData.put("currency", "INR");
            responseData.put("receipt", receiptId);

            return new APIResponseHelper<>(200, "Order created successfully", responseData);

        } catch (RazorpayException ex) {
            ex.printStackTrace();
            return new APIResponseHelper<>(500, "Razorpay order creation failed: " + ex.getMessage(), null);

        } catch (Exception ex) {
            ex.printStackTrace();
            return new APIResponseHelper<>(500, "Unexpected error: " + ex.getMessage(), null);
        }
    }

    public static String generateRandomOrderId() {
        return UUID.randomUUID().toString();
    }







    @PostMapping("/verifyPayment")
   // @PreAuthorize("isAuthenticated()")
    public APIResponseHelper<?> verifyPayment(@RequestBody RazorpayPaymentDTO paymentDTO) {
        try {
            boolean isValid = razorpayService.verifySignature(
                    paymentDTO.razorpay_order_id,
                    paymentDTO.razorpay_payment_id,
                    paymentDTO.razorpay_signature
            );

            if(isValid)
            {
                RazorpayClient razorpay = new RazorpayClient(razorpay_keyId, razorpay_keySecret);

                Payment payment = razorpay.payments.fetch( paymentDTO.razorpay_payment_id);
                JSONObject notes = payment.get("notes");
                String courseId = notes.getString("courseId");
                String selectedPlan = notes.getString("selectedPlan");
                String paymentType = notes.getString("paymentType");
                String Isweb = notes.getString("Isweb");
                String userId = notes.getString("userId");
                 String DiscountCode = notes.getString("DiscountCode");
                String batchid = notes.getString("batchid");

                //Add ActualAmount Column when discount is implemented
                var result = allCourses.InsertSubscription(courseId,selectedPlan,paymentType,Isweb,userId,
                        paymentDTO.amount,DiscountCode,paymentDTO.razorpay_order_id,paymentDTO.razorpay_payment_id ,batchid );


                return new APIResponseHelper<>(200, "Payment verified and saved", result

                );

            } else
            {

                return new APIResponseHelper<>(400, "Payment verification failed", false);

            }

        } catch (Exception e)
        {
            e.printStackTrace();

            return new APIResponseHelper<>(500, "message" + e.getMessage().toString(), false);

        }
    }

}
