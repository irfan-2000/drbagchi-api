package com.drbagchisclasses.drbagchi_api.service;

import com.drbagchisclasses.drbagchi_api.dto.Batch;
import com.drbagchisclasses.drbagchi_api.dto.Board;
import com.drbagchisclasses.drbagchi_api.dto.Classes;
import com.drbagchisclasses.drbagchi_api.dto.SubjectDto;
import com.drbagchisclasses.drbagchi_api.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SubjectService
{
@Autowired
  private SubjectRepository subjectRepository;

public List<SubjectDto> getAllSubjects(int ClassId)
{
    return subjectRepository.getAllSubjects(ClassId);
}


public List<Batch> getAvailableBatches(int ClassId, int SubjectId , int BoardId)
{

    return subjectRepository.getAvailableBatches(ClassId,SubjectId,BoardId);
}

public List<Classes> GetAvailableClasses( )
{

    return subjectRepository.GetAvailableClasses( );
}
public List<Board> GetAvailableBoards( )
{

    return subjectRepository.GetAvailableBoards( );
}




}
