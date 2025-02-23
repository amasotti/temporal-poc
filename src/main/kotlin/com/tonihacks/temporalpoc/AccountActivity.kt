package com.tonihacks.temporalpoc

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface AccountActivity {
    // Withdraw an amount of money from the source account
    @ActivityMethod fun withdraw(accountId: String?, referenceId: String?, amount: Int)

    // Deposit an amount of money into the destination account
    @ActivityMethod fun deposit(accountId: String?, referenceId: String?, amount: Int)

    // Compensate a failed deposit by refunding to the original account
    @ActivityMethod fun refund(accountId: String?, referenceId: String?, amount: Int)
}
