package com.example.wisestepBackend.service;

import com.example.wisestepBackend.dao.AuthenticationDao;
import com.example.wisestepBackend.model.EmailConfig;
import com.example.wisestepBackend.model.User;
import com.example.wisestepBackend.utils.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.Random;

@Service
public class AuthenticationService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    AuthenticationDao authenticationDao;

    @Autowired
    EmailConfig emailConfig;

    @Autowired
    RandomString randomString;

    Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationService(EmailConfig emailConfig){
        this.emailConfig = emailConfig;
    }

    // code for getting the email id of the user and sending to the database
    public String getEmail(User user) {

        Random random = new Random();
        user.setCode(String.format("%04d", random.nextInt(10000)));
        user.setSession_id(randomString.generateRandomString());


        User savedUser = authenticationDao.getEmail(user);
        // set the user to be not logged in yet, until the code is verified
        user.setIs_logged_in(false);

        if (savedUser == null) {
            try{
                if(authenticationDao.addEmail(user)) {
                    sendEmailToUser(user);
                    return "CREATED";
                }
                else return "DATABASE_ERROR";
            } catch(MailSendException e) {
                return "INVALID_EMAIL";
            }
        } else if (!savedUser.getIs_logged_in()) {
            try{
                if(authenticationDao.updateEmail(user)) {
                    sendEmailToUser(user);
                    return "UPDATED";
                }
                else return "DATABASE_ERROR";
            } catch(MailSendException e) {
                return "INVALID_EMAIL";
            }
        } else {
            return "DUPLICATE_USER";
        }

    }

    private Boolean sendEmailToUser(User user){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.emailConfig.getHost());
        mailSender.setPort(this.emailConfig.getPort());
        mailSender.setUsername(this.emailConfig.getUsername());
        mailSender.setPassword(this.emailConfig.getPassword());

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("${spring.mail.username}");
        msg.setTo(user.getEmail());
        msg.setSubject("Your login request code");
        msg.setText("Your login code is: " + user.getCode());

        javaMailSender.send(msg);

        return true;
    }

    public User getCode(User user) {
        User savedUser = authenticationDao.getCode(user);
        if(verifyCode(savedUser, user)) {
            // set the user to be logged in after the code is verified
            savedUser.setIs_logged_in(true);
            authenticationDao.updateEmail(savedUser);
            savedUser.setIs_logged_in(true);
            return savedUser;
        } else {
            authenticationDao.logout(user);
            return null;
        }
    }

    private Boolean verifyCode(User savedUser, User user){
        // define the time of saving the code and add 15 minutes
        Timestamp timeOfSavingCode = Timestamp.valueOf(savedUser.getCreated_time());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeOfSavingCode.getTime());

        cal.add(Calendar.MINUTE, 15);

        Timestamp thresholdTime = new Timestamp(cal.getTime().getTime());

        logger.info(String.valueOf(thresholdTime));

        // define the time of verifying the code sent by the user
        Timestamp timeOfVerifyingCode = Timestamp.from(Instant.now());

        // check if the codes match and check if the time of verifying the code is not later than 15 minutes after the code was stored in the database
        return user.getCode().equals(savedUser.getCode()) && timeOfVerifyingCode.before(thresholdTime);
    }

    public Boolean logoutDuplicateSession(User user) {
        user.setSession_id(randomString.generateRandomString());
        return authenticationDao.logoutDuplicateSession(user);
    }

    public Boolean authenticate(User user) {
        return authenticationDao.authenticate(user).equals(user.getSession_id());
    }

    public Boolean logout(User user) {
        return authenticationDao.logout(user);
    }
}








































