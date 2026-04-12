package com.github.breadmoirai.oneclickcrafting.operation;

@FunctionalInterface
public interface DeferredOperation {
   DeferredOperation onNextUpdate();
}
