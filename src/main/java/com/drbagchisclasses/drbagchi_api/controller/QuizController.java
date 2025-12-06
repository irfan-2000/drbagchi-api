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
    public APIResponseHelper<?> startQuiz(@RequestParam String QuizId, @RequestParam String CourseId, @RequestParam int attempt) {
        try {
            // Get student ID from JWT
            String studentId = jwtAuthenticationFilter.UserId;

            // Fetch quiz data
            var quizdata = quizservice.getquizdata(Integer.parseInt(QuizId));
            if (quizdata == null) {
                return new APIResponseHelper<>(404, "Quiz not found", null);
            }

            // Calculate end time based on quiz duration
            Map<String, String> output = calculateEndDateTime(
                    quizdata.startDateTime,
                    quizdata.startTime,
                    quizdata.duration
            );

            // Generate 6-digit session ID
            String sessionId = createSession();  // returns only 6-digit string

            // Convert duration string to total minutes
            int durationMinutes = getMinutes(quizdata.duration);

            // Insert quiz attempt
            var quizresult = quizservice.insertQuizAttempt(
                    "I",                          // flag
                    0,                            // id
                    Integer.parseInt(studentId),  // studentId
                    Integer.parseInt(CourseId),   // courseId
                    Integer.parseInt(quizdata.quizId), // quizId
                    quizdata.startDateTime,       // startDate
                    quizdata.endDateTime,         // endDate
                    output.get("EndTime"),        // endTime
                    "Running",                    // status
                    0,                            // violations
                    0,                            // isSubmitted
                    sessionId,                    // sessionId
                    durationMinutes,              // duration in minutes
                    attempt                       // attempt number
            );

            if (quizresult != null) {
                // Calculate actual start and end times
                LocalDateTime startTimeUTC = LocalDateTime.now(ZoneOffset.UTC);
                LocalDateTime endTimeUTC = startTimeUTC.plusMinutes(durationMinutes);

                // Optional: attach start/end times to response if needed
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("quizResult", quizresult);
                responseData.put("startTimeUTC", startTimeUTC);
                responseData.put("endTimeUTC", endTimeUTC);
                responseData.put("sessionId", sessionId);

                return new APIResponseHelper<>(200, "Success", responseData);
            } else {
                return new APIResponseHelper<>(500, "Failed to insert quiz attempt", null);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return new APIResponseHelper<>(500, "Internal server error", null);
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




}
