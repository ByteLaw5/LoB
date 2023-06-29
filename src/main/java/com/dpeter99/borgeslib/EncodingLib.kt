package com.dpeter99.borgeslib

import java.math.BigInteger

class EncodingLib(seed: Long, size: Long) {


    // The max length of the output in bits;
    final val OUTPUT_SIZE = 256;


    /***
     * Req:
     * A) Each output is generated only once but every possible one is generated
     * B) Each output is deterministic
     * C) The distance between he outputs is large enough to seem random
     * D) The process is reversible
     *
     * @param base The number to transform
     * @param seed The world seed to ensure different worlds have different encodings
     */
    fun encode(base: BigInteger, seed: Long) : BigInteger{



        return base;
    }
}