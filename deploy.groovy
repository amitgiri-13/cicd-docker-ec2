pipeline {
    agent any

    environment {
        SERVER_IP = 'YOUR_EC2_PUBLIC_IP'
        APP_DIR   = 'member-manager'
        REPO_URL = 'https://github.com/amitgiri-13/cicd-docker-ec2.git'
        SSH_USER = 'ubuntu'
    }

    stages {

        stage('Checkout Jenkinsfile Repo') {
            steps {
                checkout scm
            }
        }

        stage('Deploy to EC2 via SSH') {
            steps {
                sshagent(credentials: ['ec2-ssh-key']) {
                    sh '''
                    ssh -o StrictHostKeyChecking=no ${SSH_USER}@${SERVER_IP} << EOF

                      set -e

                      if [ -d ~/${APP_DIR}/.git ]; then
                        echo "Repo exists. Pulling latest changes..."
                        cd ~/${APP_DIR}
                        git reset --hard
                        git pull origin main
                      else
                        echo "Repo does not exist. Cloning..."
                        git clone ${REPO_URL} ${APP_DIR}
                        cd ~/${APP_DIR}
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
        success {
            echo "Deployment successful"
        }
        failure {
            echo "Deployment failed"
        }
    }
}
