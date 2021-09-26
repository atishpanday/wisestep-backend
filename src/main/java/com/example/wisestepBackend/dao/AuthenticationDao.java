package com.example.wisestepBackend.dao;

import com.example.wisestepBackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AuthenticationDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User getEmail(User user) {
        String qryToCheckUser = "SELECT email, code, is_logged_in, created_time, session_id FROM user WHERE email=?";
        try {
            User savedUser = jdbcTemplate.queryForObject(qryToCheckUser, new Object[]{user.getEmail()}, new UserRowMapper());
            return savedUser;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setEmail(rs.getString("email"));
            user.setCode(rs.getString("code"));
            user.setIs_logged_in(rs.getBoolean("is_logged_in"));
            user.setCreated_time(rs.getString("created_time"));
            user.setSession_id(rs.getString("session_id"));
            return user;
        }
    }

    public Boolean addEmail(User user) {
        String qryToAddEmail = "INSERT INTO users (email, code, is_logged_in, session_id) VALUES (?, ?, ?, ?)";

        int qryResult = jdbcTemplate.update(qryToAddEmail, user.getEmail(), user.getCode(), user.getIs_logged_in(), user.getSession_id());

        return qryResult == 1;
    }

    public Boolean updateEmail(User user) {
        String qryToUpdateEmail = "UPDATE users SET code=?, is_logged_in=?, session_id=?, created_time=CURRENT_TIMESTAMP WHERE email=?";
        int qryResult = jdbcTemplate.update(qryToUpdateEmail, user.getCode(), user.getIs_logged_in(), user.getSession_id(), user.getEmail());
        return qryResult == 1;
    }

    public User getCode(User user) {
        String qryToGetCode = "SELECT email, code, created_time, is_logged_in, session_id FROM users WHERE email=?";
        try{
            User savedUser = jdbcTemplate.queryForObject(qryToGetCode, new Object[] {user.getEmail()}, new UserRowMapper());
            return savedUser;
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Boolean logoutDuplicateSession(User user) {
        String qryToLogoutDuplicateSession = "UPDATE users SET session_id=?, is_logged_in=0 WHERE email=?";
        int qryResult = jdbcTemplate.update(qryToLogoutDuplicateSession, user.getSession_id(), user.getEmail());
        return qryResult==1;
    }

    public String authenticate(User user) {
        String qryToAuthenticate = "SELECT email, code, is_logged_in, created_time, session_id FROM users WHERE email=?";
        User savedUser = jdbcTemplate.queryForObject(qryToAuthenticate, new Object[] {user.getEmail()}, new UserRowMapper());
        try{
            return savedUser.getSession_id();
        } catch(NullPointerException e) {
            return "";
        }
    }

    public Boolean logout(User user) {
        String qryToLogout = "UPDATE users SET is_logged_in=0 WHERE email=?";
        int qryResult = jdbcTemplate.update(qryToLogout, user.getEmail());
        return qryResult==1;
    }
}
