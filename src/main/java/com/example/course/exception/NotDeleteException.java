package com.example.course.exception;

public class NotDeleteException extends RuntimeException{
    private static final long serialVersionUID=1L;
    public NotDeleteException(){
        super("削除できませんでした。");
    }
}