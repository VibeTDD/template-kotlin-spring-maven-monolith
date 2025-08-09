package dev.vibetdd.libs.testing

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

object Rand {

    fun uuid(): UUID = UUID.randomUUID()

    fun string(maxLength: Int = 30): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..maxLength)
            .map { chars.random() }
            .joinToString("")
    }

    fun alphaNumeric(length: Int = 10): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    fun alphabetic(length: Int = 10): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        return (1..length).map { chars.random() }.joinToString("")
    }

    fun numeric(length: Int = 10): String {
        return (1..length).map { Random.nextInt(0, 10) }.joinToString("")
    }

    fun int(min: Int = 0, max: Int = 100): Int = Random.nextInt(min, max + 1)

    fun long(min: Long = 0L, max: Long = 1000L): Long = Random.nextLong(min, max + 1)

    fun double(min: Double = 0.0, max: Double = 100.0): Double = Random.nextDouble(min, max).round(2)

    fun float(min: Float = 0.0f, max: Float = 100.0f): Float = Random.nextFloat() * (max - min) + min

    fun amount(from: Double = 0.01, to: Double = 100.0): BigDecimal =
        Random.nextDouble(from, to).round(2).toBigDecimal()

    fun percentage(): Double = Random.nextDouble(0.0, 100.0).round(2)

    fun age(min: Int = 18, max: Int = 120): Int = Random.nextInt(min, max + 1)

    fun year(from: Int = 1900, to: Int = 2024): Int = Random.nextInt(from, to + 1)

    fun boolean(): Boolean = Random.nextBoolean()

    fun date(
        startYear: Int = 2020,
        endYear: Int = 2024
    ): LocalDate {
        val startDate = LocalDate.of(startYear, 1, 1)
        val endDate = LocalDate.of(endYear, 12, 31)
        val randomDay = Random.nextLong(startDate.toEpochDay(), endDate.toEpochDay() + 1)
        return LocalDate.ofEpochDay(randomDay)
    }

    fun pastDate(daysBack: Long = 365): LocalDate =
        LocalDate.now().minusDays(Random.nextLong(1, daysBack + 1))

    fun futureDate(daysAhead: Long = 365): LocalDate =
        LocalDate.now().plusDays(Random.nextLong(1, daysAhead + 1))

    fun dateTime(
        startYear: Int = 2020,
        endYear: Int = 2024
    ): LocalDateTime {
        val randomDate = date(startYear, endYear)
        val randomHour = Random.nextInt(0, 24)
        val randomMinute = Random.nextInt(0, 60)
        val randomSecond = Random.nextInt(0, 60)
        return randomDate.atTime(randomHour, randomMinute, randomSecond)
    }

    fun currency(): String = Currency.getAvailableCurrencies().random().currencyCode

    fun email(): String =
        "${alphabetic(Random.nextInt(5, 15)).lowercase()}@${alphabetic(Random.nextInt(5, 10)).lowercase()}.com"

    fun phoneNumber(): String = "+${numeric(1)}${numeric(10)}"

    fun zipCode(): String = numeric(5)

    fun countryCode(): String = Locale.getAvailableLocales().random().country.takeIf { it.isNotEmpty() } ?: "US"

    // Common business identifiers
    fun iban(): String = "GB${numeric(2)}${alphaNumeric(4)}${numeric(14)}"

    fun creditCardNumber(): String = "${numeric(4)}-${numeric(4)}-${numeric(4)}-${numeric(4)}"

    fun socialSecurityNumber(): String = "${numeric(3)}-${numeric(2)}-${numeric(4)}"

    fun url(): String = "https://${alphabetic(Random.nextInt(5, 15)).lowercase()}.com"

    fun ipAddress(): String = "${int(1, 255)}.${int(0, 255)}.${int(0, 255)}.${int(1, 254)}"

    fun macAddress(): String = (1..6).joinToString(":") {
        String.format("%02X", Random.nextInt(0, 256))
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}