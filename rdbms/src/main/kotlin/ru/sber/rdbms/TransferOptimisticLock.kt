package ru.sber.rdbms

import main.kotlin.ru.sber.rdbms.CustomException
import java.sql.DriverManager
import java.sql.SQLException

class TransferOptimisticLock {
    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "12345"
    )
    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        var version1: Long
        var version2: Long
        connection.use { conn ->
            val autoCommit = conn.autoCommit
            try {
                conn.autoCommit = false
                val prepareStatement1 = conn.prepareStatement("select * from account1 where id = $accountId1")
                prepareStatement1.use { statement ->
                    val resultSet = statement.executeQuery()
                    resultSet.use {
                        it.next()
                        version1 = it.getLong("version")
                        if(it.next())
                            throw CustomException("Запрос вернул более 1 записи")
                    }
                }
                val prepareStatement2 = conn.prepareStatement("select * from account1 where id = $accountId2")
                prepareStatement2.use { statement ->
                    val resultSet = statement.executeQuery()
                    resultSet.use {
                        it.next()
                        version2 = it.getLong("version")
                        if(it.next())
                            throw CustomException("Запрос вернул более 1 записи")
                    }
                }
                val prepareStatement3 = conn.prepareStatement("update account1 set " +
                        "amount = amount - $amount, version = $version1 + 1" +
                        "where id = $accountId1" +
                        "and version = $version1")
                prepareStatement3.use { statement ->
                    val resultSet = statement.executeUpdate()
                    if(resultSet==0)
                        throw CustomException("Запись была изменена другим пользователем")
                }
                val prepareStatement4 = conn.prepareStatement("update account1 set " +
                        "amount = amount + $amount, version = $version2+1 " +
                        "where id = $accountId2" +
                        "and version = $version2")
                prepareStatement4.use { statement ->
                    val resultSet = statement.executeUpdate()
                    if(resultSet==0)
                        throw CustomException("Запись была изменена другим пользователем")
                }
                conn.commit()
            } catch (exception: Exception) {
                println(exception.message)
                conn.rollback()
            } finally {
                conn.autoCommit = autoCommit
            }
        }
    }
}
