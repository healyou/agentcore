package com.mycompany.db.base

/**
 * Интерфейс для работы с таблицами
 *
 * @author Nikita Gorodilov
 */
interface ITableDataProvider<out T> {

    // todo исправить его или удалить - пока возможно не нужен
    /**
     * Количество записей в таблице
     */
    fun size(): Long

    /**
     * Загрузка n записей
     * @param size количество записей
     */
    fun get(size: Long): List<T>
}