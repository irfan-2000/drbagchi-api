package com.drbagchisclasses.drbagchi_api.repository;

import com.drbagchisclasses.drbagchi_api.dto.CoursesEnrolled;
import com.drbagchisclasses.drbagchi_api.dto.LoginResponseDto;
import com.drbagchisclasses.drbagchi_api.dto.SignupDto;
import com.drbagchisclasses.drbagchi_api.dto.SubjectDto;
import com.drbagchisclasses.drbagchi_api.util.DbHelper;
import com.drbagchisclasses.drbagchi_api.util.SPHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 @Repository

public class LoginSignUpRepository
{

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private SimpleJdbcCall simpleJdbcCall;


    public int SignUp(SignupDto dto)
    {

        try {
            String sql = "EXEC Sp_ManageStudentsDetails @Flag=:Flag, " +
                    "@FullName=:FullName, @DateOfBirth=:DateOfBirth, @Gender=:Gender, " +
                    "@Email=:Email, @Phone=:Phone, @ParentName=:ParentName, " +
                    "@ParentMobile=:ParentMobile, @Address=:Address, @City=:City, " +
                    "@State=:State, @Pincode=:Pincode, @InstitutionId=:InstitutionId, " +
                    "@InstitutionName=:InstitutionName, @BoardId=:BoardId, " +
                    "@ClassId=:ClassId, @SubjectId=:SubjectId, @BatchId=:BatchId, " +
                    "@Status=:Status, @Password=:Password,@ImageName=:ImageName,@StudentCast=:StudentCast";

            MapSqlParameterSource params = new MapSqlParameterSource();

            if(Boolean.parseBoolean(dto.IsEditing))
            {
                DbHelper.addParameter(params, "Flag", "U");
                DbHelper.addParameter(params, "StudentId", Integer.parseInt(dto.StudentId));
                sql +=",@StudentId=:StudentId";


            }else {

                DbHelper.addParameter(params, "Flag", "I");
            }

             // Insert operation

            DbHelper.addParameter(params, "FullName", dto.fullName);
            DbHelper.addParameter(params, "DateOfBirth", dto.dateOfBirth != null ? dto.dateOfBirth : null);
            DbHelper.addParameter(params, "Gender", dto.gender);
            DbHelper.addParameter(params, "Email", dto.email);
            DbHelper.addParameter(params, "Phone", dto.phone);
            DbHelper.addParameter(params, "ParentName", dto.parentName);
            DbHelper.addParameter(params, "ParentMobile", dto.parentMobile);
            DbHelper.addParameter(params, "Address", dto.address);
            DbHelper.addParameter(params, "City", dto.city);
            DbHelper.addParameter(params, "State", dto.state);
            DbHelper.addParameter(params, "Pincode", dto.pincode);

            // Convert numeric types
            DbHelper.addParameter(params, "InstitutionId", dto.institutionId != null ? Integer.parseInt(dto.institutionId) : 0);
            DbHelper.addParameter(params, "InstitutionName", dto.institutionName);
            DbHelper.addParameter(params, "BoardId", dto.boardId != null ? Integer.parseInt(dto.boardId) : null);
            DbHelper.addParameter(params, "ClassId", dto.classId != null ? Integer.parseInt(dto.classId) : null);
            DbHelper.addParameter(params, "SubjectId", dto.SubjectId != null ? Integer.parseInt(dto.SubjectId) : null);
            DbHelper.addParameter(params,"BatchId", 0   );

            DbHelper.addParameter(params, "Status", dto.status != null ? Integer.parseInt(dto.status) : 1);
            DbHelper.addParameter(params, "Password", dto.password);
             DbHelper.addParameter(params, "ImageName", dto.imageName);
             DbHelper.addParameter(params, "StudentCast", dto.Caste);


            int result = namedJdbcTemplate.update(sql, params);

            return result ;
        } catch (Exception ex)
        {
                throw ex;
        }


    }


    @Autowired
    private SPHelper spHelper;

    public LoginResponseDto validateUser(String email, String password)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("UserId", email);
        params.put("Password", password);

        // call SP
        Map<String, Object> result = spHelper.executeSP("Validate_Student", params);

        LoginResponseDto dto = new LoginResponseDto();
        dto.enrolledCourses = new ArrayList<>();

        // First table (#result-set-1)
        if (result.containsKey("#result-set-1"))
        {
            List<Map<String, Object>> table1 = (List<Map<String, Object>>) result.get("#result-set-1");
            if (!table1.isEmpty())
            {
                Map<String, Object> studentRow = table1.get(0);
                dto.UserId   = studentRow.get("StudentId") != null ? studentRow.get("StudentId").toString() : "";
                dto.FullName = studentRow.get("FullName")  != null ? studentRow.get("FullName").toString()  : "";
                dto.Email    = studentRow.get("Email")     != null ? studentRow.get("Email").toString()     : "";
            }
        }

        // Second table (#result-set-2)
        if (result.containsKey("#result-set-2"))
        {
            List<Map<String, Object>> table2 = (List<Map<String, Object>>) result.get("#result-set-2");

            if (!table2.isEmpty())
            {
                Map<String, Object> courses = table2.get(0);

                for (Map<String, Object> row : table2)
                {
                    CoursesEnrolled course = new CoursesEnrolled();
                    course.Enrollmentid = row.get("EnrollmentId") != null ? row.get("EnrollmentId").toString() : "";
                    course.CourseId = row.get("CourseId") != null ? row.get("CourseId").toString() : "";
                    course.IsPaid = row.get("IsPaid") != null ? Integer.parseInt(row.get("IsPaid").toString()) : 0;

                    dto.enrolledCourses.add(course);
                }
                // = extraRow.get("Role") != null ? extraRow.get("Role").toString() : "";
                // map more columns as needed
            }
        }

        return dto;
    }


    public Integer CheckIsExistdata(String flag, String data)
    {
        try
        {
            String sql = "EXEC CheckDataExist @Flag=:Flag";

            MapSqlParameterSource params = new MapSqlParameterSource();
            DbHelper.addParameter(params, "Flag", flag);

            if ("EMAIL".equalsIgnoreCase(flag))
            {
                sql += ", @Email=:Email";
                DbHelper.addParameter(params, "Email", data);
            }
            else if ("MOBILE".equalsIgnoreCase(flag))
            {
                sql += ", @Mobile=:Mobile";
                DbHelper.addParameter(params, "Mobile", data);
            }

            Integer isExist = namedJdbcTemplate.queryForObject(
                    sql,
                    params,
                    Integer.class
            );
             return isExist;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }


    public Map<String, Object> verifyOTP(String mobile, String otp)
    {
        String sql = "EXEC sp_manageMobileOTP @Flag=:Flag, " +
                "@Mobile=:Mobile, @OTP=:OTP";

        Map<String, Object> params = new HashMap<>();
        params.put("Flag", "V");
        params.put("Mobile", mobile);
        params.put("OTP", otp);

        List<Map<String, Object>> rows = namedJdbcTemplate.queryForList(sql, params);
        Map<String, Object> response = new HashMap<>();
        for (Map<String, Object> row : rows)
        {
            response.put("Status",(row.get("Status") != null ? row.get("Status").toString() : ""));
            response.put( "Message",(row.get("Message") != null ? row.get("Message").toString() : ""));
        }
        return response;
    }


    public Map<String, Object> sendOTP(String mobile, String otp)
    {
        String sql = "EXEC sp_manageMobileOTP @Flag=:Flag, @Mobile=:Mobile, @OTP=:OTP";

        Map<String, Object> params = new HashMap<>();
        params.put("Flag", "S");
        params.put("Mobile", mobile);
        params.put("OTP", otp);

        List<Map<String, Object>> rows = namedJdbcTemplate.queryForList(sql, params);

        Map<String, Object> response = new HashMap<>();

        if (!rows.isEmpty())
        {
            Map<String, Object> row = rows.get(0);

            response.put("Status",
                    row.get("Status") != null ? row.get("Status").toString() : "0");

            response.put("Message",
                    row.get("Message") != null ? row.get("Message").toString() : "");
        }
        else
        {
            response.put("Status", "0");
            response.put("Message", "Unable to send OTP");
        }

        return response;
    }



}
