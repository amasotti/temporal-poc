package com.tonihacks.temporalpoc

import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.workflow.Workflow
import java.time.Duration

class MoneyTransferWorkflowImpl : MoneyTransferWorkflow {
    // RetryOptions specify how to automatically handle retries when Activities fail
    private val retryoptions: RetryOptions =
        RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(1)) // Wait 1 second before first retry
            .setMaximumInterval(Duration.ofSeconds(20)) // Do not exceed 20 seconds between retries
            .setBackoffCoefficient(2.0) // Wait 1 second, then 2, then 4, etc
            .setMaximumAttempts(20) // Fail after 20 attempts
            .build()

    // ActivityOptions specify the limits on how long an Activity can execute before
    // being interrupted by the Orchestration service
    private val defaultActivityOptions: ActivityOptions =
        ActivityOptions.newBuilder()
            .setRetryOptions(retryoptions) // Apply the RetryOptions defined above
            .setStartToCloseTimeout(Duration.ofSeconds(2)) // Max execution time for single Activity
            .setScheduleToCloseTimeout(
                Duration.ofSeconds(300)
            ) // Entire duration from scheduling to completion including queue time
            .build()

    private val perActivityMethodOptions: MutableMap<String?, ActivityOptions?> =
        object : HashMap<String?, ActivityOptions?>() {
            init {
                // A heartbeat time-out is a proof-of life indicator that an activity is still
                // working.
                // The 5 second duration used here waits for up to 5 seconds to hear a heartbeat.
                // If one is not heard, the Activity fails.
                // The `withdraw` method is hard-coded to succeed, so this never happens.
                // Use heartbeats for long-lived event-driven applications.
                put(
                    WITHDRAW,
                    ActivityOptions.newBuilder().setHeartbeatTimeout(Duration.ofSeconds(5)).build(),
                )
            }
        }

    // ActivityStubs enable calls to methods as if the Activity object is local but actually perform
    // an RPC invocation
    private val accountActivityStub: AccountActivity =
        Workflow.newActivityStub<AccountActivity?>(
            AccountActivity::class.java,
            defaultActivityOptions,
            perActivityMethodOptions,
        )

    // The transfer method is the entry point to the Workflow
    // Activity method executions can be orchestrated here or from within other Activity methods
    override fun transfer(transaction: TransactionDetails?) {
        // Retrieve transaction information from the `transaction` instance
        val sourceAccountId = transaction?.sourceAccountId
        val destinationAccountId = transaction?.destinationAccountId
        val transactionReferenceId = transaction?.transactionReferenceId
        val amountToTransfer = transaction?.amountToTransfer

        // Stage 1: Withdraw funds from source
        try {
            // Launch `withdrawal` Activity
            accountActivityStub.withdraw(
                sourceAccountId,
                transactionReferenceId,
                amountToTransfer ?: 0,
            )
        } catch (e: Exception) {
            // If the withdrawal fails, for any exception, it's caught here
            System.out.printf(
                "[%s] Withdrawal of $%d from account %s failed",
                transactionReferenceId,
                amountToTransfer,
                sourceAccountId,
            )
            System.out.flush()

            // Transaction ends here
            return
        }

        // Stage 2: Deposit funds to destination
        try {
            // Perform `deposit` Activity
            accountActivityStub.deposit(
                destinationAccountId,
                transactionReferenceId,
                amountToTransfer ?: 0,
            )

            // The `deposit` was successful
            System.out.printf("[%s] Transaction succeeded.\n", transactionReferenceId)
            System.out.flush()

            //  Transaction ends here
            return
        } catch (e: Exception) {
            // If the deposit fails, for any exception, it's caught here
            System.out.printf(
                "[%s] Deposit of $%d to account %s failed.\n",
                transactionReferenceId,
                amountToTransfer,
                destinationAccountId,
            )
            System.out.flush()
        }

        // Continue by compensating with a refund
        try {
            // Perform `refund` Activity
            System.out.printf(
                "[%s] Refunding $%d to account %s.\n",
                transactionReferenceId,
                amountToTransfer,
                sourceAccountId,
            )
            System.out.flush()

            accountActivityStub.refund(
                sourceAccountId,
                transactionReferenceId,
                amountToTransfer ?: 0,
            )

            // Recovery successful. Transaction ends here
            System.out.printf(
                "[%s] Refund to originating account was successful.\n",
                transactionReferenceId,
            )
            System.out.printf(
                "[%s] Transaction is complete. No transfer made.\n",
                transactionReferenceId,
            )
            return
        } catch (e: Exception) {
            // A recovery mechanism can fail too. Handle any exception here
            System.out.printf(
                "[%s] Deposit of $%d to account %s failed. Did not compensate withdrawal.\n",
                transactionReferenceId,
                amountToTransfer,
                destinationAccountId,
            )
            System.out.printf("[%s] Workflow failed.", transactionReferenceId)
            System.out.flush()

            // Rethrowing the exception causes a Workflow Task failure
            throw (e)
        }
    }

    companion object {
        private const val WITHDRAW = "Withdraw"
    }
}
