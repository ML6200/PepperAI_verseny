package com.szkkr.pepperai.backend;

import android.app.Activity;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class RobotController extends RobotActivity implements RobotLifecycleCallbacks
{
    private Activity activity;

    public RobotController(Activity activity)
    {
        this.activity = activity;
        QiSDK.register(this.activity, this);
    }

    public void init()
    {

    }

    @Override
    public void onRobotFocusGained(QiContext qiContext)
    {

    }

    @Override
    public void onRobotFocusLost()
    {
        QiSDK.unregister(this.activity);
    }

    @Override
    public void onRobotFocusRefused(String reason)
    {

    }
}

class RobotUtil
{
    public static void say(QiContext qiContext, Phrase phrase)
    {
        Say say = SayBuilder.with(qiContext)
                .withPhrase(phrase)
                .build();
        say.run();
    }

    public static void sayAsync(QiContext qiContext, Phrase phrase)
    {
        Say say = SayBuilder.with(qiContext)
                .withPhrase(phrase)
                .build();
        say.async().run();
    }
}
