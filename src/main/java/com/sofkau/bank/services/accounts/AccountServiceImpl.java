package com.sofkau.bank.services.accounts;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.Account.Type;
import com.sofkau.bank.exceptions.AlreadyExistsException;
import com.sofkau.bank.exceptions.NotFoundException;
import com.sofkau.bank.repositories.accounts.AccountRepository;
import com.sofkau.bank.repositories.accounts.AccountTypeRepository;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;

    public AccountServiceImpl(AccountRepository accountRepository, AccountTypeRepository accountTypeRepository) {
        this.accountRepository = accountRepository;
        this.accountTypeRepository = accountTypeRepository;
    }

    @Override
    public Account createAccount(Account account) {
        if (account.getClient() == null)
            throw new NotFoundException();

        if (account.getId() > 0)
            throw new AlreadyExistsException();

        return accountRepository.save(account);
    }

    @Override
    public void updateAccount(UUID number, Account account) {
        if (account.getClient() == null)
            throw new NotFoundException();

        Account stored = findAccountByNumber(number);

        stored.setType(account.getType());
        stored.setClient(account.getClient());
        stored.setBalance(account.getBalance());

        accountRepository.save(stored);
    }

    @Override
    public Type findAccountTypeByName(Type.Name name) {
        String typeName = name.name();

        return accountTypeRepository.findByName(typeName)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Account findAccountByNumber(UUID number) {
        return accountRepository.findByNumber(number)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public boolean accountBelongsToClient(UUID number, Client client) {
        return accountRepository.existsByNumberAndClient(number, client);
    }

    @Override
    public List<Account> findClientAccounts(Client client) {
        return accountRepository.findAllByClient(client);
    }
}
