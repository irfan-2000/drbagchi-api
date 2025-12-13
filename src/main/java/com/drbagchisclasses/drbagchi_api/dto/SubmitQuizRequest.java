package com.drbagchisclasses.drbagchi_api.dto;


import java.util.List;

public class SubmitQuizRequest
{
    public int quizId;
    public String sessionId;
    public boolean autoSubmit;
    public boolean violation;
    public String submittedAtUTC;   // ISO string
    public List< SubmittedAnswerDto> answers;

}