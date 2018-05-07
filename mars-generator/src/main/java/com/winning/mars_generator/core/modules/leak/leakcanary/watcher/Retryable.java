package com.winning.mars_generator.core.modules.leak.leakcanary.watcher;

/** A unit of work that can be retried later. */
public interface Retryable {

  enum Result {
    DONE, RETRY
  }

  Result run();
}
