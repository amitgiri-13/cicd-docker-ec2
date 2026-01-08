pipeline {
    agent {
        any
    }

    parameters {
        string(name: "SERVER_IP", description: "Public ip of EC2")
    }

    environment {
        SERVER_USER = "ubuntu"
        DOCKER_HUB_USER = "amitgiri13"
        DOCKER_HUB_REPO = "manage-members"
        TAG = "latest"
    }

    stages {

        stage("Build Image") {
            
            steps {
                checkout scm
            }

            steps  {
                withCredentials([
                string(credentialsId: "DOCKER_HUB_PASSWORD", variable: "DOCKER_HUB_PASSWORD")
            ]
            ){
                sh """
                    set -e 
                    echo "$DOCKER_HUB_PASSWORD" | docker login -u $DOCKER_HUB_USER --password-stdin
                    docker build -t $DOCKER_HUB_USER/$DOCKER_HUB_REPO:$TAG
                    docker push "$DOCKER_HUB_USER/$DOCKER_HUB_REPO:$TAG"
                """
            } 
            }       
        }

        stage("")
    }
}