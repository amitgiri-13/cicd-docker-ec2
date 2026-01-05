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
                    string(credentialsId: 'SSH_KEY64', variable: 'SSH_KEY64')
                ]) {
                    sh '''
                    set -e

                    KEY_PATH="$WORKSPACE/mykey.pem"

                    # Configure SSH
                    mkdir -p ~/.ssh
                    chmod 700 ~/.ssh
                    echo -e "Host *\\n\\tStrictHostKeyChecking no\\n" > ~/.ssh/config
                    chmod 600 ~/.ssh/config
                    touch ~/.ssh/known_hosts
                    chmod 600 ~/.ssh/known_hosts

                    # Decode SSH key (WRITE TO WORKSPACE)
                    echo "$SSH_KEY64" | base64 -d > "$KEY_PATH"
                    chmod 400 "$KEY_PATH"

                    # Remove old host key
                    ssh-keygen -R "$SERVER_IP" || true

                    # SSH into EC2 and deploy
                    ssh -i "$KEY_PATH" ${SSH_USER}@${SERVER_IP} << 'EOF'
                      set -e

                      APP_DIR="member-manager"
                      REPO_URL="https://github.com/amitgiri-13/cicd-docker-ec2.git"

                      if [ -d ~/$APP_DIR/.git ]; then
                        echo "Repo exists. Pulling latest changes..."
                        cd ~/$APP_DIR
                        git reset --hard
                        git pull origin main
                      else
                        echo "Repo doesn't exist. Cloning..."
                        git clone $REPO_URL $APP_DIR
                        cd ~/$APP_DIR
                      fi

                      docker compose pull
                      docker compose up -d --build
                    EOF
                    '''
                }
            }
        }
    }

    post {
        always {
            sh 'rm -f "$WORKSPACE/mykey.pem" || true'
        }
        success {
            echo "Deployment successful"
        }
        failure {
            echo "Deployment failed"
        }
    }
}
