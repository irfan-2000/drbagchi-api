package com.drbagchisclasses.drbagchi_api.controller;

import com.drbagchisclasses.drbagchi_api.dto.Batch;
import com.drbagchisclasses.drbagchi_api.dto.LoginDto;
import com.drbagchisclasses.drbagchi_api.dto.LoginResponseDto;
import com.drbagchisclasses.drbagchi_api.dto.SignupDto;
import com.drbagchisclasses.drbagchi_api.repository.LoginSignUpRepository;
import com.drbagchisclasses.drbagchi_api.service.SubjectService;
import com.drbagchisclasses.drbagchi_api.util.APIResponseHelper;
import com.drbagchisclasses.drbagchi_api.util.ExceptionLogger;
import com.drbagchisclasses.drbagchi_api.util.JwtUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/")
public class LoginSignUpController
{

    @Autowired
    private LoginSignUpRepository loginSignUpRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("SubmitSignup")
    public APIResponseHelper<Boolean> SubmitSignup (HttpServletRequest request,@RequestPart(value = "profileImage", required = false) MultipartFile profileImage    )
    {
        try
        {

            SignupDto dto = new SignupDto();

            dto.fullName = request.getParameter("fullName");
            dto.email = request.getParameter("email");
            dto.phone = request.getParameter("mobile");
            dto.gender = request.getParameter("gender");
            dto.dateOfBirth = request.getParameter("dob");
            dto.parentName = request.getParameter("parentName");
            dto.parentMobile = request.getParameter("parentMobile");
            dto.address = request.getParameter("address");
            dto.city = request.getParameter("city");
            dto.state = request.getParameter("state");
            dto.pincode = request.getParameter("pincode");
            dto.institutionName = request.getParameter("institution");
            dto.boardId = request.getParameter("board");
            dto.classId = request.getParameter("class");  // handle carefully
            dto.SubjectId = request.getParameter("subject");
            dto.batchId = request.getParameter("batch");
            dto.password = request.getParameter("password");
            dto.IsEditing = request.getParameter("IsEditing");
            dto.OldProfileImage = request.getParameter("OldProfileImage");

            if (Boolean.parseBoolean(dto.IsEditing) && (profileImage == null))
            {
                dto.imageName = dto.OldProfileImage;
            } else
            {
                if (profileImage != null && !profileImage.isEmpty())
                {
                    // Generate random file name with extension
                    String extension = "";
                    String originalFilename = profileImage.getOriginalFilename();
                    if (originalFilename != null && originalFilename.contains(".")) {
                        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    String randomFileName = UUID.randomUUID().toString() + extension;

                    // Save file to target folder
                    String uploadDir = "D:\\Dr.Bagchi_Media\\StudentImages\\";
                    File dest = new File(uploadDir + randomFileName);
                    profileImage.transferTo(dest);

                    dto.imageName = randomFileName;
                }
            }
            boolean result = loginSignUpRepository.SignUp(dto);

            if (result)
            {
                return new APIResponseHelper<>(200, "Success", true);
            } else {
                return new APIResponseHelper<>(204, "No records inserted", false);
            }


        } catch (DuplicateKeyException ex) {
            // Handle duplicate key (e.g., phone/email already exists)
            return new APIResponseHelper<>(409, "Already exists", false);

        } catch (Exception ex)
        {
            ExceptionLogger.logException(ex);
            return new APIResponseHelper<>(500, "Internal Server Error", false);

        }
    }

    @PostMapping("Authenticate")
    @PermitAll
    public APIResponseHelper<LoginResponseDto> Authenticate(@RequestBody  LoginDto dto)
    {
        try {
            LoginResponseDto result = loginSignUpRepository.validateUser(dto.UserId, dto.Password);

            if (result != null && result.UserId != null && !result.UserId.isEmpty())
            {

                String Token = jwtUtil.generateToken(result.FullName,result.UserId);
    result.token =Token;
                // Successful login, JWT can be generated here
                return new APIResponseHelper<>(200, "Login successful", result);
            } else {
                // Invalid credentials
                return new APIResponseHelper<>(401, "Invalid UserName or Password", null);
            }
        } catch (Exception ex) {
            String errorMessage = (ex.getMessage() != null) ? ex.getMessage() : "Unknown error";

            return new APIResponseHelper<>(500, "Internal Server Error" + ex.getMessage(), null);
        }

    }


}


