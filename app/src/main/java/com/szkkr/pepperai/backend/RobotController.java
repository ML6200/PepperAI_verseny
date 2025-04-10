package com.szkkr.pepperai.backend;

import androidx.annotation.NonNull;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.HolderBuilder;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.holder.AutonomousAbilitiesType;
import com.aldebaran.qi.sdk.object.holder.Holder;
import com.aldebaran.qi.sdk.object.touch.TouchSensor;
import com.szkkr.pepperai.R;

import java.util.concurrent.ExecutionException;

public class RobotController
        extends RobotActivity
        implements RobotLifecycleCallbacks
{
    public Animation ROBOT_HAND_HOLD;
    private QiContext qiContext;
    private final SayManager sayManager = new SayManager();

    private TouchSensor headTouchSensor;
    private volatile Future<Say> sayFuture;
    private Animate animate;

    public TouchSensor getHeadTouchSensor() {
        return headTouchSensor;
    }

    public Future<Say> getSayFuture() {
        return sayFuture;
    }

    public Holder holder;

    @Override
    public void onRobotFocusGained(QiContext qiContext)
    {
        this.qiContext = qiContext;
        this.headTouchSensor = qiContext.getTouch().getSensor("Head/Touch");

        this.ROBOT_HAND_HOLD = AnimationBuilder
                .with(qiContext)
                .withResources(R.raw.robot_hand_hold)
                .build();

        holder = HolderBuilder
                .with(qiContext)
                .withAutonomousAbilities(AutonomousAbilitiesType.BACKGROUND_MOVEMENT)
                .build();

        headTouchSensor.addOnStateChangedListener(state ->
        {
            sayFuture.cancel(true);
            say("Ez jól esik. Szeretem, ha simogatják a fejem!");
        });
    }

    //-------------------------------FUNCTIONS--------------------------------------------

    public void holdBodyPose()
    {
        holder.hold();
    }

    public void releaseBodyPose()
    {
        holder.release();
    }

    @Override
    public void onRobotFocusLost()
    {
        //NO NEED TO IMPLEMENT
    }

    @Override
    public void onRobotFocusRefused(String reason)
    {
        //NO NEED TO IMPLEMENT
    }

    public void execSay(String text, ExecuteEndedListener listener) {
        if (qiContext == null) {
            System.err.println("QiContext is null. Cannot execute speech.");
            if (listener != null) {
                listener.onExecuteEnded();
            }
            return;
        }

        // Store the future reference so it can be cancelled if needed
        sayFuture = say(text, listener);
    }


    //#####################################HANDLING_ANIMATION####################################

    @NonNull
    public Future<Say> say(String text) {
        return say(text, null);
    }

    @NonNull
    public Future<Say> say(String text, ExecuteEndedListener listener)
    {
        Future<Say> sayFuture = SayBuilder
                .with(qiContext)
                .withText(text)
                .buildAsync();

        // Process the future result correctly
        sayFuture.thenConsume(future -> {
            try {
                if (future.isSuccess()) {
                    // Get the actual Say object from the future
                    Say say = future.getValue();

                    // Run the say action
                    Future<Void> runFuture = say.async().run();

                    // When the say action completes, notify the listener
                    runFuture.thenConsume(runResult -> {
                        if (listener != null) {
                            listener.onExecuteEnded();
                        }
                    });
                } else {
                    System.err.println("Failed to build Say: " + future.getErrorMessage());
                    if (listener != null) {
                        listener.onExecuteEnded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onExecuteEnded();
                }
            }
        });

        return sayFuture;
    }

    public void animateRobot(Animation animation, ExecuteEndedListener listener)
    {
        animate = AnimateBuilder
                .with(qiContext)
                .withAnimation(animation)
                .build();

        animate.async().run();

        listener.onExecuteEnded();
    }

    public ListenResult versenyListener(ExecuteEndedListener listener)
    {
        PhraseSet setThanks = PhraseSetBuilder
                .with(qiContext)
                .withTexts("köszönjük",
                        "köszönjük szépen",
                        "nagyon köszönjük")
                .build();

        Listen listen = ListenBuilder.with(qiContext)
                .withPhraseSets(setThanks)
                .build();
        ListenResult result = listen.run();
        listener.onExecuteEnded();

        return result;
    }



    //#####################################DEPRICATED_DEPENDENCIES###################################################
    @Deprecated
    public final RobotOperation<String> SAY_ROBOT_OPERATION = new RobotOperation<String>()
    {
        @Override
        public Future callMethod(String... params)
        {
            /*
            Future<Say> sayFuture = SayBuilder.with(qiContext).withText(params[0]).buildAsync();
            try
            {
                sayFuture.get().run();
            } catch (ExecutionException e)
            {
                throw new RuntimeException(e);
            }

             */

            return sayManager.executeSay(params[0]);
        }
    };

    @Deprecated
    public <P> void execute(RobotOperation robotOperation, ExecuteEndedListener listener, P... params) {
        // Cancel any existing operation first
        if (sayManager.isExecutePending) {
            sayManager.cancelCurrentOperation();
        }

        try {
            Future future = robotOperation.callMethod(params);
            future.andThenConsume(f -> {
                if (listener != null) {
                    listener.onExecuteEnded();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    class SayManager {
        private Future<Say> sayFuture;
        private volatile boolean isExecutePending = false;

        void cancelCurrentOperation() {
            if (sayFuture != null) {
                sayFuture.cancel(true);
                isExecutePending = false;
            }
        }

        Future<Say> executeSay(String message) {
            cancelCurrentOperation();

            sayFuture = SayBuilder.with(qiContext).withText(message).buildAsync();
            startExec();
            return sayFuture;
        }

        void startExec() {
            try {
                isExecutePending = true;
                sayFuture.get().run();
                isExecutePending = false;
            } catch (ExecutionException e) {
                isExecutePending = false;
                throw new RuntimeException(e);
            }
        }
    }
}