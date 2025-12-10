package com.drbagchisclasses.drbagchi_api.controller;

import com.drbagchisclasses.drbagchi_api.config.JwtAuthenticationFilter;
import com.drbagchisclasses.drbagchi_api.dto.quizdataforuser;
import com.drbagchisclasses.drbagchi_api.repository.QuizRepository;
import com.drbagchisclasses.drbagchi_api.util.APIResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class QuizController
{
    @Autowired
    public QuizRepository quizservice;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @PostMapping("startquiz")
    @PreAuthorize("isAuthenticated()")
    public APIResponseHelper<?> startQuiz(@RequestParam String QuizId, @RequestParam String CourseId) {
        try {
            // Get student ID from JWT
            String studentId = jwtAuthenticationFilter.UserId;

            // Fetch quiz data
            var quizdata = quizservice.getquizdata(Integer.parseInt(QuizId));
            if (quizdata == null)
            {
                return new APIResponseHelper<>(404, "Data not found", null);
            }
            String sessionId = createSession();
            var startquiz = quizservice.startQuiz(  Integer.parseInt(studentId),   Integer.parseInt(CourseId),   Integer.parseInt(QuizId),"started",1,   sessionId);

            if(startquiz != null)
            {
                return new APIResponseHelper<>(200, "Success", startquiz);

            }else
            {                return new APIResponseHelper<>(500, "internal server error", startquiz);

            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return new APIResponseHelper<>(500, "Internal server error", null);
        }
    }

    @PostMapping("UpdatequizProgress")
    @PreAuthorize("isAuthenticated()")
    public APIResponseHelper<?> UpdatequizProgress(@RequestParam String SessionId,@RequestParam String QuizId)
    {
        try
        {
            var result = quizservice.getquizsessiondata(SessionId);
            if(result == null)
            {
                return new APIResponseHelper<>(404, "session not found", null);

            }


            if (!"started".equals(result.Status))
            {
                return new APIResponseHelper<>(404, "quiz not started or completed", null);
            }

            var quizdata = quizservice.getquizdata(Integer.parseInt(QuizId));
            if (quizdata == null)
            {
                return new APIResponseHelper<>(404, "Data not found", null);
            }

            Map<String, String> output = calculateEndDateTime(
                    quizdata.startDateTime,
                    quizdata.startTime,
                    quizdata.duration
            );
            int durationMinutes = getMinutes(quizdata.duration);



            if ("1".equals(result.Attempt))
            { // Fresh page load
                if (result.StartDate == null || result.StartDate.isEmpty())
                {
                  //String Sessionid,String Starttime,String Endtime,int Duration,String Startdate,String Enddate

                    LocalDateTime startTimeUTC = LocalDateTime.now(ZoneOffset.UTC);
                    LocalDateTime endTimeUTC = startTimeUTC.plusMinutes(durationMinutes);

                    // Optional: attach start/end times to response if needed
                    Map<String, Object> responseData = new HashMap<>();
                     responseData.put("startTimeUTC", startTimeUTC);
                    responseData.put("endTimeUTC", endTimeUTC);
                    responseData.put("sessionId", SessionId);
                    responseData.put("serverTimeUTC", LocalDateTime.now(ZoneOffset.UTC)); // <-- add this

                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    // current date UTC


                    LocalDate currentDateUTC = LocalDate.now(ZoneOffset.UTC);  // StartDate / EndDate in UTC


                    var quizresult = quizservice.UpdateQuizProgress(
                            SessionId,                          // flag
                            startTimeUTC,                            // id
                            endTimeUTC,  // studentId
                            durationMinutes ,   // courseId
                            currentDateUTC, // quizId
                            currentDateUTC);

                    return new APIResponseHelper<>(200, "quiz updated", responseData);

                }else
                {
                 // check if quiz is expired

                    Map<String, Object> responseData = new HashMap<>();
                    responseData.put("startTimeUTC", result.StartTime);
                    responseData.put("endTimeUTC", result.EndTime);
                    responseData.put("sessionId", SessionId);
                    responseData.put("serverTimeUTC", LocalDateTime.now(ZoneOffset.UTC)); // <-- add this

                    return new APIResponseHelper<>(200, "quiz updated", responseData);

                }

            }
            return new APIResponseHelper<>(404, "Data not found", null);


        }catch (Exception ex)
        {
            return new APIResponseHelper<>(404, "Data not found", null);

        }

    }



    public   Map<String, String> calculateEndDateTime(String startDate, String startTime, String quizDuration)
    {
        Map<String, String> result = new HashMap<>();
        // Parse date and times
        LocalDate date = LocalDate.parse(startDate.split(" ")[0]);  // picks YYYY-MM-DD
        LocalTime time = LocalTime.parse(startTime.substring(0, 8)); // only HH:mm:ss
        Duration duration = Duration.between(LocalTime.MIN, LocalTime.parse(quizDuration.substring(0, 8)));

        // Calculate combined datetime
        LocalDateTime startDateTime = LocalDateTime.of(date, time);
        LocalDateTime endDateTime = startDateTime.plus(duration);

        // Return values
        result.put("EndDate", endDateTime.toLocalDate().toString());
        result.put("EndTime", endDateTime.toLocalTime().toString());

        return result;
    }


    public   String createSession()
    {
        // Current datetime formatted to seconds
        String dateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Generate a 6-digit random session id
        String sessionId = String.format("%06d", new Random().nextInt(999999));

        return   sessionId;
    }

    public static int getMinutes(String timeString) {
        // Extract only HH:mm:ss part
        String[] parts = timeString.substring(0, 8).split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        // int seconds = Integer.parseInt(parts[2]); // optional if you want to include seconds

        return hours * 60 + minutes;
    }


    @GetMapping("GetALLQuizByStatus")
    @PreAuthorize("isAuthenticated()")
    public APIResponseHelper<?>GetALLQuizByStatus(String Flag)
    {
        String studentId = jwtAuthenticationFilter.UserId;

        try {
             var result = quizservice.GetALLQuizByStatus(Flag,Integer.parseInt(studentId));

            if (result != null)
            {
                return new APIResponseHelper<>(200, "Success", result);
            } else
            {
                return new APIResponseHelper<>(204, "No subjects found", null);
            }
        } catch (Exception ex)
        {
            String errorMessage = (ex.getMessage() != null) ? ex.getMessage() : "Unknown error";

            return new APIResponseHelper<>(500, "Internal Server Error" + ex.getMessage(), null);
        }


    }

    @PostMapping("GetQuizData")
    @PreAuthorize("isAuthenticated()")
        public APIResponseHelper<?> GetQuizData(String quizId, String sessionId) {
        try {

            // Fetch session info
            var session = quizservice.getquizsessiondata(sessionId);
            if (session == null) {
                return new APIResponseHelper<>(404, "Session not found", null);
            }

            // Status must be "started"
            if (!"started".equalsIgnoreCase(session.Status)) {
                return new APIResponseHelper<>(404, "Quiz not started or already completed", null);
            }

            // Current UTC date & time
            LocalDate currentDateUTC = LocalDate.now(ZoneOffset.UTC);
            LocalTime currentTimeUTC = LocalTime.now(ZoneOffset.UTC);

            // Convert DB values to LocalDate & LocalTime
            LocalDate endDate = LocalDate.parse(session.EndDate.split(" ")[0]);
            LocalTime endTime = LocalTime.parse(session.EndTime.split(" ")[1]);

            // Check if quiz time is over
            boolean timeOver =
                    currentDateUTC.isAfter(endDate) ||
                            (currentDateUTC.isEqual(endDate) && currentTimeUTC.isAfter(endTime));
            if (timeOver) {
                return new APIResponseHelper<>(410, "Quiz time over", null);
            }

            // Fetch quiz questions & details
            var quizdata = quizservice.getQuizData(Integer.parseInt(quizId));

            return new APIResponseHelper<>(200, "Quiz fetched successfully", quizdata);

        } catch (Exception ex) {
            ex.printStackTrace();
            return new APIResponseHelper<>(500, "Internal server error", null);
        }
    }


}
