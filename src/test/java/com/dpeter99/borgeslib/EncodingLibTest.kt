package com.dpeter99.borgeslib

import it.unimi.dsi.fastutil.booleans.BooleanArrayList
import it.unimi.dsi.fastutil.booleans.BooleanBigLists
import it.unimi.dsi.fastutil.booleans.BooleanList
import org.apache.commons.lang3.BitField
import org.junit.jupiter.api.Assertions.*
import java.math.BigInteger

class EncodingLibTest {

    val encoder = EncodingLib(0,100);

    @org.junit.jupiter.api.Test
    fun validateEncode() {
        val size = 100;

        val check = BooleanArrayList(size).apply {
            this.size(size)
            this.fill(false);
        }


        for (i in 0 until size){
            val result = encoder.encode(BigInteger.valueOf(i.toLong()),0);
            val res = result.toInt()

            assertTrue(!check.getBoolean(res), "Value \"$res\" was encoded multiple times");

            check.set(res, true);
        }
    }
}