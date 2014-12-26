package sandbox;

/**
 * Created by i303874 on 12/26/14.
 */

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

import java.util.concurrent.TimeUnit;

/**
 * A sandboxed {@link ContextFactory} that prevents access to all native classes.
 */
public class SandboxContextFactory extends ContextFactory {
    private static final int OPS_QUANTUM = 10000;

    private final long maxTimeNanos;

    public SandboxContextFactory(long maxTime, TimeUnit timeUnit) {
        maxTimeNanos = timeUnit.toNanos(maxTime);
    }

    public static class TimeExceededException extends IllegalStateException {
        public TimeExceededException() {
            super();
        }
    }

    private static class TimeLimitedContext extends Context {
        public TimeLimitedContext(SandboxContextFactory timeLimitedContextFactory) {
            super(timeLimitedContextFactory);
        }

        private long startTime;
    }

    @Override
    public Context makeContext() {
        TimeLimitedContext context = new TimeLimitedContext(this);
        context.setInstructionObserverThreshold(OPS_QUANTUM);
        context.setWrapFactory(new SandboxWrapFactory());
        context.setClassShutter(new SandboxClassShutter());
        context.setInstructionObserverThreshold(OPS_QUANTUM);
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