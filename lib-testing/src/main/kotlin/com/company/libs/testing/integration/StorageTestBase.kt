package com.company.libs.testing.integration

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles
import kotlin.collections.filter
import kotlin.collections.forEach
import kotlin.text.startsWith

@ActiveProfiles("test")
@SpringBootTest
class StorageTestBase {

    @Autowired
    protected lateinit var mongoTemplate: MongoTemplate

    @AfterEach
    fun cleanupDatabase() {
        mongoTemplate.collectionNames
            .filter { !it.startsWith("system.") }  // Skip system collections
            .forEach { mongoTemplate.dropCollection(it) }
    }
}