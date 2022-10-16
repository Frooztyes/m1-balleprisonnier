package fr.icom.info.m1.balleauprisonnier_mvn;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.image.*;
import javafx.util.Duration;

import java.util.Objects;

class Sprite extends ImageView {
    private final Rectangle2D[] walkClips;
    private final Rectangle2D[] shootClips;
    private final Rectangle2D[] dieClips;
    private int numCells;
    private int numCellsWalk;
    private int numCellsShoot;
    private int numCellsDie;
    private final Timeline walkTimeline;
    private final IntegerProperty frameCounter = new SimpleIntegerProperty(0);
    private final Timeline shootTimeline;
    private final Timeline dieTimeline;
    private Timeline timeline;
    public boolean isRunning;

    public Sprite(Image animationImage, int numCells, int numRows, Duration frameTime, int side) {
        this.numCells = numCells;


        double cellWidth  = 64;//animationImage.getWidth() / numCells; //64x64
        double cellHeight = 64;//animationImage.getHeight() / numRows;


        numCellsWalk = 9;

        int lineNumber = 8;
        if(side == Const.SIDE_TOP){
            lineNumber += 2;
        }

        walkClips = new Rectangle2D[numCellsWalk];
        for (int i = 0; i < numCellsWalk; i++) {
            walkClips[i] = new Rectangle2D(
                    i * cellWidth, cellHeight*lineNumber,
                    cellWidth, cellHeight
            );
        }

        setImage(animationImage);
        setViewport(walkClips[0]);

        walkTimeline = new Timeline(
                new KeyFrame(frameTime, event -> {
                    frameCounter.set((frameCounter.get() + 1) % numCellsWalk);
                    setViewport(walkClips[frameCounter.get()]);
                })
        );

        numCellsShoot = 13;
        lineNumber += 8;

        shootClips = new Rectangle2D[numCellsShoot];
        for (int i = 0; i < numCellsShoot; i++){
            shootClips[i] = new Rectangle2D(
                    i * cellWidth, cellHeight*lineNumber,
                    cellWidth, cellHeight
            );
        }


        shootTimeline = new Timeline(
                new KeyFrame(Duration.seconds(.1), event -> {
                    frameCounter.set((frameCounter.get() + 1) % numCellsShoot);
                    setViewport(shootClips[frameCounter.get()]);
                }));

        timeline = walkTimeline;
        isRunning = false;

        lineNumber += 2;

        if(side == Const.SIDE_BOT){
            lineNumber += 2;
        }

        numCellsDie = 6;

        dieClips = new Rectangle2D[numCellsDie];
        for (int i = 0; i < numCellsDie; i++){
            dieClips[i] = new Rectangle2D(
                    i * cellWidth, cellHeight*lineNumber,
                    cellWidth, cellHeight
            );
        }

        dieTimeline = new Timeline(
                new KeyFrame(frameTime, event -> {
                    frameCounter.set((frameCounter.get() + 1) % numCellsDie);
                    setViewport(dieClips[frameCounter.get()]);
                }));
    }

    public void playContinuously() {
        isRunning = true;
        frameCounter.set(0);
        timeline = walkTimeline;
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.stop();
        timeline.playFromStart();
    }

    public void playShoot() {
        frameCounter.set(0);
        timeline.stop();
        timeline = shootTimeline;
        timeline.setCycleCount(numCellsShoot);
        timeline.setOnFinished(e -> playContinuously());
        timeline.playFromStart();
    }

    public void playDie() {
        frameCounter.set(0);
        timeline.stop();
        timeline = dieTimeline;
        timeline.setCycleCount(numCellsDie);
        timeline.playFromStart();
        frameCounter.set(numCellsDie - 1);
    }

    public void stop() {
        frameCounter.set(0);
        setViewport(walkClips[frameCounter.get()]);
        walkTimeline.stop();
    }

    public double getCellSize() {
        return 64;
    }
}
