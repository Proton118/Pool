import java.io.FileWriter;
import java.io.IOException;

import Utility.Vector;

/**
 * A class responsible for saving and loading the state of a pool game.
 */
public class PoolSaveManager {
    String fileName;

    /**
     * Constructor for PoolSaveManager.
     *
     * @param fileName The name of the file where the game state will be saved and
     *                 loaded from.
     */
    public PoolSaveManager(String fileName) {
        this.fileName = fileName;
    }

    private String[] loadSaveData() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(fileName);
            String content = java.nio.file.Files.readString(path);
            return content.split("\\|");
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    /**
     * Saves the current state of all balls in the pool game to a file
     *
     * @param cueBall       The cue ball in the game.
     * @param ballSetup     The setup of all balls on the table.
     * @param physicsEngine The physics engine managing the game state.
     */
    public void SavePoolGame(Ball cueBall, BallSetup ballSetup, PhysicsEngine physicsEngine) {
        StringBuilder saveData = new StringBuilder();
        saveData.append(cueBall.GetPosition().x).append(",")
                .append(cueBall.GetPosition().y).append(",")
                .append(cueBall.GetVelocity().x).append(",")
                .append(cueBall.GetVelocity().y).append(",")
                .append(physicsEngine.IsBallInPocket(cueBall) ? "1" : "0").append("|");
        for (ICollider collider : ballSetup.GetBalls()) {
            Ball ball = (Ball) collider;
            saveData.append(ball.GetPosition().x).append(",")
                    .append(ball.GetPosition().y).append(",")
                    .append(ball.GetVelocity().x).append(",")
                    .append(ball.GetVelocity().y).append(",")
                    .append(physicsEngine.IsBallInPocket(ball) ? "1" : "0").append("|");
        }
        saveData.deleteCharAt(saveData.length() - 1);
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(saveData.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the save file by overwriting it with an empty string.
     */
    public void ClearSaveFile() {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write("");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the save file is empty.
     *
     * @return if the save file is empty.
     */
    public boolean isSaveFileEmpty() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(fileName);
            return java.nio.file.Files.size(path) == 0;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Loads the cue ball from the save file and adds it to the physics engine if
     * not in a pocket.
     *
     * @param physicsEngine The physics engine managing the game state.
     * @return The loaded cue ball.
     */
    public Ball loadCueBall(PhysicsEngine physicsEngine) {
        String[] data = loadSaveData()[0].split(",");
        float x = Float.parseFloat(data[0]);
        float y = Float.parseFloat(data[1]);
        float vx = Float.parseFloat(data[2]);
        float vy = Float.parseFloat(data[3]);
        Ball cueBall = (Ball) new Ball(new Vector(x, y)).SetVelocity(new Vector(vx, vy));
        boolean inPocket = data[4].equals("1");
        if (!inPocket) {
            physicsEngine.AddCollider(cueBall);
        }
        return cueBall;
    }

    /**
     * Loads the ball setup from the save file and adds the balls to the physics
     * engine if not in a pocket.
     *
     * @param physicsEngine The physics engine managing the game state.
     * @return The loaded ball setup.
     */
    public BallSetup loadBallSetup(PhysicsEngine physicsEngine) {
        BallSetup ballSetup = new BallSetup(new Vector(0, 0));
        String[] data = loadSaveData();
        for (int i = 1; i < data.length; i++) {
            String[] ballData = data[i].split(",");
            float x = Float.parseFloat(ballData[0]);
            float y = Float.parseFloat(ballData[1]);
            float vx = Float.parseFloat(ballData[2]);
            float vy = Float.parseFloat(ballData[3]);
            ((Ball) ballSetup.GetBalls().get(i - 1)).SetVelocity(new Vector(vx, vy));
            ((Ball) ballSetup.GetBalls().get(i - 1)).SetPosition(new Vector(x, y));
            if (!ballData[4].equals("1")) {
                physicsEngine.AddCollider(ballSetup.GetBalls().get(i - 1));
            }
        }
        return ballSetup;
    }
}