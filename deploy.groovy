pipeline {
    agent any

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

        // stage("Build and Push Image") {

        //     steps  {

        //         withCredentials([
        //         string(credentialsId: "DOCKER_HUB_PASSWORD", variable: "DOCKER_HUB_PASSWORD")
        //     ]
        //     ){
        //         sh '''
        //             set -e 
        //             echo "$DOCKER_HUB_PASSWORD" | docker login -u $DOCKER_HUB_USER --password-stdin
        //             docker build -t "$DOCKER_HUB_USER/$DOCKER_HUB_REPO:$TAG" .
        //             docker push "$DOCKER_HUB_USER/$DOCKER_HUB_REPO:$TAG"
        //         '''
        //     } 
        //     }       
        // }

        stage("Deploy To EC2") {

            steps {
                withCredentials([
                    file(credentialsId: "SSH_KEY64", variable: "SSH_KEY")
                ]) {
                    sh '''
                        set -e 
                        mkdir -p ~/.ssh
                        chmod 700 ~/.ssh
                        echo -e "HOST *\n\tStrictHostKeyChecking no\n" > ~/.ssh/config
                        chmod 600 ~/.ssh/config

                        echo "$SSH_KEY" > mykey.pem
                        chmod 400 mykey.pem
                        touch ~/.ssh/known_hosts
                        ssh-keygen -R "$SERVER_IP"

                        scp -i "./mykey.pem" ./docker-compose.yaml $SERVER_USER@$SERVER_IP:~/

                        ssh -i mykey.pem $SERVER_USER@$$SERVER_IP "
                        docker compose --env-file ./.env/dev_env pull
                        docker compose --env-file ./.env/dev_env down
                        docker compose --env-file ./.env/dev_env up -d
                        "
                    '''
                }
            }
        }

    }
}