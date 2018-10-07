package com.codylund.onestep.utils

import com.codylund.onestep.models.Step
import java.lang.StringBuilder

class StringUtils {
    companion object {
        fun isEmpty(string: String?): Boolean {
            return (string == null) || string.isEmpty()
        }
    }
}