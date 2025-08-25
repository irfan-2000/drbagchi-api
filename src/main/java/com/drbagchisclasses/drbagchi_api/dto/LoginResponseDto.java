package com.drbagchisclasses.drbagchi_api.dto;

import java.util.List;

public class LoginResponseDto
{


    // Basic user info
   public  String UserId;
   public  String FullName;
   public  String Email;
   public  String role; // e.g., "Student", "Admin", "Teacher"

   public String token;
   public  String accessToken;
   public  String refreshToken; // if you are using refresh tokens

   public  String courses;
   public  List<CoursesEnrolled> enrolledCourses;




}
