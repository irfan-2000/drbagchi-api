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








}