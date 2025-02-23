package com.tonihacks.temporalpoc

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(`as` = CoreTransactionDetails::class)
interface TransactionDetails {
    val sourceAccountId: String?
    val destinationAccountId: String?
    val transactionReferenceId: String?
    val amountToTransfer: Int
}
