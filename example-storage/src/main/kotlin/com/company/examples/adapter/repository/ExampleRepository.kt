package com.company.examples.adapter.repository

import com.company.examples.adapter.repository.doc.ExampleDoc
import org.springframework.data.mongodb.repository.MongoRepository

interface ExampleRepository: MongoRepository<ExampleDoc, String> {

    fun existsByDataEmail(email: String): Boolean
}