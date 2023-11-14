package com.example.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Iterator;
import java.util.List;

import moxy.MvpView;
import moxy.viewstate.ViewCommand;
import moxy.viewstate.strategy.OneExecutionStateStrategy;
import moxy.viewstate.strategy.StateStrategy;
import moxy.viewstate.strategy.StateStrategyType;

public class AddToEndSingleByTagStateStrategy implements StateStrategy {

    @Override
    public <View extends MvpView> void beforeApply(List<ViewCommand<View>> currentState, ViewCommand<View> incomingCommand) {
        Iterator<ViewCommand<View>> iterator = currentState.iterator();

        while (iterator.hasNext()) {
            ViewCommand<View> entry = iterator.next();

            if (entry.getTag().equals(incomingCommand.getTag())) {
                iterator.remove();
                break;
            }
        }

        currentState.add(incomingCommand);
    }

    @Override
    public <View extends MvpView> void afterApply(List<ViewCommand<View>> currentState, ViewCommand<View> incomingCommand) {
        // pass
    }
}

