package com.codylund.onestep.utils

class StringUtils {
    companion object {
        fun isEmpty(string: String?) : Boolean {
            return (string == null) || string.isEmpty()
        }
    }
}