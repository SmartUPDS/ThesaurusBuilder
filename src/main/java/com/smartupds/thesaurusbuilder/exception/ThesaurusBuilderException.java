package com.smartupds.thesaurusbuilder.exception;

/**  Generic Exceptions
 * 
 * @author Yannis Marketakis 
 */
public class ThesaurusBuilderException extends Exception{
    
    public ThesaurusBuilderException(String message){
        super(message);
    }
    
    public ThesaurusBuilderException(String message, Throwable thr){
        super(message,thr);
    }
}