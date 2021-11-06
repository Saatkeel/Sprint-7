package ru.sber.rdbms

import java.sql.DriverManager
import java.sql.SQLException
import kotlin.math.max
import kotlin.math.min

class TransferPessimisticLock {
    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "postgres"
    )
    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        val minId = min(accountId1,accountId2)
        val maxId = max(accountId1,accountId2)

        connection.use { conn ->
            val autoCommit = conn.autoCommit
            try {
                conn.autoCommit = false
                val prepareStatement1 = conn.prepareStatement("select * from account1 where id = ? for update")
                prepareStatement1.use { statement ->
                    statement.setLong(1, minId)
                    statement.executeQuery()
                }
                val prepareStatement2 = conn.prepareStatement("select * from account1 where id = ? for update")
                prepareStatement2.use { statement ->
                    statement.setLong(1, maxId)
                    statement.executeQuery()
                }
                val prepareStatement3 = conn.prepareStatement("update account1 set amount = amount - ? where id = ?")
                prepareStatement3.use { statement ->
                    statement.setLong(1,amount)
                    statement.setLong(2,minId)
                    statement.executeUpdate()
                }
                val prepareStatement4 = conn.prepareStatement("update account1 set amount = amount + ? where id = ?")
                prepareStatement4.use { statement ->
                    statement.setLong(1,amount)
                    statement.setLong(2,maxId)
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
