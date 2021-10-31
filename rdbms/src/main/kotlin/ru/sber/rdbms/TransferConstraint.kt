package ru.sber.rdbms

import java.sql.DriverManager
import java.sql.SQLException

class TransferConstraint {
    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "postgres"
    )
    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        connection.use { conn ->
            val autoCommit = conn.autoCommit
            try {
                conn.autoCommit = false
//                val prepareStatement1 = conn.prepareStatement("select * from account1 where id = $accountId1")
//                prepareStatement1.use { statement ->
//                    statement.executeQuery()
//                }
//                val prepareStatement2 = conn.prepareStatement("select * from account1 where id = $accountId2")
//                prepareStatement2.use { statement ->
//                    statement.executeQuery()
//                }
                val prepareStatement3 = conn.prepareStatement("update account1 set amount = amount - $amount where id = $accountId1")
                prepareStatement3.use { statement ->
                    statement.executeUpdate()
                }
                val prepareStatement4 = conn.prepareStatement("update account1 set amount = amount + $amount where id = $accountId2")
                prepareStatement4.use { statement ->
                    statement.executeUpdate()
                }
                conn.commit()
            } catch (exception: SQLException) {
                println(exception.message)
                conn.rollback()
            } finally {
                conn.autoCommit = autoCommit
                println("Успешно!")
            }
        }
    }
}
