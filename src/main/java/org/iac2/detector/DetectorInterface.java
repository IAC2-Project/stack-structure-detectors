package org.iac2.detector;

import io.github.edmm.model.DeploymentModel;

public interface DetectorInterface {

    public DetectorOutputInterface calculateScore(DeploymentModel deploymentModel);

}
