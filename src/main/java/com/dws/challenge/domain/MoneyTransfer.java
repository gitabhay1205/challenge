package com.dws.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MoneyTransfer {

    @NotNull
    @NotEmpty
    private final String accountIdTo;

    @NotNull
    @NotEmpty
    private final String accountIdFrom;

    @NotNull
    @Min(value = 0, message = "Amount to be transferred must be positive.")
    private BigDecimal amount;

    @JsonCreator
    public MoneyTransfer(@JsonProperty("accountIdTo") String accountIdTo,
                         @JsonProperty("accountIdFrom") String accountIdFrom,
                   @JsonProperty("amount") BigDecimal amount) {
        this.accountIdTo = accountIdTo;
        this.accountIdFrom = accountIdFrom;
        this.amount = amount;
    }

}
