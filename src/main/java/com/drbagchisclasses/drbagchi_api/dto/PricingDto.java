package com.drbagchisclasses.drbagchi_api.dto;

import java.math.BigDecimal;

public class PricingDto
{
    public BigDecimal price;            // Price
    public String discountId;           // DiscountId
    public String discountName;         // DiscountName
    public String type;                 // Type
    public BigDecimal discountValue;        // DiscountValue
    public String appliedTo;            // AppliedTo
    public String studentDiscountId;    // StudentDiscountId
    public String studentId;            // StudentId
    public String appliedOn;            // AppliedOn
    public String email;                // Email
    public String appliesTo;            // AppliesTo
    public String courseId;             // CourseId
    public String value;                // Value

    // Optional: status field if needed
    private int status;


    public BigDecimal BasePrice  =  BigDecimal.ZERO;
    public BigDecimal Discount =  BigDecimal.ZERO;
    public BigDecimal Total =  BigDecimal.ZERO;
    public String CouponCode = "";
}
