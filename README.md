# challenge
DWS- Deutsche Bank Online Asessment To add functionality for transferring money from one account to another.
 What I did?
 1) I included a new POJO (MoneyTransfer) which has fields like accountIdTo, accountIdFrom and amount.
 2) I created an api for moneyTranser
 3) For thready safety I used Synchronized block with object lock. This could be improved by make using of AmoticBigDecimal instead of BigDecimal
    for balance field and use its method like getAndAdd() etc.
