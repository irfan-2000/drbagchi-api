package com.drbagchisclasses.drbagchi_api.util;
public class APIResponseHelper<T>  // <-- add <T> here
{
    private int status;
    private String message;
    private T result;

    public APIResponseHelper(int status, String message, T result)
    {
        this.status = status;
        this.message = message;
        this.result = result;
    }

    // Getters and setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getResult() { return result; }
    public void setResult(T result) { this.result = result; }
}
