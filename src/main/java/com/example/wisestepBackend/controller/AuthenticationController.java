package com.example.wisestepBackend.controller;

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

    // get the email id of the user
    @PostMapping("get-email")
    public ResponseEntity<String> getEmail(@RequestBody User user){
        Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
        Boolean result = authenticationService.getEmail(user);
        if(result) {
            return new ResponseEntity<String>("Success", new HttpHeaders(), HttpStatus.OK);
        }
        return new ResponseEntity<String>("Failed", new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @PostMapping("get-code")
    public ResponseEntity<User> getCode(@RequestBody User user) {
        User savedUser = authenticationService.getCode(user);
        if(savedUser != null) return new ResponseEntity<User>(savedUser, new HttpHeaders(), HttpStatus.OK);
        else return new ResponseEntity<User>(null, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @PostMapping("logout-duplicate-session")
    public ResponseEntity<String> logoutDuplicateSession(@RequestBody User user) {
        if(authenticationService.logoutDuplicateSession(user)) return new ResponseEntity<String>("Success", new HttpHeaders(), HttpStatus.OK);
        else return new ResponseEntity<String>("Failed", new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @PostMapping("authenticate")
    public ResponseEntity<String> authenticate(@RequestBody User user) {
        if(authenticationService.authenticate(user)) return new ResponseEntity<>("Success", new HttpHeaders(), HttpStatus.OK);
        else return new ResponseEntity<String>("Failed", new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @PostMapping("logout")
    public ResponseEntity<String> logout(@RequestBody User user) {
        if(authenticationService.logout(user)) return new ResponseEntity<>("Success", new HttpHeaders(), HttpStatus.OK);
        else return new ResponseEntity<String>("Failed", new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

}
