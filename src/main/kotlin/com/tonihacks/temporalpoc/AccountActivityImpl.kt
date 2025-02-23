package com.tonihacks.temporalpoc

import io.temporal.activity.Activity

class AccountActivityImpl : AccountActivity {
    // Mock up the withdrawal of an amount of money from the source account
    override fun withdraw(accountId: String?, referenceId: String?, amount: Int) {
        System.out.printf(
            "\nWithdrawing $%d from account %s.\n[ReferenceId: %s]\n",
            amount,
            accountId,
            referenceId,
        )
        System.out.flush()
    }

    // Mock up the deposit of an amount of money from the destination account
    override fun deposit(accountId: String?, referenceId: String?, amount: Int) {
        val activityShouldSucceed = true

        if (!activityShouldSucceed) {
            println("Deposit failed")
            System.out.flush()
            throw Activity.wrap(
                RuntimeException("Simulated Activity error during deposit of funds")
            )
        }

        System.out.printf(
            "\nDepositing $%d into account %s.\n[ReferenceId: %s]\n",
            amount,
            accountId,
            referenceId,
        )
        System.out.flush()
    }

    // Mock up a compensation refund to the source account
    override fun refund(accountId: String?, referenceId: String?, amount: Int) {
        val activityShouldSucceed = true

        if (!activityShouldSucceed) {
            println("Refund failed")
            System.out.flush()
            throw Activity.wrap(
                RuntimeException("Simulated Activity error during refund to source account")
            )
        }

        System.out.printf(
            "\nRefunding $%d to account %s.\n[ReferenceId: %s]\n",
            amount,
            accountId,
            referenceId,
        )
        System.out.flush()
    }
}
