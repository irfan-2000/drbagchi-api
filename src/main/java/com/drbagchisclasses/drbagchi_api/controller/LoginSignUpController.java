package com.drbagchisclasses.drbagchi_api.controller;

import com.drbagchisclasses.drbagchi_api.dto.*;
import com.drbagchisclasses.drbagchi_api.repository.LoginSignUpRepository;
import com.drbagchisclasses.drbagchi_api.service.SubjectService;
import com.drbagchisclasses.drbagchi_api.util.APIResponseHelper;
import com.drbagchisclasses.drbagchi_api.util.ExceptionLogger;
import com.drbagchisclasses.drbagchi_api.util.JwtUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@RestController
@RequestMapping("/api/guest/")
public class LoginSignUpController
{

    @Autowired
    private LoginSignUpRepository loginSignUpRepository;

    @Autowired
    private SubjectService subjectService;



    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("SubmitSignup")
    public APIResponseHelper<?> SubmitSignup (HttpServletRequest request,@RequestPart(value = "profileImage", required = false) MultipartFile profileImage    )
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
            dto.Caste = request.getParameter("caste");

            if(loginSignUpRepository.CheckIsExistdata("MOBILE",dto.phone) > 0)
            {
                return new APIResponseHelper<>(409, "Mobile number already exist", false);
            }


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
            Integer result = loginSignUpRepository.SignUp(dto);

            if (result > 0)
            {
                return new APIResponseHelper<>(200, "Success", result);
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

                String Token = jwtUtil.generateToken(result.FullName,result.UserId,result.Email);
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


    public Authentication getUserDetails()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication;
    }


    @GetMapping("GetAvailableSubjects")
    @PermitAll
    public APIResponseHelper<List<SubjectDto>> getAllSubjects(@RequestParam int ClassId)
    {
        try {

            List<SubjectDto> result = subjectService.getAllSubjects(ClassId);

            if (result != null && !result.isEmpty()) {
                return new APIResponseHelper<>(200, "Success", result);
            } else {
                return new APIResponseHelper<>(204, "No subjects found", null);
            }

        } catch (Exception ex) {
            return new APIResponseHelper<>(500, "Internal server error: " + ex.getMessage(), null);
        }
    }

    @GetMapping("GetAvailableClasses")
    @PermitAll
    public APIResponseHelper<List<Classes>> GetAvailableClasses()
    {
        try
        {
            List<Classes> result = subjectService.GetAvailableClasses();

            if (result != null && !result.isEmpty()) {
                return new APIResponseHelper<>(200, "Success", result);
            } else {
                return new APIResponseHelper<>(204, "No subjects found", null);
            }

        }catch (Exception ex)
        {
            return new APIResponseHelper<>(500, "Internal server error: " + ex.getMessage(), null);
        }

    }


    @GetMapping("GetAvailableBatches")
    public APIResponseHelper<List<Batch>> getAvailableBatches(int ClassId, int SubjectId , int BoardId)
    {
        try
        {
            List<Batch> result = subjectService.getAvailableBatches(ClassId,SubjectId,BoardId );

            if (result != null && !result.isEmpty())
            {
                return new APIResponseHelper<>(200, "Success", result);
            } else
            {
                return new APIResponseHelper<>(204, "No Batches found", null);
            }

        }catch (Exception ex)
        {
            return new APIResponseHelper<>(500, "Internal server error: " + ex.getMessage(), null);

        }
    }



    @GetMapping("GetAvailableBoards")
    @PermitAll
    public APIResponseHelper<List<Board>> GetAvailableBoards()
    {
        try
        {
            List<Board> result = subjectService.GetAvailableBoards();

            if (result != null && !result.isEmpty()) {
                return new APIResponseHelper<>(200, "Success", result);
            } else {
                return new APIResponseHelper<>(204, "No subjects found", null);
            }

        }catch (Exception ex)
        {
            return new APIResponseHelper<>(500, "Internal server error: " + ex.getMessage(), null);
        }

    }



    @PostMapping("VerifyOTP")
    @PermitAll
    public APIResponseHelper<?> VerifyOTP(String mobile, String purpose, String otp, String email)
    {

        var result = loginSignUpRepository.verifyOTP(mobile,otp);
     return new APIResponseHelper<>(200, "No subjects found", result);
    }

    @PostMapping("SendOTP")
    @PermitAll
    public APIResponseHelper<?> SendOTP(String mobile, String purpose,String email)
    {
        String otp = CreateOTP();
        var result = loginSignUpRepository.sendOTP(mobile,otp);
        return new APIResponseHelper<>(200, "OTP checked success", result);
    }


    public   String CreateOTP()
    {
        // Current datetime formatted to seconds
        String dateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Generate a 6-digit random session id
        String sessionId = String.format("%06d", new Random().nextInt(999999));

        return   sessionId;
    }


}


