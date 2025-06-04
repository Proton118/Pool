import java.io.FileWriter;
import java.io.IOException;

public class PoolSaveManager {
    String fileName;

    public PoolSaveManager(String fileName){
        this.fileName = fileName;
    }

    public void SavePoolGame(Ball cueBall, BallSetup ballSetup){
        StringBuilder saveData = new StringBuilder();
        saveData.append(cueBall.GetPosition().x).append(",")
                .append(cueBall.GetPosition().y).append(",")
                .append(cueBall.GetVelocity().x).append(",")
                .append(cueBall.GetVelocity().y).append(",")
                .append(cueBall.GetMass()).append("|");
        for (ICollider collider : ballSetup.GetBalls()) {
            Ball ball = (Ball) collider;
            saveData.append(ball.GetPosition().x).append(",")
                    .append(ball.GetPosition().y).append(",")
                    .append(ball.GetVelocity().x).append(",")
                    .append(ball.GetVelocity().y).append(",")
                    .append(ball.GetMass()).append("|");
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

    public void ClearSaveFile() {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write("");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}