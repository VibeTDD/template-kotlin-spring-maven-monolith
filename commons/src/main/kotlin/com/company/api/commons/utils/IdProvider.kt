package com.company.api.commons.utils

import java.util.UUID

interface IdProvider<T> {
    fun generate(vararg params: Any): T
}

class UUIDProvider : IdProvider<UUID> {
    override fun generate(vararg params: Any): UUID = uuidOf(params)
}