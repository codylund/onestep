package com.codylund.onestep.views

import com.codylund.onestep.models.Path

interface MainView {
    fun showPathView(path: Path)
    fun delete(path: Path)
}