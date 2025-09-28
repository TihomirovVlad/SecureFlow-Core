package com.yotsume.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
    private Long id;
    private String email;
    private BigDecimal balance;

    public User(String email) {
        this.email = email;
        this.balance = BigDecimal.ZERO;
    }
}
