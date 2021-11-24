import io.github.edmm.model.DeploymentModel;

public interface DetectorInterface {

    public DetectorOutputInterface calculateScore(DeploymentModel deploymentModel);

}
