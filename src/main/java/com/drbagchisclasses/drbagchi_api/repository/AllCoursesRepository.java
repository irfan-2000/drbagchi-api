package com.drbagchisclasses.drbagchi_api.repository;

import com.drbagchisclasses.drbagchi_api.dto.*;
import com.drbagchisclasses.drbagchi_api.util.DbHelper;
import com.drbagchisclasses.drbagchi_api.util.SPHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class AllCoursesRepository
{
    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private SimpleJdbcCall simpleJdbcCall;

    @Value("${GlobalFetchPath}")
    public  String GlobalFetchPath;

    @Autowired
    private SPHelper spHelper;

    public List<AllCourseDetails> GetAllCourses()
    {
        List<AllCourseDetails> AllCourses = new ArrayList<AllCourseDetails>();

        String sql = "EXEC sp_Manage_CourseDetails @From=:From,@Flag=:Flag";

        MapSqlParameterSource params = new MapSqlParameterSource();
        DbHelper.addParameter(params, "From", "U");
        DbHelper.addParameter(params, "Flag", "GA");

        List<Map<String,Object>> rows = namedJdbcTemplate.queryForList(sql,params);
        for (Map<String, Object> row : rows) {
            AllCourseDetails data = new AllCourseDetails();
            data.OldPrice = (row.get("OldPrice") != null ? row.get("OldPrice").toString():"");
            data.DetailId = (row.get("DetailId") != null ? row.get("DetailId").toString() : "");
            data.CourseId = (row.get("CourseId") != null ? row.get("CourseId").toString() : "");
            data.CourseName = (row.get("CourseName") != null ? row.get("CourseName").toString() : "");
            data.Price = (row.get("Price") != null ? row.get("Price").toString() : "");
            data.Status = (row.get("Status") != null ? row.get("Status").toString() : "");
            data.Description = (row.get("Description") != null ? row.get("Description").toString() : "");
            data.Objectives = (row.get("Objectives") != null ? row.get("Objectives").toString() : "");
            data.Requirements = (row.get("Requirements") != null ? row.get("Requirements").toString() : "");
            data.CourseLevel = (row.get("CourseLevel") != null ? row.get("CourseLevel").toString() : "");
            data.CreatedAt = (row.get("CreatedAt") != null ? row.get("CreatedAt").toString() : "");
            data.UpdatedAt = (row.get("UpdatedAt") != null ? row.get("UpdatedAt").toString() : "");
            data.CourseImage = (row.get("CourseImage") != null ? GlobalFetchPath+"CourseImages/"+ row.get("CourseImage").toString() : "");

            AllCourses.add(data);
        }

        return AllCourses;

    }


    public CourseById CourseById(int CourseId)
    {

        CourseById Course = new CourseById();
        String sql = "EXEC sp_Manage_CourseDetails @From=:From,@Flag=:Flag,@CourseId=:CourseId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        DbHelper.addParameter(params, "From", "U");
        DbHelper.addParameter(params, "Flag", "GID");
        DbHelper.addParameter(params,"CourseId",CourseId);

        List<Map<String,Object>> rows = namedJdbcTemplate.queryForList(sql,params);

        for (Map<String, Object> row : rows)
        {
            Course.OldPrice = (row.get("OldPrice") != null)? new BigDecimal(row.get("OldPrice").toString()): BigDecimal.ZERO;
            Course.DetailId = (row.get("DetailId") != null)? Integer.parseInt(row.get("DetailId").toString())   : 0;
            Course.CourseId = (row.get("CourseId") != null)? Integer.parseInt(row.get("CourseId").toString())     : 0;
            Course.CourseName = (row.get("CourseName") != null) ? row.get("CourseName").toString()  : "";
            Course.Price = (row.get("Price") != null) ? new BigDecimal(row.get("Price").toString()) : BigDecimal.ZERO;
            Course.Status = (row.get("Status") != null) ? Integer.parseInt(row.get("Status").toString()): 0;
            Course.Description = (row.get("Description") != null) ? row.get("Description").toString() : "";
            Course.Objectives = (row.get("Objectives") != null) ? row.get("Objectives").toString() : "";
            Course.Requirements = (row.get("Requirements") != null) ? row.get("Requirements").toString() : "";
            Course.CourseLevel = (row.get("CourseLevel") != null) ? row.get("CourseLevel").toString() : "";
            Course.CreatedAt = (row.get("CreatedAt") != null) ? row.get("CreatedAt").toString() : "";
            Course.UpdatedAt = (row.get("UpdatedAt") != null) ? row.get("UpdatedAt").toString() : "";
            Course.CourseImage = (row.get("CourseImage") != null) ?GlobalFetchPath+"CourseImages/" + row.get("CourseImage").toString() : "";

        }

return Course;
        }




    public PricingDto GetPricing(String Email,int UserId ,int courseId,String CouponCode)
    {
        PricingDto price = new PricingDto();

        String sql = "EXEC sp_Manage_Discounts @CourseId=:CourseId,@StudentId=:StudentId,@Email=:Email";
        MapSqlParameterSource params = new MapSqlParameterSource();
        DbHelper.addParameter(params, "CourseId", courseId);
        DbHelper.addParameter(params, "StudentId", UserId);
        DbHelper.addParameter(params, "Email", Email);

            if(!CouponCode.isEmpty())
            {
                sql+= ",@CouponCode=:CouponCode";
                DbHelper.addParameter(params,"CouponCode",CouponCode);
            }

        List<Map<String,Object>> rows = namedJdbcTemplate.queryForList(sql,params);

        for (Map<String, Object> row : rows)
        {
            price.BasePrice = row.get("Price") != null ? new BigDecimal(row.get("Price").toString()) : BigDecimal.ZERO;
            price.discountId = row.get("DiscountId") != null ? row.get("DiscountId").toString() : null;
            price.discountName = row.get("DiscountName") != null ? row.get("DiscountName").toString() : null;
            price.type = row.get("Type") != null ? row.get("Type").toString() : null;
             price.appliedTo = row.get("AppliedTo") != null ? row.get("AppliedTo").toString() : null;
            price.studentDiscountId = row.get("StudentDiscountId") != null ? row.get("StudentDiscountId").toString() : null;
            price.studentId = row.get("StudentId") != null ? row.get("StudentId").toString() : null;
            price.appliedOn = row.get("AppliedOn") != null ? row.get("AppliedOn").toString() : null;
            price.email = row.get("Email") != null ? row.get("Email").toString() : null;
            price.appliesTo = row.get("AppliesTo") != null ? row.get("AppliesTo").toString() : null;
            price.courseId = row.get("CourseId") != null ? row.get("CourseId").toString() : null;
            price.value = row.get("Value") != null ? row.get("Value").toString() : null;
        }

    return price;


    }



    public PaymentMethodDto GetPaymentTypeandstatus(int CourseId)
    {
        PaymentMethodDto data = new PaymentMethodDto();

        Map<String, Object> params = new HashMap<>();
        params.put("CourseId", CourseId);

        Map<String, Object> result = spHelper.executeSP("sp_GetPaymentTypeFreshUsers", params);

        // ------- FIRST RESULT SET --------
        if (result.containsKey("#result-set-1"))
        {
            List<Map<String, Object>> table1 = (List<Map<String, Object>>) result.get("#result-set-1");

            if (!table1.isEmpty())
            {
                Map<String, Object> row = table1.get(0);

                data.CourseId = row.get("CourseId") != null ? row.get("CourseId").toString() : "";
                data.PaymentType = row.get("PaymentType") != null ? row.get("PaymentType").toString() : "";
                data.fixed_paymentMode = row.get("fixed_paymentMode") != null ? row.get("fixed_paymentMode").toString() : "";
            }
        }

        // ------- SECOND RESULT SET (only if subscription) --------
        if (data.PaymentType.equalsIgnoreCase("subscription") && result.containsKey("#result-set-2"))
        {
            List<Map<String, Object>> table2 = (List<Map<String, Object>>) result.get("#result-set-2");

            if (!table2.isEmpty())
            {
                Map<String, Object> row = table2.get(0);
                data.MonthlyAmount = row.get("MonthlyAmount") != null ?  (row.get("MonthlyAmount").toString()) : "0";
                data.QuarterlyAmount = row.get("QuarterlyAmount") != null ?  (row.get("QuarterlyAmount").toString()) : "0";
                data.HalfYearlyAmount = row.get("HalfYearlyAmount") != null ? (row.get("HalfYearlyAmount").toString()) : "0";
                data.YearlyAmount = row.get("YearlyAmount") != null ? (row.get("YearlyAmount").toString()) : "0";
            }
        }

        return data;
    }


    public boolean InsertSubscription(
            String courseId,
            String selectedPlan,
            String paymentType,
            String isWeb,         // IsPaid or IsWeb?
            String userId,
            String amount,
            String discountCode,
            String orderId,
            String paymentId,
            String batchId) {

        try {
            String sql = "EXEC sp_InsertEnrollment_subscription " +
                    "@CourseId=:CourseId, @StudentId=:StudentId, @BatchId=:BatchId, " +
                    "@PaymentType=:PaymentType, @IsWeb=:IsWeb, " +
                    "@AmountPaid=:AmountPaid, @DiscountAppliedId=:DiscountAppliedId, " +
                    "@fixed_paymentMode=:fixed_paymentMode, @OrderId=:OrderId, @PaymentId=:PaymentId";

            MapSqlParameterSource params = new MapSqlParameterSource();

            params.addValue("CourseId", parseIntSafe(courseId));
            params.addValue("StudentId", parseIntSafe(userId));
            params.addValue("BatchId", parseIntSafe(batchId));
            params.addValue("PaymentType", paymentType);
             params.addValue("IsWeb", parseIntSafe(isWeb));
            params.addValue("AmountPaid", parseDoubleSafe(amount));
            params.addValue("DiscountAppliedId", parseIntSafeOrNull(discountCode));
            params.addValue("fixed_paymentMode", null);  // passing null unless you have value
            params.addValue("OrderId", orderId);
            params.addValue("PaymentId", paymentId);

            int result = namedJdbcTemplate.update(sql, params);

            return result > 0;

        } catch (Exception ex) {
            System.err.println("InsertSubscription Error: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    // Helper functions
    private Integer parseIntSafe(String value) {
        return (value == null || value.trim().isEmpty()) ? 0 : Integer.parseInt(value.trim());
    }

    private Integer parseIntSafeOrNull(String value) {
        return (value == null || value.trim().isEmpty()) ? null : Integer.parseInt(value.trim());
    }

    private Double parseDoubleSafe(String value) {
        return (value == null || value.trim().isEmpty()) ? 0.0 : Double.parseDouble(value.trim());
    }

    private Boolean parseBooleanBit(String value) {
        return (value != null && value.equals("1")) || (value != null && value.equalsIgnoreCase("true"));
    }







}




