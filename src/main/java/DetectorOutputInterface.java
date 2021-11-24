import io.github.edmm.model.DeploymentModel;

import java.io.BufferedWriter;
import java.io.IOException;

public interface DetectorOutputInterface {
    public double getScore();
    public void write(BufferedWriter bw) throws IOException;
}
