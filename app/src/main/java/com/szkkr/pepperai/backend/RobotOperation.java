package com.szkkr.pepperai.backend;

import com.aldebaran.qi.Future;

@FunctionalInterface
interface RobotOperation<P>
{
    Future callMethod(P... params);
}