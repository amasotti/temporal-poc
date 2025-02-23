package com.tonihacks.temporalpoc

class CoreTransactionDetails : TransactionDetails {
    override var sourceAccountId: String? = null
        private set

    override var destinationAccountId: String? = null
        private set

    override var transactionReferenceId: String? = null
        private set

    override var amountToTransfer: Int = 0
        private set

    // MARK: Constructor
    constructor()

    constructor(
        sourceAccountId: String?,
        destinationAccountId: String?,
        transactionReferenceId: String?,
        amountToTransfer: Int,
    ) {
        this.sourceAccountId = sourceAccountId
        this.destinationAccountId = destinationAccountId
        this.transactionReferenceId = transactionReferenceId
        this.amountToTransfer = amountToTransfer
    }
}
