package com.example.wisestepBackend.controller;

import com.example.wisestepBackend.model.ResponseMessage;
import com.example.wisestepBackend.model.User;
import com.example.wisestepBackend.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;
    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    // get the email id of the user
    @PostMapping("get-email")
    public ResponseEntity<ResponseMessage> getEmail(@RequestBody User user) {
        logger.info(user.getEmail());
        String result = authenticationService.getEmail(user);
        if (result.equals("CREATED") || result.equals("UPDATED")) {
            return new ResponseEntity<>(new ResponseMessage("CREATED"), new HttpHeaders(), HttpStatus.OK);
        } else if (result.equals("DATABASE_ERROR")) {
            return new ResponseEntity<>(new ResponseMessage("DATABASE_ERROR"), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (result.equals("INVALID_EMAIL")) {
            return new ResponseEntity<>(new ResponseMessage("INVALID_EMAIL"), new HttpHeaders(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new ResponseMessage("DUPLICATE_USER"), new HttpHeaders(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("get-code")
    public ResponseEntity<User> getCode(@RequestBody User user) {
        User savedUser = authenticationService.getCode(user);
        if (savedUser != null) return new ResponseEntity<>(savedUser, new HttpHeaders(), HttpStatus.OK);
        else return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @PutMapping("logout-duplicate-session")
    public ResponseEntity<ResponseMessage> logoutDuplicateSession(@RequestBody User user) {
        if (authenticationService.logoutDuplicateSession(user))
            return new ResponseEntity<>(new ResponseMessage("Success"), new HttpHeaders(), HttpStatus.OK);
        else return new ResponseEntity<>(new ResponseMessage("Failed"), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @PostMapping("authenticate")
    public ResponseEntity<ResponseMessage> authenticate(@RequestBody User user) {
        logger.info(user.getSession_id());
        if (authenticationService.authenticate(user))
            return new ResponseEntity<>(new ResponseMessage("Success"), new HttpHeaders(), HttpStatus.OK);
        else return new ResponseEntity<>(new ResponseMessage("Failed"), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @PutMapping("logout")
    public ResponseEntity<ResponseMessage> logout(@RequestBody User user) {
        if (authenticationService.logout(user))
            return new ResponseEntity<>(new ResponseMessage("Success"), new HttpHeaders(), HttpStatus.OK);
        else return new ResponseEntity<>(new ResponseMessage("Failed"), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

}
