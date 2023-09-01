package com.razielez.chatgpt.app.infra.gpt

import org.springframework.stereotype.Component

@Component
open class ChatLoadBalanceImpl(
    private val chatGptProperties: ChatGptProperties,
) : ChatLoadBalance {
//    private var current = -1
//    private var gcd = 0
//    private var maxWeight = 0

    //private val weights: List<Int> = chatGptProperties.token.map { it.w!! }

//    init {
//        chatGptProperties.token.forEach {
//            gcd = gcd(gcd, it.w!!)
//            maxWeight = maxOf(maxWeight, it.w!!)
//        }
//    }

    override fun get(): Token {
        return chatGptProperties.token
//        while (true) {
//            current = (current + 1) % tokens.size
//            if (current == 0) {
//                maxWeight -= gcd
//                if (maxWeight <= 0) {
//                    maxWeight = tokens.maxByOrNull { it.w!! }?.w ?: 0
//                    if (maxWeight == 0) {
//                        throw IllegalStateException()
//                    }
//                }
//            }
//            if (weights[current] >= maxWeight) {
//                return tokens[current]
//            }
//        }
    }

//    private fun gcd(a: Int, b: Int): Int {
//        return if (b == 0) a else gcd(b, a % b)
//    }

}