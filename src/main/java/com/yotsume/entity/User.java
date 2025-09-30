package com.yotsume.entity;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {
    private Long id;
    private String email;
    private BigDecimal balance;

    public User(String email) {
        if (this.email == null || !this.email.contains("@")) {
            throw new IllegalArgumentException("Email address is invalid " + this.email);
        }
        this.email = email;
        this.balance = BigDecimal.ZERO;
    }
}
