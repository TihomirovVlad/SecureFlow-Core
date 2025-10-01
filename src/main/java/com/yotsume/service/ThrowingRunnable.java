package com.yotsume.service;

import java.sql.SQLException;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws SQLException;
}
