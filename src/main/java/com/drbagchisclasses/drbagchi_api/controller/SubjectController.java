package com.drbagchisclasses.drbagchi_api.controller;

import com.drbagchisclasses.drbagchi_api.dto.Batch;
import com.drbagchisclasses.drbagchi_api.dto.Board;
import com.drbagchisclasses.drbagchi_api.dto.Classes;
import com.drbagchisclasses.drbagchi_api.dto.SubjectDto;
import com.drbagchisclasses.drbagchi_api.service.SubjectService;
import com.drbagchisclasses.drbagchi_api.util.APIResponseHelper;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping("GetAvailableBoards")
    @PreAuthorize("isAuthenticated()")    // Now only requests with valid token allowed
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


}