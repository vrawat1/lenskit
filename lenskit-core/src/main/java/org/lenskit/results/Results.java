/*
 * LensKit, an open source recommender systems toolkit.
 * Copyright 2010-2014 LensKit Contributors.  See CONTRIBUTORS.md.
 * Work on LensKit has been funded by the National Science Foundation under
 * grants IIS 05-34939, 08-08692, 08-12148, and 10-17697.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.lenskit.results;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;
import org.lenskit.api.Result;
import org.lenskit.api.ResultList;
import org.lenskit.api.ResultMap;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * Utility functions for working with results.
 */
public final class Results {
    private Results() {}

    /**
     * Create a new result with just and ID and a score.
     * @param id The ID.
     * @param score The score.
     * @return The result instance.
     */
    public static BasicResult create(long id, double score) {
        return new BasicResult(id, score);
    }

    /**
     * Create a basic result that has the same ID and score as another result (a basic copy of the result).
     *
     * @param r The result to copy.
     * @return A basic result with the same ID and score as `r`.  This may be the same object as `r`, since basic
     * results are immutable.
     */
    public static BasicResult basicCopy(Result r) {
        if (r instanceof BasicResult) {
            return (BasicResult) r;
        } else {
            return create(r.getId(), r.getScore());
        }
    }

    /**
     * Create a new result list.
     * @param results The results to include in the list.
     * @return The result list.
     */
    @Nonnull
    public static ResultList newResultList(@Nonnull List<? extends Result> results) {
        return new BasicResultList(results);
    }

    /**
     * Create a new result list.
     * @param results The results to include in the list.
     * @param <R> the result type
     * @return The result list.
     */
    @SafeVarargs
    @Nonnull
    public static <R extends Result> ResultList newResultList(R... results) {
        return new BasicResultList(Arrays.asList(results));
    }

    /**
     * Create a new result list.
     * @param results The results to include in the list.
     * @return The result list.
     */
    @Nonnull
    public static ResultMap newResultMap(@Nonnull List<? extends Result> results) {
        return new BasicResultMap(results);
    }

    /**
     * Create a new result list.
     * @param results The results to include in the list.
     * @param <R> the result type
     * @return The result list.
     */
    @SafeVarargs
    @Nonnull
    public static <R extends Result> ResultMap newResultMap(R... results) {
        return new BasicResultMap(Arrays.asList(results));
    }

    /**
     * Guava function that converts a result to a basic result.  This is just {@link #basicCopy(Result)} exposed as
     * a Guava {@link Function} for use in processing lists, etc.
     *
     * @return A function that maps results to basic (only score and ID) versions of them.
     */
    public static Function<Result,BasicResult> basicCopyFunction() {
        return BasicCopyFunction.INSTANCE;
    }

    /**
     * An equivalence relation that considers objects to be equal if they are equal after being converted to
     * basic results (that is, their IDs and scores are equal).
     * @return The equivalence relation.
     */
    public static Equivalence<Result> basicEquivalence() {
        return Equivalence.equals().onResultOf(basicCopyFunction());
    }

    /**
     * Get an ordering (comparator) that orders results by ID.
     * @return An ordering that orders results by ID (increasing).
     */
    public static Ordering<Result> idOrder() {
        return IdOrder.INSTANCE;
    }

    /**
     * Get an ordering (comparator) that orders results by score.
     * @return An ordering that orders results by score (increasing).
     */
    public static Ordering<Result> scoreOrder() {
        return ScoreOrder.INSTANCE;
    }

    private enum BasicCopyFunction implements Function<Result,BasicResult> {
        INSTANCE {
            @Override
            public BasicResult apply(Result result) {
                return basicCopy(result);
            }
        }
    }

    private static class IdOrder extends Ordering<Result> {
        private static final IdOrder INSTANCE = new IdOrder();
        @Override
        public int compare(Result left, Result right) {
            return Longs.compare(left.getId(), right.getId());
        }
    }

    private static class ScoreOrder extends Ordering<Result> {
        private static final ScoreOrder INSTANCE = new ScoreOrder();
        @Override
        public int compare(Result left, Result right) {
            return Doubles.compare(left.getScore(), right.getScore());
        }
    }
}
