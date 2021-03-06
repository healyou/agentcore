package com.mycompany.objects

import com.mycompany.user.User

/**
 * @author Nikita Gorodilov
 */
class UserObjects {

    static final loginUser(login, password) {
        User user = new User(login, password)
        user.id = 1L
        user.createDate = new Date()
        user
    }

    static final user() {
        User user = new User(StringObjects.randomString, StringObjects.randomString)
        user.id = 2L
        user.createDate = new Date()
        user
    }
}
