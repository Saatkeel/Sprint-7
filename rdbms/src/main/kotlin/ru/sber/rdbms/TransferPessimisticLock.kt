package ru.sber.rdbms

import java.sql.DriverManager
import java.sql.SQLException
import kotlin.math.max
import kotlin.math.min

class TransferPessimisticLock {
    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "12345"
    )
    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        val minId = min(accountId1,accountId2)
        val maxId = max(accountId1,accountId2)

        connection.use { conn ->
            val autoCommit = conn.autoCommit
            try {
                conn.autoCommit = false
                val prepareStatement1 = conn.prepareStatement("select * from account1 where id = $minId for update")
                prepareStatement1.use { statement ->
                    statement.executeQuery()
                }
                val prepareStatement2 = conn.prepareStatement("select * from account1 where id = $maxId for update")
                prepareStatement2.use { statement ->
                    statement.executeQuery()
                }
                val prepareStatement3 = conn.prepareStatement("update account1 set amount = amount - $amount where id = $minId")
                prepareStatement3.use { statement ->
                    statement.executeUpdate()
                }
                val prepareStatement4 = conn.prepareStatement("update account1 set amount = amount + $amount where id = $maxId")
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
