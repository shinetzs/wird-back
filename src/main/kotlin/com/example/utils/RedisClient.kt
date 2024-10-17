package com.example.util

import redis.clients.jedis.Jedis

object RedisClient {
    private lateinit var jedis: Jedis

//    fun init(redisUrl: String, password: String) {
        fun init() {
            jedis = Jedis("redis-17927.c82.us-east-1-2.ec2.redns.redis-cloud.com", 17927).apply {
            auth("yXctNX45yo2MR3DcgDe1iJzndVvoxqsb")
        }
    }

    fun getClient(): Jedis = jedis
}
