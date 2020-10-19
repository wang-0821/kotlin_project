package com.xiao.demo.model

/**
 *
 * @author lix wang
 */
class User {
    var id: Long = 0
    var username: String = ""
    var password: String = ""

    override fun toString(): String {
        return "User(id=$id, username='$username', password='$password')"
    }
}