package ru.sber.rdbms

import java.sql.DriverManager

fun main() {
    val transferConstraint = TransferConstraint()
    transferConstraint.transfer(
        1,
        2,
        500
    )
}