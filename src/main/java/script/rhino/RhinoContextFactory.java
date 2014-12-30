package script.rhino;

/**
 * Created by i303874 on 12/26/14.
 */

import org.mozilla.javascript.*;

import java.util.concurrent.TimeUnit;

/**
 * A sandboxed {@link ContextFactory} that prevents access to all native classes.
 */
public class RhinoContextFactory extends ContextFactory {
    private static final int OPS_QUANTUM = 10000;

    private final long maxTimeNanos;

    public RhinoContextFactory(long maxTime, TimeUnit timeUnit) {
        maxTimeNanos = timeUnit.toNanos(maxTime);
    }

    public static class TimeExceededException extends IllegalStateException {
        public TimeExceededException() {
            super();
        }
    }

    private static class TimeLimitedContext extends Context {
        public TimeLimitedContext(RhinoContextFactory timeLimitedContextFactory) {
            super(timeLimitedContextFactory);
        }

        private long startTime;
    }

    @Override
    public Context makeContext() {
        TimeLimitedContext context = new TimeLimitedContext(this);
        context.setOptimizationLevel(-1);
        context.setMaximumInterpreterStackDepth(1000);
        context.setInstructionObserverThreshold(OPS_QUANTUM);
        context.setWrapFactory(new RhinoWrapFactory());
        context.setClassShutter(new RhinoClassShutter());
        context.seal(null);
        return context;
    }

    /**
     * Record start time in context.
     */
    @Override
    protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        TimeLimitedContext tlcx = (TimeLimitedContext) cx;
        tlcx.startTime = System.nanoTime();
        return super.doTopCall(callable, tlcx, scope, thisObj, args);
    }

    /**
     * Enforce time restrictions.
     */
    @Override
    protected void observeInstructionCount(Context cx, int instructionCount) {
        TimeLimitedContext tlcx = (TimeLimitedContext) cx;
        long currentTime = System.nanoTime();
        long durationNanos = currentTime - tlcx.startTime;
        if (durationNanos > maxTimeNanos) {
            throw new TimeExceededException();
        }
    }
}