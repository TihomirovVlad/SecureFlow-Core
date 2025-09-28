package com.yotsume;

import com.yotsume.dao.UserDao;
import com.yotsume.model.User;
import com.yotsume.service.TransferService;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) {
        UserDao userDao = new UserDao();
        TransferService transferService = new TransferService();

        // Создаём двух пользователей
        User alice = userDao.save(new User("alice@example.com"));
        User bob = userDao.save(new User("bob@example.com"));

        // Пополняем Alice (в реальности — через отдельный метод)
        userDao.updateBalance(alice.getId(), new BigDecimal("100.00"));

        // Переводим
        try {
            transferService.transferMoney(alice.getId(), bob.getId(), new BigDecimal("30.50"));
            System.out.println("Transfer successful!");
        } catch (Exception e) {
            System.err.println("Transfer failed: " + e.getMessage());
        }

        // Проверяем балансы
        System.out.println("Alice: " + userDao.findById(alice.getId()).map(User::getBalance).orElse(BigDecimal.ZERO));
        System.out.println("Bob: " + userDao.findById(bob.getId()).map(User::getBalance).orElse(BigDecimal.ZERO));
    }
}