package com.clarissa.thewholeshare.api.helpers

object ParticipantsStatuses {
    val PENDING = 0;
    val DELIVERING = 1;
    val DELIVERED = 2;
    val VERIFIED = 3;
    val CANCELLED = 4;

    val STATUSES = arrayOf(
        PENDING,
        DELIVERING,
        DELIVERED,
        VERIFIED,
        CANCELLED,
    )
}