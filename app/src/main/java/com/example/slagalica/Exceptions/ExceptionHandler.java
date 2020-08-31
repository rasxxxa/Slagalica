package com.example.slagalica.Exceptions;

public class ExceptionHandler extends Exception{

    protected ErrorInfo errorInfo;

    public ExceptionHandler(ErrorInfo errorInfo)
    {
        super(errorInfo.getValue());
        this.errorInfo = errorInfo;
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }
}
