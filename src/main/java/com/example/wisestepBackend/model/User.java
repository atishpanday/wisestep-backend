package com.example.wisestepBackend.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class User {
    private String email;
    private String code;
    private String created_time;
    private Boolean is_logged_in;
    private String session_id;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    @Value("")
    public void setCode(String code) {
        this.code = code;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public Boolean getIs_logged_in() {
        return is_logged_in;
    }

    public void setIs_logged_in(Boolean is_logged_in) {
        this.is_logged_in = is_logged_in;
    }

    public String getSession_id() {
        return session_id;
    }

    @Value("")
    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
}
