package com.drbagchisclasses.drbagchi_api.controller;

import com.drbagchisclasses.drbagchi_api.config.JwtAuthenticationFilter;
import com.drbagchisclasses.drbagchi_api.dto.CourseById;
import com.drbagchisclasses.drbagchi_api.dto.PricingDto;
import com.drbagchisclasses.drbagchi_api.repository.AllCoursesRepository;
import com.drbagchisclasses.drbagchi_api.repository.LoginSignUpRepository;
import com.drbagchisclasses.drbagchi_api.util.APIResponseHelper;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class PricingController
{
    @Autowired
    private AllCoursesRepository allCourses;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private  AllCoursesRepository allCoursesRepository;

    private final LoginSignUpController loginSignUpController;
    @Autowired
    public PricingController(LoginSignUpController loginSignUpController)
    {
        this.loginSignUpController = loginSignUpController;
    }

    @PostMapping("GetPricing")
    @PreAuthorize("isAuthenticated()")
    public APIResponseHelper<PricingDto> GetPricing(@RequestBody Object dto) {
        String email = jwtAuthenticationFilter.Email;
        String userId = jwtAuthenticationFilter.UserId;

        if (email == null || email.isEmpty()) {
            return new APIResponseHelper<>(200, "Email Id not found", null);
        }

        if (userId == null || userId.isEmpty()) {
            return new APIResponseHelper<>(200, "UserId not found", null);
        }

        try {
            Map<String, Object> map = (Map<String, Object>) dto;
            Integer courseId = Integer.parseInt(map.get("CourseId").toString());
            String couponCode = map.get("CouponCode") != null ? map.get("CouponCode").toString() : "";

            PricingDto result = allCoursesRepository.GetPricing(email, Integer.parseInt(userId), courseId, couponCode);

            // If no coupon code is provided
            if (couponCode.isEmpty())
            {
                if (result != null && result.BasePrice != null && result.BasePrice.compareTo(BigDecimal.ZERO) > 0
                        && (result.discountId == null || result.discountId.isEmpty()))
                {
                    result.Total =   result.BasePrice;
                    return new APIResponseHelper<>(200, "Success", result);
                } else {
                    return new APIResponseHelper<>(400, "Invalid or zero price", result);
                }
            }

            // If coupon code is provided
            if (!couponCode.isEmpty())
            {
                if (result != null && result.discountId != null && !result.discountId.isEmpty())
                {
                    BigDecimal finalPrice = result.BasePrice != null ? result.BasePrice : BigDecimal.ZERO;


                    if ("fixed".equalsIgnoreCase(result.type))
                    {
                        // Fixed discount (flat off)
                        BigDecimal discountValue = result.value != null ? new BigDecimal(result.value) : BigDecimal.ZERO;
                        finalPrice = finalPrice.subtract(discountValue);
                        result.discountValue = discountValue;
                    } else if ("percentage".equalsIgnoreCase(result.type))
                    {
                        // Percentage discount
                        BigDecimal discountPercent = result.value != null ? new BigDecimal(result.value) : BigDecimal.ZERO;
                        BigDecimal discountAmount = finalPrice.multiply(discountPercent).divide(new BigDecimal(100));
                        finalPrice = finalPrice.subtract(discountAmount);
                        result.discountValue = discountAmount; // store actual amount in ₹
                    }

                    if (finalPrice.compareTo(BigDecimal.ZERO) < 0)
                    {
                        finalPrice = BigDecimal.ZERO;
                    }
                    result.Total = finalPrice;

                    // Validate discount type
                    if ("fixed".equalsIgnoreCase(result.type) || "percentage".equalsIgnoreCase(result.type))
                    {
                        return new APIResponseHelper<>(200, "Success", result);
                    } else {
                        return new APIResponseHelper<>(400, "Invalid Coupon Code type", result);
                    }
                } else
                {
                    result.Total =   result.BasePrice;
                    return new APIResponseHelper<>(400, "Invalid Coupon Code", result);
                }
            }

            // Default fallback
            return new APIResponseHelper<>(400, "Invalid request", result);

        } catch (Exception ex) {
            String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Unknown error";
            return new APIResponseHelper<>(500, "Internal Server Error: " + errorMessage, null);
        }
    }

}
