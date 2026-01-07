package com.example.course.exception;

public class NotInsertException extends RuntimeException{
    private static final long serialVersionUID= 1L;

    public NotInsertException() {
        super("登録できませんでした。");
    }
    
}
