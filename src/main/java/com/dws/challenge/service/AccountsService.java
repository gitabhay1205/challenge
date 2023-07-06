package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.MoneyTransfer;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  private NotificationService notificationService;

  private static final Object lock = new Object();

  @Autowired
  public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  public void transferAmount(MoneyTransfer accountTransfer) {

      Account accountToBeDebited = getAccount(accountTransfer.getAccountIdFrom());
      Account accountToBeCredited = getAccount(accountTransfer.getAccountIdTo());

      if(Objects.isNull(accountToBeDebited)) {
         throw new AccountNotFoundException("No Account Exist with accountId "+accountTransfer.getAccountIdFrom());
      }
      if(Objects.isNull(accountToBeCredited)) {
        throw new AccountNotFoundException("No Account Exist with accountId "+accountTransfer.getAccountIdTo());
      }

      //IF Balance is enough so that amount could be debited
      if(accountToBeDebited.getBalance().compareTo(accountTransfer.getAmount()) >=0) {

          synchronized (lock) {

              BigDecimal balanceAfterAmountDebited = accountToBeDebited.getBalance().subtract(accountTransfer.getAmount());

              //Update New Balance in the account
              Account updatedDebitedAccount = new Account(accountTransfer.getAccountIdFrom(), balanceAfterAmountDebited);
              this.accountsRepository.updateAccountBalance(updatedDebitedAccount);
              this.notificationService.notifyAboutTransfer(updatedDebitedAccount, "Amount of RS " + accountTransfer.getAmount() + " is debited from your account");

              BigDecimal balanceAfterAmountCredited = accountToBeCredited.getBalance().add(accountTransfer.getAmount());
              //Update New Balance in the account
              Account updatedCreditedAccount = new Account(accountTransfer.getAccountIdTo(), balanceAfterAmountCredited);
              this.accountsRepository.updateAccountBalance(updatedCreditedAccount);
              this.notificationService.notifyAboutTransfer(updatedCreditedAccount, "Amount of RS " + accountTransfer.getAmount() + " is credited into your account");
          }

      } else {
        throw new InsufficientBalanceException("Your account has insufficient balance to make this transaction");
      }
  }
}
