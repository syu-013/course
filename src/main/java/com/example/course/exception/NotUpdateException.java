package com.example.course.exception;

public class NotUpdateException extends RuntimeException{
    private static final long serialVersionUID=1L;
    public NotUpdateException(){
        super("更新できませんでした。");
    }
}