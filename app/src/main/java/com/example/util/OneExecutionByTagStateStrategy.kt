package com.example.util

import android.util.Log
import moxy.MvpView
import moxy.viewstate.ViewCommand
import moxy.viewstate.strategy.StateStrategy

class OneExecutionByTagStateStrategy : StateStrategy {

    override fun <View : MvpView?> beforeApply(currentState: MutableList<ViewCommand<View>>, incomingCommand: ViewCommand<View>) {
        val iterator = currentState.iterator()

        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.tag == incomingCommand.tag) {
                iterator.remove()
                break
            }
        }

        currentState.add(incomingCommand)
    }

    override fun <View : MvpView?> afterApply(currentState: MutableList<ViewCommand<View>>, incomingCommand: ViewCommand<View>) {
        currentState.remove(incomingCommand)
    }
}