package com.drbagchisclasses.drbagchi_api.controller;

import com.drbagchisclasses.drbagchi_api.config.JwtAuthenticationFilter;
import com.drbagchisclasses.drbagchi_api.dto.AllCourseDetails;
import com.drbagchisclasses.drbagchi_api.dto.MyCoursesDto;
import com.drbagchisclasses.drbagchi_api.repository.AllCoursesRepository;
import com.drbagchisclasses.drbagchi_api.repository.MyCoursesRepository;
import com.drbagchisclasses.drbagchi_api.util.APIResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MyCoursesController
{
    @Autowired
    private MyCoursesRepository myCourses;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @GetMapping("GetMyCourses")
    @PreAuthorize("isAuthenticated()")
    public APIResponseHelper<List<MyCoursesDto>> GetMyCourses()
    {
        try {
            String userId = jwtAuthenticationFilter.UserId;


            List<MyCoursesDto> result = myCourses.GetMyCourses(Integer.parseInt(userId));

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

}
