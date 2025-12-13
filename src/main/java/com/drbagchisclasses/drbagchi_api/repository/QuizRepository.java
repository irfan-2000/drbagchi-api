package com.drbagchisclasses.drbagchi_api.repository;

import com.drbagchisclasses.drbagchi_api.dto.*;
import com.drbagchisclasses.drbagchi_api.util.DbHelper;
import com.drbagchisclasses.drbagchi_api.util.SPHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.*;
import java.util.stream.Collectors;

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


    public class QuizDto
    {
        public int QuizId = 0;
        public int CourseId;


        public String Title = "";
        public List<QuizQuestionDto> Questions = new ArrayList<>();

        public int Status;

        public int TotalQuestions;

        public double MarksPerQuestion;

        public boolean AllowNegative;

        public boolean ShuffleQuestions;

        public double TotalMarks;

        public String Subjects;

        public boolean AllowSkip;

        public double Negativemarks;
    }



    public class QuizQuestionDto
    {
        public int QuestionId = 0;

        public String QuestionText = "";

        public String IsNumerical = "";

        public String PositiveMarks = "";

        public String NegativeMarks = "";

        public List<String> ImagePaths;

        public String NumericalAnswer = "";

        public List<QuizOptionDto> Options = new ArrayList<>();

        public String OptionA;
        public String OptionB;
        public String OptionC;
        public String OptionD;
        public int correctCount = 0;   // <── Add this

        public String QuestionType;

     }

    public class QuizOptionDto
    {
        public int QuestionId = 0;
        public String Text = "";
        public boolean IsCorrect = false;

    }



    public List<QuizQuestionDto> getQuizData(int quizId) {
        List<QuizQuestionDto> questions = new ArrayList<>();

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("@quizid", quizId);
            params.put("@Flag", "Id");

            Map<String, Object> result = spHelper.executeSP("usp_getquizdata", params);

            // --- TABLE 2 : QUESTIONS ---
            if (result.containsKey("#result-set-2")) {
                List<Map<String, Object>> table2 = (List<Map<String, Object>>) result.get("#result-set-2");

                for (Map<String, Object> row : table2)
                {
                    QuizQuestionDto question = new QuizQuestionDto();

                    question.QuestionId = row.get("QuestionId") != null
                            ? Integer.parseInt(row.get("QuestionId").toString())
                            : 0;

                    question.QuestionText = row.getOrDefault("QuestionText", "").toString();
                    question.IsNumerical = row.getOrDefault("IsNumerical", "").toString();
                    question.PositiveMarks = row.getOrDefault("PositiveMarks", "").toString();
                    question.NegativeMarks = row.getOrDefault("NegativeMarks", "").toString();

                    String raw = row.get("Images") != null ? row.get("Images").toString() : "";

                    if (raw.isEmpty())
                    {
                        question.ImagePaths = new ArrayList<>();
                    } else
                    {
                        question.ImagePaths = Arrays.stream(raw.split(","))
                                .map(img -> GlobalFetchPath +"QuestionImages/"+ img.trim())
                                .collect(Collectors.toList());
                    }


                    questions.add(question);
                }
            }

            // --- TABLE 3 : OPTIONS ---
            if (result.containsKey("#result-set-3"))
            {
                List<Map<String, Object>> table3 = (List<Map<String, Object>>) result.get("#result-set-3");

                for (Map<String, Object> row : table3) {
                    int qid = Integer.parseInt(row.get("QuestionId").toString());
                    String text = row.get("OptionText") != null ? row.get("OptionText").toString() : "";
                    int isCorrect = row.get("IsCorrect") != null ? Integer.parseInt(row.get("IsCorrect").toString()) : 0;

                    QuizQuestionDto q = questions.stream()
                            .filter(x -> x.QuestionId == qid)
                            .findFirst()
                            .orElse(null);

                    if (q == null) continue;

                    // Fill options in A → B → C → D order
                    if (q.OptionA == null || q.OptionA.isEmpty()) q.OptionA = text;
                    else if (q.OptionB == null || q.OptionB.isEmpty()) q.OptionB = text;
                    else if (q.OptionC == null || q.OptionC.isEmpty()) q.OptionC = text;
                    else if (q.OptionD == null || q.OptionD.isEmpty()) q.OptionD = text;

                     if (isCorrect == 1)
                        q.correctCount++;



                }
                for (QuizQuestionDto q : questions)
                {
                    if ("1".equals(q.IsNumerical))
                    {
                        q.QuestionType = "NUM";  // numerical input

                     }
                    else if (q.correctCount > 1)
                    {
                        q.QuestionType = "MSQ";  // multiple correct → checkbox
                    }
                    else
                    {
                        q.QuestionType = "MCQ";  // single correct → radio
                    }
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return questions;
    }




    public static String buildAnswersXml(List<SubmittedAnswerDto> answers) {

        StringBuilder xml = new StringBuilder();
        xml.append("<Answers>");

        for (SubmittedAnswerDto ans : answers) {

            xml.append("<Answer>");

            xml.append("<QuestionId>")
                    .append(ans.questionId)
                    .append("</QuestionId>");

            xml.append("<QuestionType>")
                    .append(ans.questionType)
                    .append("</QuestionType>");

            // Handle MCQ / MSQ / NUM
            String selectedOption = "";

            if (ans.answer != null) {
                if (ans.answer instanceof List) {
                    // MSQ
                    @SuppressWarnings("unchecked")
                    List<String> list = (List<String>) ans.answer;
                    selectedOption = String.join(",", list);
                } else {
                    // MCQ / NUM
                    selectedOption = ans.answer.toString();
                }
            }

            xml.append("<SelectedOption>")
                    .append(escapeXml(selectedOption))
                    .append("</SelectedOption>");

            xml.append("</Answer>");
        }

        xml.append("</Answers>");

        return xml.toString();
    }

    private static String escapeXml(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }





    public int  saveSubmittedAnswers(
            SubmitQuizRequest request,
            String answersXml,
            String studentId,quizsessiondata result
     ) {

        int insertedCount = 0;
        try {

            String sql = "EXEC sp_InsertQuizAnswers_XML " +
                    "@SessionId = :SessionId, " +
                    "@AnswersXml = :AnswersXml";

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("SessionId", request.sessionId);
            params.addValue("AnswersXml", answersXml);

            Map<String, Object> response =
                    namedJdbcTemplate.queryForMap(sql, params);

              insertedCount = response.get("InsertedCount") != null
                    ? Integer.parseInt(response.get("InsertedCount").toString())
                    : 0;

            System.out.println("✅ Quiz answers inserted: " + insertedCount);



        } catch (Exception ex) {
            System.err.println("❌ Error saving quiz answers");
            ex.printStackTrace();
            throw ex; // IMPORTANT: do not silently swallow errors
        }

        return insertedCount;
    }


}
