package com.drbagchisclasses.drbagchi_api.repository;
import com.drbagchisclasses.drbagchi_api.dto.Batch;
import com.drbagchisclasses.drbagchi_api.dto.Board;
import com.drbagchisclasses.drbagchi_api.dto.Classes;
import com.drbagchisclasses.drbagchi_api.dto.SubjectDto;
import com.drbagchisclasses.drbagchi_api.util.DbHelper;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.web.bind.annotation.RequestParam;


@Repository
public class SubjectRepository
{

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private   SimpleJdbcCall simpleJdbcCall;

    public List<SubjectDto> getAllSubjects( int ClassId)
    {
        List<SubjectDto> subjects = new ArrayList<SubjectDto>();

        String sql = "EXEC Sp_Manage_AvailableSubjects @From=:From, @Flag=:Flag,@ClassId =:ClassId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        DbHelper.addParameter(params, "From", "U");
        DbHelper.addParameter(params, "Flag", "S");
        DbHelper.addParameter(params,"ClassId",ClassId);

        List<Map<String, Object>> rows = namedJdbcTemplate.queryForList(sql, params);

        for (Map<String, Object> row : rows) {
            SubjectDto subject = new SubjectDto();
            subject.SubjectId = (row.get("SubjectId") != null ? Integer.parseInt(row.get("SubjectId").toString()) : 0);
            subject.ClassId = (row.get("ClassId") != null ? Integer.parseInt(row.get("ClassId").toString()) : 0);
            subject.SubjectName = (row.get("SubjectName") != null ?  (row.get("SubjectName").toString()) : "");
            subject.Status = row.get("Status") != null ? Integer.parseInt(row.get("Status").toString()) :0;
            subjects.add(subject);
        }
    return subjects;
     }


    @PermitAll
    public List<Board>GetAvailableBoards()
    {
        List<Board> Boards = new ArrayList<Board>();

        String sql = "EXEC sp_Manage_AvailableBoards @From=:From, @Flag=:Flag";

        MapSqlParameterSource params = new MapSqlParameterSource();
        DbHelper.addParameter(params, "From", "U");
        DbHelper.addParameter(params, "Flag", "S");

        List<Map<String,Object>> rows = namedJdbcTemplate.queryForList(sql,params);
        for (Map<String ,Object> row:rows)
        {
            Board data = new Board();
            data.BoardId = (row.get("BoardId") != null ? Integer.parseInt(row.get("BoardId").toString()) : 0);
            data.BoardName = (row.get("BoardName") != null ? row.get("BoardName").toString() : "");
            data.Status = (row.get("Status") != null ? Integer.parseInt(row.get("Status").toString()) :0 );
            Boards.add(data);
        }
        return Boards;

    }


   public List<Classes>GetAvailableClasses()
    {
        List<Classes> Classes = new ArrayList<Classes>();

        String sql = "EXEC sp_Manage_AvailableClasses @From=:From, @Flag=:Flag";

        MapSqlParameterSource params = new MapSqlParameterSource();
        DbHelper.addParameter(params, "From", "U");
        DbHelper.addParameter(params, "Flag", "S");

        List<Map<String,Object>> rows = namedJdbcTemplate.queryForList(sql,params);
        for (Map<String ,Object> row:rows)
        {
            Classes data = new Classes();
            data.ClassId = (row.get("ClassId") != null ? Integer.parseInt(row.get("ClassId").toString()) : 0);
            data.ClassName = (row.get("ClassName") != null ? row.get("ClassName").toString() : "");
            data.Status = (row.get("Status") != null ? Integer.parseInt(row.get("Status").toString()) :0 );
            Classes.add(data);
        }
        return Classes;

    }

    public List<Batch>getAvailableBatches( int ClassId, int SubjectId , int BoardId )
    {
        List<Batch> Boards = new ArrayList<Batch>();

        String sql = "EXEC sp_Manage_AvailableBatches  @From=:From, @Flag=:Flag,@ClassId=:ClassId,@SubjectId=:SubjectId,@BoardId=:BoardId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        DbHelper.addParameter(params, "From", "U");
        DbHelper.addParameter(params, "Flag", "S");
         DbHelper.addParameter(params, "ClassId", ClassId);
         DbHelper.addParameter(params, "SubjectId", SubjectId);
         DbHelper.addParameter(params, "BoardId", BoardId);

        List<Map<String,Object>> rows = namedJdbcTemplate.queryForList(sql,params);
        for (Map<String ,Object> row:rows)
        {
            Batch data = new Batch();

            data.BatchId = (row.get("BatchId") != null ? Integer.parseInt(row.get("BatchId").toString()) : 0);
            data.BatchName = (row.get("BatchName") != null ? (row.get("BatchName").toString()) : "");
            data.ClassId = (row.get("ClassId") != null ? Integer.parseInt(row.get("ClassId").toString()) : 0);
            data.SubjectId = (row.get("SubjectId") != null ? Integer.parseInt(row.get("SubjectId").toString()) : 0);
            data.BoardId = (row.get("BoardId") != null ? Integer.parseInt(row.get("BoardId").toString()) : 0);
            data.StartDate = (row.get("StartDate") != null ?   (row.get("StartDate").toString()) : "");
            data.EndDate = (row.get("EndDate") != null ?   (row.get("EndDate").toString()) : "");
            data.Status = (row.get("Status") != null ? Integer.parseInt(row.get("Status").toString()) : 0);
            Boards.add(data);
        }

        return Boards;

    }




}

