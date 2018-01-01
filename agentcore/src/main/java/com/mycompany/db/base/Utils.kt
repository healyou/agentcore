package com.mycompany.db.base

/**
 * Вспомогательные функции
 *
 * @author Nikita Gorodilov
 */
object Utils {

    /**
     * Проверка параметров на нулевое значение
     *
     * @return true - если один из параметров null
     */
    fun isOneNull(vararg args: Any?): Boolean {
        args.forEach {
            if (it == null) {
                return true
            }
        }

        return false
    }

    /**
     * Проверка параметров на не нулевое значение
     *
     * @return true - если один из параметров != null
     */
    fun isOneNotNull(vararg args: Any?): Boolean {
        args.forEach {
            if (it != null) {
                return true
            }
        }

        return false
    }
}