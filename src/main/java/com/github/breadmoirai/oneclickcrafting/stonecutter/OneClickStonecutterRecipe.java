package com.github.breadmoirai.oneclickcrafting.stonecutter;

import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;

import java.util.function.Predicate;

public record OneClickStonecutterRecipe(OneClickItemStack result, Predicate<OneClickItemStack> ingredient) {
   public static OneClickStonecutterRecipe EMPTY = new OneClickStonecutterRecipe(OneClickItemStack.EMPTY, unused1 -> false);
}
