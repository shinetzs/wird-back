package com.example.util

import redis.clients.jedis.Jedis
import java.io.File

object RedisClient {


    private lateinit var jedis: Jedis

        fun init() {
            val host: String = EnvLoader.get("REDIS_HOST") { it }
            val port: Int = EnvLoader.get("REDIS_PORT") { it.toInt() }
            val password: String = EnvLoader.get("REDIS_PASSWORD") { it }

            jedis = Jedis(host, port).apply {
            auth(password)
        }
    }

    fun getClient(): Jedis {
        if (!::jedis.isInitialized) {
            init()
        }
        return jedis
    }
}
