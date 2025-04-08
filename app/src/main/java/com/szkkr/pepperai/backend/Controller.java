package com.szkkr.pepperai.backend;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.SpeechEngine;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;

import java.util.List;

public class Controller extends RobotActivity implements RobotLifecycleCallbacks
{
    private QiContext qiContext;
    private boolean hasFocus = false;



    @Override
    public void onRobotFocusGained(QiContext qiContext)
    {
        this.qiContext = qiContext;
        this.hasFocus = true;
    }

    @Override
    public void onRobotFocusLost()
    {
        this.hasFocus = false;
        this.qiContext = null;
    }

    @Override
    public void onRobotFocusRefused(String reason)
    {
        this.hasFocus = false;
    }

    //-------------------------------FUNCTIONS--------------------------------------------

    public void exec(String text, ExecuteEndedListener listener) {
        Say say = SayBuilder.with(qiContext).withText(text).build();
        // Execute asynchronously so that the main thread isn’t blocked.
        say.async().run().andThenConsume(future -> {

            if (listener != null) {
                listener.onExecuteEnded();
            }
        });
    }
}
