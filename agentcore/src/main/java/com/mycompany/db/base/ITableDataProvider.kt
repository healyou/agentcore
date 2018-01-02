package com.mycompany.db.base

/**
 * Интерфейс для работы с таблицами
 *
 * @author Nikita Gorodilov
 */
interface ITableDataProvider<out T> {

    /**
     * Количество записей в таблице
     */
    fun size(): Long

    /**
     *
     */
    fun get(size: Long): List<T>
}