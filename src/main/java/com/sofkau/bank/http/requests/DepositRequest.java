package com.sofkau.bank.http.requests;

import java.math.BigDecimal;

public record DepositRequest(BigDecimal amount) {
}