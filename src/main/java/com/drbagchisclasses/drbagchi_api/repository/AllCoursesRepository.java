package com.drbagchisclasses.drbagchi_api.repository;

import com.drbagchisclasses.drbagchi_api.dto.AllCourseDetails;
import com.drbagchisclasses.drbagchi_api.dto.Classes;
import com.drbagchisclasses.drbagchi_api.dto.CourseById;
import com.drbagchisclasses.drbagchi_api.dto.PricingDto;
import com.drbagchisclasses.drbagchi_api.util.DbHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
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














}
