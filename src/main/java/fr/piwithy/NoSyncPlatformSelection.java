package fr.piwithy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static java.util.concurrent.TimeUnit.SECONDS;

public class NoSyncPlatformSelection implements Runnable {

    private RailwayStation railwayStation;
    private Semaphore aPlatforms;

    private static final Logger LOGGER = LogManager.getLogger(NoSyncPlatformSelection.class);

    public NoSyncPlatformSelection(RailwayStation railwayStation) {
        this.railwayStation = railwayStation;
        aPlatforms = new Semaphore(railwayStation.platforms.size());
    }

    @Override
    public void run() {
        LOGGER.info("Train " + Thread.currentThread().getName() + " on approach.");
        try {
            int current_platform;
            aPlatforms.acquire();
            current_platform = railwayStation.getFreePlatform();
            LOGGER.info("Train " + Thread.currentThread().getId() + " headed for platform ---> " + current_platform);
            Thread.sleep(1000); // Passengers IO
            LOGGER.info("Train " + Thread.currentThread().getId() + " leaving platform " + current_platform + " --->");
            this.railwayStation.platforms.put(current_platform, Boolean.TRUE);
            aPlatforms.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        RailwayStation railwayStation = new RailwayStation("Carhaix", 3);
        int n_trains = 9;
        ArrayList<Runnable> runnables = new ArrayList<>();
        NoSyncPlatformSelection platformSelection = new NoSyncPlatformSelection(railwayStation);
        for (int i = 0; i < n_trains; i++) {
            runnables.add(platformSelection);
        }
        ExecutorService executor = Executors.newFixedThreadPool(n_trains);
        for (Runnable r : runnables)
            executor.submit(r);

        try {
            executor.shutdown();
            executor.awaitTermination(10, SECONDS);
        } catch (InterruptedException e) {
            LOGGER.fatal("tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                LOGGER.fatal("canceling non-finished tasks");
            }
            executor.shutdownNow();
            LOGGER.info("Tasks finished");
        }
        LOGGER.info("This is the end !");
    }
}
