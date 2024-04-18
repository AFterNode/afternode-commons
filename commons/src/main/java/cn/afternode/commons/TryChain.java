package cn.afternode.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class TryChain {
    private final List<Runnable> chain = new ArrayList<>();

    private BiConsumer<Runnable, Throwable> onError = null;
    private Runnable onFail = null;
    private Runnable onSuccess = null;

    private boolean continueOnSuccess = false;

    /**
     * @param chain Runnable objects to try
     */
    public TryChain(Runnable... chain) {
        this.chain.addAll(Arrays.asList(chain));
    }

    /**
     * @param continueOnSuccess Keep running on successful
     * @param chain Runnable objects to try
     */
    public TryChain(boolean continueOnSuccess, Runnable... chain) {
        this(chain);
        this.continueOnSuccess = continueOnSuccess;
    }

    /**
     * @param chain Runnable objects to try
     */
    public TryChain(List<Runnable> chain) {
        this.chain.addAll(chain);
    }

    /**
     * @param continueOnSuccess Keep running on successful
     * @param chain Runnable objects to try
     */
    public TryChain(boolean continueOnSuccess, List<Runnable> chain) {
        this(chain);
        this.continueOnSuccess = continueOnSuccess;
    }

    /**
     * @param continueOnSuccess Keep running on successful
     */
    public TryChain(boolean continueOnSuccess) {
        this.continueOnSuccess = continueOnSuccess;
    }

    public TryChain() {}

    /**
     * Set listener on error
     * @param onError Listener
     */
    public void setOnError(BiConsumer<Runnable, Throwable> onError) {
        this.onError = onError;
    }

    /**
     * Set listener on all tries failed
     * @param onFail Listener
     */
    public void setOnFail(Runnable onFail) {
        this.onFail = onFail;
    }

    /**
     * Set listener on one runnable success
     * @param onSuccess Listener
     */
    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    /**
     * Set is keep running on successful
     * @param continueOnSuccess Value
     */
    public void setContinueOnSuccess(boolean continueOnSuccess) {
        this.continueOnSuccess = continueOnSuccess;
    }

    /**
     * Is keep running on successful
     * @return Value
     */
    public boolean isContinueOnSuccess() {
        return continueOnSuccess;
    }

    /**
     * Add to tries
     * @param run Runnable object
     */
    public void add(Runnable run) {
        this.chain.add(run);
    }

    /**
     * Start trying
     * @return Result
     */
    public boolean start() {
        boolean success = false;

        for (Runnable run : chain) {
            try {
                run.run();
                success = true;

                try {   // Execute onSuccess
                    onSuccess.run();
                } catch (Throwable t) {
                    throw new RuntimeException("Error in onSuccess", t);
                }

                if (!continueOnSuccess) break;  // Break if not continueOnSuccess
            } catch (Throwable t) { // Execute onError
                onError.accept(run, t);
            }
        }

        if (!success) onFail.run(); // Failed

        return success;
    }
}
