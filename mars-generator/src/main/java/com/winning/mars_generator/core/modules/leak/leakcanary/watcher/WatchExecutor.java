package com.winning.mars_generator.core.modules.leak.leakcanary.watcher;

/**
 * A {@link WatchExecutor} is in charge of executing a {@link Retryable} in the future, and retry
 * later if needed.
 */
public interface WatchExecutor {
  WatchExecutor NONE = new WatchExecutor() {
    @Override public void execute(Retryable retryable) {
    }
  };

  void execute(Retryable retryable);
}
