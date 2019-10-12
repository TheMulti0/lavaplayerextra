/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.spotify.models;

/**
 * Model class that has the useful properties to customize multi threaded operations
 */
public class MultiThreadingOptions {
    /**
     * The maximum amount of threads allocated / core pool size of the search ThreadPoolExecutor, if smaller than 1, set to the default maximum amount (size of the collection)
     */
    public final int poolSize;
    /**
     * Amount of search tasks executed in a single thread
     */
    public final int operationsPerThread;

    /**
     * @param poolSize The maximum amount of threads allocated / core pool size of the search ThreadPoolExecutor, if smaller than 1, set to the default maximum amount (size of the collection)
     * @param operationsPerThread Amount of search tasks executed in a single thread
     */
    public MultiThreadingOptions(int poolSize, int operationsPerThread) {
        this.poolSize = poolSize;
        this.operationsPerThread = operationsPerThread;
    }
}
