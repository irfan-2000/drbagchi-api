package com.drbagchisclasses.drbagchi_api.dto;

public class SubmittedAnswerDto
{
    public int questionId;
    public String questionType; // MCQ | MSQ | NUM

    // MCQ → "A"
    // MSQ → ["A","C"]
    // NUM → "15.5"
    public Object answer;
}
