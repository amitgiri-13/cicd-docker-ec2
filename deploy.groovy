pipeline {
    agent any

    environment {
        APP_DIR  = 'member-manager'
        REPO_URL = 'https://github.com/amitgiri-13/cicd-docker-ec2.git'
        SSH_USER = 'ubuntu'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Configure SSH & Deploy') {
            steps {
                withCredentials([
                    string(credentialsId: 'SERVER_IP', variable: 'SERVER_IP'),
                    file(credentialsId: 'SSH_KEY64', variable: 'SSH_KEY_FILE')
                ]) {
                    sh '''
                    set -e

                    chmod 400 "$SSH_KEY_FILE"

                    mkdir -p ~/.ssh
                    chmod 700 ~/.ssh
                    echo -e "Host *\\n\\tStrictHostKeyChecking no\\n" > ~/.ssh/config
                    chmod 600 ~/.ssh/config

                    ssh-keygen -R "$SERVER_IP" || true

                    ssh -i "$SSH_KEY_FILE" ${SSH_USER}@${SERVER_IP} << 'EOF'
                      set -e

                      APP_DIR  = 'member-manager'
                      REPO_URL = 'https://github.com/amitgiri-13/cicd-docker-ec2.git'

                      if [ -d ~/$APP_DIR/.git ]; then
                        cd ~/$APP_DIR
                        git reset --hard
                        git pull origin main
                      else
                        git clone $REPO_URL $APP_DIR
                        cd ~/$APP_DIR
                      fi

                      docker compose pull
                      docker compose up -d --build
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Deployment successful"
        }
        failure {
            echo "Deployment failed"
        }
    }
}
