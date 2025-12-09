package com.drbagchisclasses.drbagchi_api.repository;

import com.drbagchisclasses.drbagchi_api.dto.AllCourseDetails;
import com.drbagchisclasses.drbagchi_api.dto.quizdataforuser;
import com.drbagchisclasses.drbagchi_api.dto.quizsessiondata;
import com.drbagchisclasses.drbagchi_api.util.DbHelper;
import com.drbagchisclasses.drbagchi_api.util.SPHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
           String sql = "EXEC Sp_GetQuizDataforuser @quizid = :quizid";

           MapSqlParameterSource params = new MapSqlParameterSource();
           DbHelper.addParameter(params, "quizid", quizid);
            List<Map<String,Object>> rows = namedJdbcTemplate.queryForList(sql,params);

           for (Map<String, Object> row : rows)
           {
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


    public class GetQuizStatus
    {
        public String QuizId  ;
        public String CourseId ;
        public String Title ;
        public String Duration ;
        public String StartDateTime ;
        public String EndDateTime ;
        public String StartTime;
        public String EndTime;

        public String BatchName ;
        public String CourseName;
        public String TotalQuestions;

    }



    public List<GetQuizStatus> GetALLQuizByStatus(String Flag ,int StudentId)
    {
        List<GetQuizStatus> quiz =  new ArrayList<GetQuizStatus>();

        try
        {
            String sql = "EXEC sp_GetQuizzesByAttemptStatus @Flag=:Flag, @StudentId=:StudentId";

            MapSqlParameterSource params = new MapSqlParameterSource();
            DbHelper.addParameter(params, "Flag", Flag);
            DbHelper.addParameter(params, "StudentId", StudentId);

            List<Map<String,Object>> rows = namedJdbcTemplate.queryForList(sql,params);

            for (Map<String, Object> row : rows)
            {
                GetQuizStatus data= new GetQuizStatus();

                data.QuizId       = row.get("QuizId")       != null ? row.get("QuizId").toString()       : "";
                data.CourseId     = row.get("CourseId")     != null ? row.get("CourseId").toString()     : "";
                data.Title        = row.get("Title")        != null ? row.get("Title").toString()        : "";
                data.Duration     = row.get("Duration")     != null ? row.get("Duration").toString()     : "";
                data.StartDateTime    =  row.get("StartDateTime") != null
                        ? LocalDate.parse(row.get("StartDateTime").toString().split(" ")[0]).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        : "";

                data.EndDateTime    =  row.get("EndDateTime") != null
                        ? LocalDate.parse(row.get("EndDateTime").toString().split(" ")[0]).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        : "";

                //data.EndDateTime       = row.get("EndDateTime") != null ? row.get("EndDateTime").toString() : "";
                data.StartTime      = row.get("StartTime")      != null ? row.get("StartTime").toString()      : "";
                data.EndTime  = row.get("EndTime")  != null ? row.get("EndTime").toString()  : "";
                data.BatchName   = row.get("BatchName")   != null ? row.get("BatchName").toString()   : "";
                data.CourseName   = row.get("CourseName")   != null ? row.get("CourseName").toString()   : "";

                data.TotalQuestions   = row.get("TotalQuestions")   != null ? row.get("TotalQuestions").toString()   : "";


                quiz.add(data);

                            }


        }catch (Exception ex)
        {
            System.err.println("InsertQuizAttempt Error: " + ex.getMessage());
            ex.printStackTrace();

        }
        return quiz;
    }

    public  class startQuizmodel
    {
        public  String id ;
        public  String Sessionid;
    }
    public startQuizmodel startQuiz(int studentId, int courseId, int quizId, String status, int attempt, String sessionId)
    {
        startQuizmodel data= new startQuizmodel();

        try {
            String sql = "EXEC StartInitiateQuiz @StudentId=:StudentId, @CourseId=:CourseId, @QuizId=:QuizId, " +
                    "@Status=:Status, @Attempt=:Attempt, @SessionId=:SessionId";

            MapSqlParameterSource params = new MapSqlParameterSource();
            DbHelper.addParameter(params, "StudentId", studentId);
            DbHelper.addParameter(params, "CourseId", courseId);
            DbHelper.addParameter(params, "QuizId", quizId);
            DbHelper.addParameter(params, "Status", status);
            DbHelper.addParameter(params, "Attempt", attempt);
            DbHelper.addParameter(params, "SessionId", sessionId);

            List<Map<String, Object>> rows = namedJdbcTemplate.queryForList(sql, params);


            for (Map<String, Object> row : rows) {
                data.id = (row.get("InsertedId") != null ? row.get("InsertedId").toString() : "");
                data.Sessionid = (row.get("SessionId") != null ? row.get("SessionId").toString() : "");


            }
        }
        catch (Exception ex)
        {

        }

        return data;
    }


    public quizsessiondata getquizsessiondata(String Sessionid)
    {

         quizsessiondata quiz =  new quizsessiondata();

        try
        {
            String sql = "EXEC UpdateQuizProgress @Flag = :Flag, @SessionId = :SessionId";

            MapSqlParameterSource params = new MapSqlParameterSource();
            DbHelper.addParameter(params, "Flag", "G");
            DbHelper.addParameter(params, "SessionId", Sessionid);

            List<Map<String,Object>> rows = namedJdbcTemplate.queryForList(sql,params);

            for (Map<String, Object> row : rows)
            {
                quiz.Id               = row.get("Id")               != null ? row.get("Id").toString()               : "";
                quiz.StudentId        = row.get("StudentId")        != null ? row.get("StudentId").toString()        : "";
                quiz.CourseId         = row.get("CourseId")         != null ? row.get("CourseId").toString()         : "";
                quiz.QuizId           = row.get("QuizId")           != null ? row.get("QuizId").toString()           : "";
                quiz.StartTime        = row.get("StartTime")        != null ? row.get("StartTime").toString()        : "";
                quiz.EndTime          = row.get("EndTime")          != null ? row.get("EndTime").toString()          : "";
                quiz.Status           = row.get("Status")           != null ? row.get("Status").toString()           : "";
                quiz.Violations       = row.get("Violations")       != null ? row.get("Violations").toString()       : "";
                quiz.IsSubmitted      = row.get("IsSubmitted")      != null ? row.get("IsSubmitted").toString()      : "";
                quiz.CreatedAt        = row.get("CreatedAt")        != null ? row.get("CreatedAt").toString()        : "";
                quiz.UpdatedAt        = row.get("UpdatedAt")        != null ? row.get("UpdatedAt").toString()        : "";
                quiz.SessionId        = row.get("SessionId")        != null ? row.get("SessionId").toString()        : "";
                quiz.Attempt          = row.get("Attempt")          != null ? row.get("Attempt").toString()          : "";
                quiz.Duration         = row.get("Duration")         != null ? row.get("Duration").toString()         : "";
                quiz.StartDate        = row.get("StartDate")        != null ? row.get("StartDate").toString()        : "";
                quiz.EndDate          = row.get("EndDate")          != null ? row.get("EndDate").toString()          : "";
                quiz.SubmittedMessage = row.get("SubmittedMessage") != null ? row.get("SubmittedMessage").toString() : "";
            }



        }catch (Exception ex)
        {
            System.err.println("InsertQuizAttempt Error: " + ex.getMessage());
            ex.printStackTrace();

        }
        return quiz;

    }

    public boolean UpdateQuizProgress(String Sessionid, LocalDateTime startTime, LocalDateTime endTime, int duration, LocalDate startDate, LocalDate endDate) {

        String sql = "EXEC UpdateQuizProgress @Flag = :Flag, @SessionId = :SessionId, @StartTime = :StartTime, @EndTime = :EndTime, @Duration = :Duration, @StartDate = :StartDate, @EndDate = :EndDate";

        MapSqlParameterSource params = new MapSqlParameterSource();
        DbHelper.addParameter(params, "Flag", "U");
        DbHelper.addParameter(params, "SessionId", Sessionid);
        DbHelper.addParameter(params, "StartTime", Timestamp.valueOf(startTime));
        DbHelper.addParameter(params, "EndTime", Timestamp.valueOf(endTime));
        DbHelper.addParameter(params, "Duration", duration);
        DbHelper.addParameter(params, "StartDate", java.sql.Date.valueOf(startDate));
        DbHelper.addParameter(params, "EndDate", java.sql.Date.valueOf(endDate));

        int result = namedJdbcTemplate.update(sql, params);
        return result > 0;
    }




}
