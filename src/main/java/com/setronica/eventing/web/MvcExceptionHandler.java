package com.setronica.eventing.web;

import com.setronica.eventing.dto.ApplicationExceptionDto;
import com.setronica.eventing.exceptions.ApplicationLogicException;
import com.setronica.eventing.exceptions.NotFoundException;
import com.setronica.eventing.exceptions.OrderInProcess;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
@ControllerAdvice
public class MvcExceptionHandler {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(EventController.class);
    @ExceptionHandler({ ApplicationLogicException.class })
    public ResponseEntity<ApplicationExceptionDto> handleApplicationLogicException(ApplicationLogicException ex) {
        log.error("Something went wrong");
        ApplicationExceptionDto exceptionDto = new ApplicationExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatusCode.valueOf(500));
    }

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<ApplicationExceptionDto> handleNotFoundException(NotFoundException ex) {
        log.error("Entity not found");
        ApplicationExceptionDto exceptionDto = new ApplicationExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatusCode.valueOf(404));
    }

    @ExceptionHandler({OrderInProcess.class})
    public ResponseEntity<ApplicationExceptionDto> handleOrderCurrentlyBooked(OrderInProcess ex) {
        log.error("Can't modify already processed order");
        ApplicationExceptionDto exceptionDto = new ApplicationExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatusCode.valueOf(400));
    }
}
