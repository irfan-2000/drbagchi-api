package com.drbagchisclasses.drbagchi_api.repository;

import com.drbagchisclasses.drbagchi_api.dto.AllCourseDetails;
import com.drbagchisclasses.drbagchi_api.dto.quizdataforuser;
import com.drbagchisclasses.drbagchi_api.util.DbHelper;
import com.drbagchisclasses.drbagchi_api.util.SPHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
public class QuizRepository
{

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private SimpleJdbcCall simpleJdbcCall;

    @Value("${GlobalFetchPath}")
    public  String GlobalFetchPath;

    @Autowired
    private SPHelper spHelper;


    public class quizresponse
    {
        public String InsertedId;
        public String SessionId;
    }


    public quizresponse insertQuizAttempt(
            String flag,
            Integer id,
            Integer studentId,
            Integer courseId,
            Integer quizId,
            String startDate,
            String endDate,
            String endTime,
            String status,
            Integer violations,
            Integer isSubmitted,
            String sessionId,
            Integer duration,
            Integer attempt
    ) {
        quizresponse data = new quizresponse();

        try {
            String sql = "EXEC sp_InsertQuizAttempt " +
                    "@Flag=:Flag, @Id=:Id, @StudentId=:StudentId, @CourseId=:CourseId, @QuizId=:QuizId, " +
                    "@StartTime=:StartTime, @EndTime=:EndTime, @Status=:Status, @Violations=:Violations, " +
                    "@IsSubmitted=:IsSubmitted, @SessionId=:SessionId, @Duration=:Duration, @Attempt=:Attempt";

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("Flag", flag)
                    .addValue("Id", id)
                    .addValue("StudentId", studentId)
                    .addValue("CourseId", courseId)
                    .addValue("QuizId", quizId)
                    .addValue("EndTime", endTime)
                    .addValue("Status", status)
                    .addValue("Violations", violations)
                    .addValue("IsSubmitted", isSubmitted)
                    .addValue("SessionId", sessionId)
                    .addValue("Duration", duration)
                    .addValue("Attempt", attempt);

            List<Map<String, Object>> rows = namedJdbcTemplate.queryForList(sql, params);

            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.get(0);
                data.InsertedId = row.get("InsertedId") != null ? row.get("InsertedId").toString() : "";
                data.SessionId = row.get("SessionId") != null ? row.get("SessionId").toString() : "";
            }

        } catch (Exception ex)
        {
            System.err.println("InsertQuizAttempt Error: " + ex.getMessage());
            ex.printStackTrace();

        }

        return data;
    }



   public quizdataforuser getquizdata(int quizid)
   {
       quizdataforuser data = new quizdataforuser();

       try
       {
           String sql = "EXEC sp_InsertQuizAttempt@quizid=:quizid";

           MapSqlParameterSource params = new MapSqlParameterSource();
           DbHelper.addParameter(params, "quizid", quizid);
            List<Map<String,Object>> rows = namedJdbcTemplate.queryForList(sql,params);

           for (Map<String, Object> row : rows) {
               data.quizId = (row.get("QuizId") != null ? row.get("QuizId").toString() : "");
               data.courseId = (row.get("CourseId") != null ? row.get("CourseId").toString() : "");
               data.title = (row.get("Title") != null ? row.get("Title").toString() : "");
               data.startDateTime = (row.get("StartDateTime") != null ? row.get("StartDateTime").toString() : "");
               data.endDateTime = (row.get("EndDateTime") != null ? row.get("EndDateTime").toString() : "");
               data.duration = (row.get("Duration") != null ? row.get("Duration").toString() : "");
               data.createdAt = (row.get("CreatedAt") != null ? row.get("CreatedAt").toString() : "");
               data.updateAt = (row.get("UpdateAt") != null ? row.get("UpdateAt").toString() : "");
               data.startTime = (row.get("StartTime") != null ? row.get("StartTime").toString() : "");
               data.endTime = (row.get("EndTime") != null ? row.get("EndTime").toString() : "");
               data.status = (row.get("Status") != null ? row.get("Status").toString() : "");
               data.totalQuestions = (row.get("TotalQuestions") != null ? row.get("TotalQuestions").toString() : "");
               data.marksPerQuestion = (row.get("MarksPerQuestion") != null ? row.get("MarksPerQuestion").toString() : "");
               data.negativeMarking = (row.get("NegativeMarking") != null ? row.get("NegativeMarking").toString() : "");
               data.totalMarks = (row.get("TotalMarks") != null ? row.get("TotalMarks").toString() : "");
               data.allowNegative = (row.get("AllowNegative") != null ? row.get("AllowNegative").toString() : "");
               data.shuffleQuestions = (row.get("ShuffleQuestions") != null ? row.get("ShuffleQuestions").toString() : "");
               data.allowSkip = (row.get("AllowSkip") != null ? row.get("AllowSkip").toString() : "");
               data.subjects = (row.get("Subjects") != null ? row.get("Subjects").toString() : "");
            }

       }catch (Exception ex)
       {
           System.err.println("InsertQuizAttempt Error: " + ex.getMessage());
           ex.printStackTrace();

       }
       return data;
   }



}
