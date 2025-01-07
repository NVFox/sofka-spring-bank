package com.sofkau.bank.http.requests;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(BigDecimal amount, UUID destinationNumber) {
}
