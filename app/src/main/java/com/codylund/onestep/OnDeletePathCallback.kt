package com.codylund.onestep

import com.codylund.onestep.models.Path
import io.reactivex.Completable

interface OnDeletePathCallback {
    fun delete(path: Path) : Completable
}