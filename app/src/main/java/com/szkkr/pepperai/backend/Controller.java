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



    @Override
    public void onRobotFocusGained(QiContext qiContext)
    {
        this.qiContext = qiContext;
    }

    @Override
    public void onRobotFocusLost()
    {

    }

    @Override
    public void onRobotFocusRefused(String reason)
    {

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



    /*
    public class HumanAwarenessController
    {

        private HumanAwareness humanAwareness;
        private List<Human> humansAround;
        private Human engagedHuman;

        public HumanAwarenessController()
        {
            initializeHumanAwareness();
        }

        private void initializeHumanAwareness()
        {
            humanAwareness = qiContext.getHumanAwareness();
            if (humanAwareness != null)
            {
                humansAround = humanAwareness.getHumansAround();
                engagedHuman = humanAwareness.getEngagedHuman();
            } else
            {
                // Handle the case where humanAwareness is null
                // For example, log an error or throw an exception
                System.err.println("Error: HumanAwareness is null. Check if the service is available.");
            }
        }

        public HumanAwareness getHumanAwareness()
        {
            return humanAwareness;
        }

        public List<Human> getHumansAround()
        {
            return humansAround;
        }

        public Human getEngagedHuman()
        {
            return engagedHuman;
        }

        public void updateHumanAwareness()
        {
            if (humanAwareness != null)
            {
                humansAround = humanAwareness.getHumansAround();
                engagedHuman = humanAwareness.getEngagedHuman();
            } else
            {
                // Handle the case where humanAwareness is null
                // For example, log an error or throw an exception
                System.err.println("Error: HumanAwareness is null. Check if the service is available.");
            }
        }

        public void addOnHumansAroundChangedListener(HumanAwareness.OnHumansAroundChangedListener listener)
        {
            this.humanAwareness.addOnHumansAroundChangedListener(listener);
        }

        public void addOnEngagedHumanAgeChangedListener()
        {

        }
    }

    public interface ExecuteEndedListener
    {
        void onExecuteEnded();
    }
     */
}
