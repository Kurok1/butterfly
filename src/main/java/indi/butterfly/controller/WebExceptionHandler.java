package indi.butterfly.controller;

import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * web controller's exceptions handler
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.19
 */
@RestControllerAdvice
public class WebExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseMessage<Object> handle(Exception e) {
        logger.error(e.getMessage(), e);
        return MessageFactory.errorResponse(e.getMessage());
    }

}
