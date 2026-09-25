// ============================================
// Mainu-CICD | Single Microservice Template
// ============================================
// Replace all <PLACEHOLDER> values before use

pipeline {
    agent any

    parameters {
        string(name: 'branch',      defaultValue: 'main',   description: 'Git branch to build')
        string(name: 'version',     defaultValue: 'latest', description: 'Base image tag')
        string(name: 'releaseTag',  defaultValue: 'v1',     description: 'Retag version')
        string(name: 'environment', defaultValue: 'dev',    description: 'Environment prefix')
    }

    environment {
        ECR_ACCOUNT = "<YOUR_AWS_ACCOUNT_ID>"
        ECR_REGION  = "<YOUR_AWS_REGION>"
        SERVICE     = "<YOUR_SERVICE_NAME>"
        REGISTRY    = "${ECR_ACCOUNT}.dkr.ecr.${ECR_REGION}.amazonaws.com"
    }

    stages {

        stage('SCM Checkout') {
            steps {
                git branch: "${params.branch}",
                    url: "<YOUR_GIT_REPO_URL>",
                    credentialsId: "git-credentials-id"
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t ${SERVICE} .'
                sh 'docker tag ${SERVICE}:latest ${REGISTRY}/${SERVICE}:${params.environment}${params.version}'
            }
        }

        stage('Push Image') {
            steps {
                sh 'aws ecr get-login-password --region ${ECR_REGION} | docker login --username AWS --password-stdin ${REGISTRY}'
                sh 'docker push ${REGISTRY}/${SERVICE}:${params.environment}${params.version}'
            }
        }

        stage('Retag ECR') {
            steps {
                sh 'aws ecr batch-get-image --region ${ECR_REGION} --repository-name ${SERVICE} --image-ids imageTag=${params.environment}${params.version} --query "images[].imageManifest" --output text > /tmp/manifest.json'
                sh 'aws ecr put-image --region ${ECR_REGION} --repository-name ${SERVICE} --image-tag ${params.environment}${params.releaseTag} --image-manifest file:///tmp/manifest.json || true'
            }
        }
    }

    post {
        success { echo "SUCCESS: ${SERVICE} pushed and retagged" }
        failure { echo "FAILED:  ${SERVICE}" }
    }
}
