package com.yotsume.dao;

import com.yotsume.entity.User;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDaoInterface {

    RowMapper<User> ROW_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setBalance(rs.getBigDecimal("balance"));

        return user;
    };

    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAll();
    void update(User user);
    void addBalance(Long userId, BigDecimal balance);
    void downBalance(Long userId, BigDecimal balance);
    void delete(User user);
}
