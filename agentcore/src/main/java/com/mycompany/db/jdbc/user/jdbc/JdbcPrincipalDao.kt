package com.mycompany.db.jdbc.user.jdbc

import com.mycompany.db.base.AbstractDao
import com.mycompany.db.base.AbstractRowMapper
import com.mycompany.db.base.Codable
import com.mycompany.db.jdbc.user.PrincipalDao
import org.springframework.stereotype.Component
import com.mycompany.user.Authority
import com.mycompany.user.Principal
import com.mycompany.user.User
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

/**
 * @author Nikita Gorodilov
 */
@Component
class JdbcPrincipalDao: AbstractDao(), PrincipalDao {

    override fun getPrincipal(login: String): Principal {
        return jdbcTemplate.queryForObject("select * from users where UPPER(login) = UPPER(?)",
                object : AbstractRowMapper<Principal>() {

                    @Throws(SQLException::class)
                    override fun mapRow(rs: ResultSet, i: Int): Principal {
                        val userId = getLong(rs, "id")
                        val user = User(getString(rs, "login"), getString(rs, "password"))
                        user.id = userId
                        user.createDate = getDate(rs, "create_date")
                        user.endDate = getNullDate(rs, "end_date")
                        return Principal(user, readAuthorities(userId))
                    }
                }, login)!!
    }

    private fun readAuthorities(userId: Long): Set<Authority> {
        val authorities = jdbcTemplate.query("SELECT * FROM user_privileges_v WHERE id = ?",
                object : AbstractRowMapper<Authority>() {
                    @Throws(SQLException::class)
                    override fun mapRow(rs: ResultSet, i: Int): Authority {
                        return Codable.find(Authority::class.java, getString(rs, "privilege_code"))
                    }
                }, userId)

        return if (authorities.isEmpty()) {
            EnumSet.noneOf(Authority::class.java)
        } else {
            EnumSet.copyOf(authorities)
        }
    }
}