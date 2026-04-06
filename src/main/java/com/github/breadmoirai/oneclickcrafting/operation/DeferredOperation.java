package com.github.breadmoirai.oneclickcrafting.operation;

@FunctionalInterface
public interface DeferredOperation {
   public DeferredOperation onNextUpdate();
}
