package com.drbagchisclasses.drbagchi_api.repository;

import com.drbagchisclasses.drbagchi_api.dto.AllCourseDetails;
import com.drbagchisclasses.drbagchi_api.dto.MyCoursesDto;
import com.drbagchisclasses.drbagchi_api.util.DbHelper;
import com.drbagchisclasses.drbagchi_api.util.SPHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Repository
public class MyCoursesRepository
{
    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private SimpleJdbcCall simpleJdbcCall;

    @Value("${GlobalFetchPath}")
    public  String GlobalFetchPath;

    @Autowired
    private SPHelper spHelper;

    public List<MyCoursesDto> GetMyCourses(int Studentid)
    {
        List<MyCoursesDto> MyCourses = new ArrayList<MyCoursesDto>();

        String sql = "EXEC sp_GetMyEnrolledCourses @StudentId=:StudentId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        DbHelper.addParameter(params, "StudentId", Studentid);

        List<Map<String,Object>> rows = namedJdbcTemplate.queryForList(sql,params);
        for (Map<String, Object> row : rows)
        {
            MyCoursesDto data = new MyCoursesDto();

            data.courseId = row.get("CourseId") != null ? row.get("CourseId").toString() : "";
            data.batchId = row.get("BatchId") != null ? row.get("BatchId").toString() : "";
            data.validFrom = row.get("ValidFrom") != null ? row.get("ValidFrom").toString() : "";
            data.validTill = row.get("ValidTill") != null ? row.get("ValidTill").toString() : "";
            data.isActive = row.get("IsActive") != null ? row.get("IsActive").toString() : "";
            data.classId = row.get("ClassId") != null ? row.get("ClassId").toString() : "";
            data.boardId = row.get("BoardId") != null ? row.get("BoardId").toString() : "";
            data.teacherName = row.get("Teacher_Name") != null ? row.get("Teacher_Name").toString() : "";
            data.batchName = row.get("BatchName") != null ? row.get("BatchName").toString() : "";
            data.startDate = row.get("StartDate") != null ? row.get("StartDate").toString() : "";
            data.startTime = row.get("StartTime") != null ? row.get("StartTime").toString() : "";
            data.endDate = row.get("EndDate") != null ? row.get("EndDate").toString() : "";
            data.endTime = row.get("EndTime") != null ? row.get("EndTime").toString() : "";
            data.CourseImage = row.get("CourseImage") != null ? GlobalFetchPath +"CourseImages/"+ row.get("CourseImage").toString() : "";
            data.CourseName = row.get("CourseName") != null ?   row.get("CourseName").toString() : "";

            MyCourses.add(data);
        }

        return MyCourses;

    }
}
