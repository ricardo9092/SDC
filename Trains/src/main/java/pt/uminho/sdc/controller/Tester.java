package pt.uminho.sdc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spread.SpreadException;

import java.util.Random;

public class Tester {
    private static Logger logger = LoggerFactory.getLogger(Tester.class);

    private final ContollerSupplier supplier;
    private final long time;
    private final Random random;
    private Worker[] worker;
    private Stage stage = Stage.Warmup;
    private int nops;
    private long totalrtt;
    private int totalop;
    private int[] trainsTerminated;

    @FunctionalInterface
    public interface ContollerSupplier {
        Controller get() throws Exception;
    }

    public Tester(ContollerSupplier supplier, int nthr, long seconds) {
        this.worker = new Worker[nthr];
        this.trainsTerminated = new int[nthr];
        this.supplier = supplier;
        this.time = seconds*1000;
        this.random = new Random();
        logger.info("testing controller implementation: threads = {}, seconds = {}", nthr, seconds);
    }

    public Tester(ContollerSupplier supplier, String[] args) {
        int nthr = 1;
        if (args.length >= 1)
            nthr = Integer.parseInt(args[0]);
        this.worker = new Worker[nthr];
        this.trainsTerminated = new int[nthr];
        this.supplier = supplier;
        int seconds = 10;
        if (args.length >= 2)
            seconds = Integer.parseInt(args[1]);
        this.time = seconds*1000;
        this.random = new Random();
        logger.info("testing controller implementation: threads = {}, seconds = {}", nthr, seconds);
    }

    public void test() throws InterruptedException, SpreadException {
        int initial;

        try {
            logger.debug("connected to controller");
            initial = 0 ;
        } catch(Exception e) {
            logger.error("cannot get initial balance: test aborted", e);
            return;
        }

        for(int i=0; i<worker.length; i++) {
            int linha = generateRandomLinha();
            int entryPoint = generateRandomStartPoint(linha);
            int leavePoint = generateRandomEndPoint(linha, entryPoint);
            trainsTerminated[i] = 0;
            worker[i] = new Worker(linha, entryPoint, leavePoint, i);
        }
        for(int i=0; i<worker.length; i++) {
            worker[i].start();
        }

        if (!waitInStage(Stage.Warmup, time/10)) {
            logger.error("test aborted during warmup");
        }

        long before = System.nanoTime();

        logger.info("warmup complete: running!");

        setStage(Stage.Run);

        if (!waitInStage(Stage.Run, time)) {
            logger.error("test aborted during measurement");
        }

        setStage(Stage.Shutdown);

        long after = System.nanoTime();

        logger.info("complete: shutting down");


        for(int i=0; i<worker.length; i++) {
            worker[i].join();
        }

        if (stage != Stage.Shutdown) {
            logger.error("test aborted");
            return;
        }

        logger.info("performance: {} ops, {} ops/s, {} s", nops, nops/((after-before)/1e9d), (totalrtt/1e9d)/nops);

        int failedTrain = -1;
        boolean testTrains = true;

        for(int i=0; i<worker.length; i++) {
            if(!worker[i].arrivedAtDestination()){
                testTrains = false;
                failedTrain = i;
            }
        }

        if(testTrains)
            logger.info("test PASSED: all trains arrived at destination");
        else
            logger.error("test FAILED: train " + failedTrain + " was stuck in segemento " + worker[failedTrain].getCurrentPostion() + " of linha " + worker[failedTrain].getLinha() + " was supposed to stop in segmento " + worker[failedTrain].getDestinarion());

    }

    private static enum Stage { Warmup, Run, Shutdown, Error };

    private synchronized void setStage(Stage stage) {
        if (stage.compareTo(this.stage) <= 0)
            return;
        this.stage = stage;
    }

    private synchronized boolean waitInStage(Stage stage, long time) throws InterruptedException {
        long now = System.currentTimeMillis();
        long target = now + time;
        while(this.stage == stage && now < target) {
            wait(target-now);
            now = System.currentTimeMillis();
        }
        return this.stage == stage;
    }

    private synchronized void log(long delta) {
        System.out.println("Stage = " + stage);
        if (stage != Stage.Run)
            return;

        nops++;
        totalrtt+=delta;
    }

    private synchronized boolean isRunning() {
        return stage.compareTo(Stage.Run) <= 0;
    }

    private class Worker extends Thread {
        private int linha;
        private int entryPoint;
        private int leavePoint;
        private int currentPostion;
        private boolean entered;

        public Worker(int linha, int entryPoint, int leavePoint, int trainPosition){
            this.linha = linha;
            this.entryPoint = entryPoint;
            this.leavePoint = leavePoint;
            this.currentPostion = this.entryPoint;
            this.entered = false;
        }

        public boolean arrivedAtDestination(){
            if(currentPostion == leavePoint){
                return true;
            }
            return false;
        }

        public int getDestinarion(){
            return leavePoint;
        }

        public int getCurrentPostion(){
            return currentPostion;
        }
        public int getLinha(){
            return linha;
        }

        public void run() {
            try {
                Controller controller = supplier.get();

                logger.debug("worker connected to controller");
                while (currentPostion < leavePoint) {
                    long before = System.nanoTime();
                    if (entered) {
                        controller.requestEntry(linha, currentPostion + 1);
                        //bank.setOccupied(linha, currentPostion + 1);
                        controller.setAvailable(linha, currentPostion);
                        currentPostion = currentPostion + 1;
                        long after = System.nanoTime();
                        log(after - before);
                    } else {
                        controller.requestEntry(linha, entryPoint);
                        //bank.setOccupied(linha, currentPostion);
                        entered = true;
                        long after = System.nanoTime();
                        log(after - before);
                    }
                }
                //***** Free the last station in case other trains want to come to this segment **** //
                controller.setAvailable(linha,currentPostion);
            } catch(Exception e) {
                logger.error("worker stopping on exception", e);
                setStage(Stage.Error);
            }
        }
    }


    public int generateRandomLinha(){
        int linha = random.nextInt(3)+1;
        return linha;
    }

    public int generateRandomStartPoint(int linha){
        int StartPoint = 100;
        switch(linha){
            case 1:
                while(StartPoint >= 5)
                    StartPoint = random.nextInt(6);
                break;
            case 2:
                while(StartPoint >= 7)
                    StartPoint = random.nextInt(8);
                break;
            case 3:
                while(StartPoint >= 10)
                    StartPoint = random.nextInt(11);
                break;
        }
        return StartPoint;
    }

    public int generateRandomEndPoint(int linha, int startPoint){
        int EndPoint = 0;
        switch(linha){
            case 1:
                while(EndPoint <= startPoint)
                    EndPoint = random.nextInt(6);
                break;
            case 2:
                while(EndPoint <= startPoint)
                    EndPoint = random.nextInt( 8);
                break;
            case 3:
                while(EndPoint <= startPoint)
                    EndPoint = random.nextInt(11);
                break;
        }
        return EndPoint;
    }
}
