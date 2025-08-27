package com.drbagchisclasses.drbagchi_api.controller;

import com.drbagchisclasses.drbagchi_api.dto.CourseById;
import com.drbagchisclasses.drbagchi_api.repository.AllCoursesRepository;
import com.drbagchisclasses.drbagchi_api.repository.LoginSignUpRepository;
import com.drbagchisclasses.drbagchi_api.util.APIResponseHelper;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api")
public class PricingController
{
    @Autowired
    private AllCoursesRepository allCourses;

    private final LoginSignUpController loginSignUpController;
    @Autowired
    public PricingController(LoginSignUpController loginSignUpController)
    {
        this.loginSignUpController = loginSignUpController;
    }

    @PostMapping("GetPricing")
    @PreAuthorize("isAuthenticated()")
    public APIResponseHelper<CourseById> GetPricing(@RequestBody Object dto)
    {

        Authentication data =  loginSignUpController.getUserDetails();
        try {

                    if (true)
                    {
                        return new APIResponseHelper<>(200, "Success", null);
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
