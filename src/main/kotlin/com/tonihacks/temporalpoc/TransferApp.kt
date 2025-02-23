package com.tonihacks.temporalpoc

import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import io.temporal.serviceclient.WorkflowServiceStubs
import java.security.SecureRandom
import java.time.Instant
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.function.IntFunction
import java.util.stream.Collectors
import java.util.stream.IntStream

object TransferApp {
    // Seed the random number generator with nano date
    private val random: SecureRandom = SecureRandom()

    init {
        random.setSeed(Instant.now().getNano().toLong())
    }

    fun randomAccountIdentifier(): String {
        return IntStream.range(0, 9)
            .mapToObj<String?>(IntFunction { i: Int -> random.nextInt(10).toString() })
            .collect(Collectors.joining())
    }

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        // In the Java SDK, a stub represents an element that participates in
        // Temporal orchestration and communicates using gRPC.

        // A WorkflowServiceStubs communicates with the Temporal front-end service.

        val serviceStub = WorkflowServiceStubs.newLocalServiceStubs()

        // A WorkflowClient wraps the stub.
        // It can be used to start, signal, query, cancel, and terminate Workflows.
        val client = WorkflowClient.newInstance(serviceStub)

        // Workflow options configure  Workflow stubs.
        // A WorkflowId prevents duplicate instances, which are removed.
        val options =
            WorkflowOptions.newBuilder()
                .setTaskQueue(Shared.MONEY_TRANSFER_TASK_QUEUE)
                .setWorkflowId("money-transfer-workflow")
                .build()

        // WorkflowStubs enable calls to methods as if the Workflow object is local
        // but actually perform a gRPC call to the Temporal Service.
        val workflow: MoneyTransferWorkflow =
            client.newWorkflowStub<MoneyTransferWorkflow?>(
                MoneyTransferWorkflow::class.java,
                options,
            )

        // Configure the details for this money transfer request
        val referenceId = UUID.randomUUID().toString().substring(0, 18)
        val fromAccount = randomAccountIdentifier()
        val toAccount = randomAccountIdentifier()
        val amountToTransfer = ThreadLocalRandom.current().nextInt(15, 75)
        val transaction: TransactionDetails =
            CoreTransactionDetails(fromAccount, toAccount, referenceId, amountToTransfer)

        // Perform asynchronous execution.
        // This process exits after making this call and printing details.
        val we = WorkflowClient.start(workflow::transfer, transaction)

        System.out.printf("\nMONEY TRANSFER PROJECT\n\n")
        System.out.printf(
            "Initiating transfer of $%d from [Account %s] to [Account %s].\n\n",
            amountToTransfer,
            fromAccount,
            toAccount,
        )
        System.out.printf(
            "[WorkflowID: %s]\n[RunID: %s]\n[Transaction Reference: %s]\n\n",
            we.getWorkflowId(),
            we.getRunId(),
            referenceId,
        )
        System.exit(0)
    }
} // @@@SNIPEND
