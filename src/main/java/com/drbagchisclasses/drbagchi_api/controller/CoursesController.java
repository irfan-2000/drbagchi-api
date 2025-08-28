package com.drbagchisclasses.drbagchi_api.controller;

import com.drbagchisclasses.drbagchi_api.config.JwtAuthenticationFilter;
import com.drbagchisclasses.drbagchi_api.dto.AllCourseDetails;
import com.drbagchisclasses.drbagchi_api.dto.CourseById;
import com.drbagchisclasses.drbagchi_api.dto.LoginDto;
import com.drbagchisclasses.drbagchi_api.dto.LoginResponseDto;
import com.drbagchisclasses.drbagchi_api.repository.AllCoursesRepository;
import com.drbagchisclasses.drbagchi_api.repository.LoginSignUpRepository;
import com.drbagchisclasses.drbagchi_api.util.APIResponseHelper;
import io.jsonwebtoken.Jwt;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
@RestController
@RequestMapping("/api")
public class CoursesController
{
    @Autowired
    private AllCoursesRepository allCourses;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;


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




}
