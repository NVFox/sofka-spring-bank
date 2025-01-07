package com.sofkau.bank.commands;

/**
 * OperationalCommand
 */
public sealed interface OperationalCommand
        permits DepositCommand, WithdrawalCommand, TransferCommand {
}
