// ============================================
// Mainu-CICD | Jenkins Job DSL Seed Script
// ============================================

def ECR_ACCOUNT = "<YOUR_AWS_ACCOUNT_ID>"
def ECR_REGION  = "<YOUR_AWS_REGION>"
def GIT_BASE    = "<YOUR_GIT_REPO_BASE_URL>"
def GIT_CRED    = "git-credentials-id"

def services = [
    "microservice-user-auth",
    "microservice-notification-service",
    "microservice-payment-gateway"
]

def ecrOverride = [:]

services.each { svc ->

    def ecrRepo  = ecrOverride.containsKey(svc) ? ecrOverride[svc] : svc.toLowerCase()
    def ecrFile  = ecrRepo.replace(".", "_")
    def registry = "${ECR_ACCOUNT}.dkr.ecr.${ECR_REGION}.amazonaws.com"

    pipelineJob(svc) {
        description("CI/CD Pipeline for ${svc}")
        logRotator { numToKeep(10) }

        parameters {
            stringParam('branch',      'main',   'Git branch to build')
            stringParam('version',     'latest', 'Base image tag')
            stringParam('releaseTag',  'v1',     'Retag version')
            stringParam('environment', 'dev',    'Environment prefix')
        }

        definition {
            cps {
                sandbox(true)
                def pipelineScript = """
pipeline {
    agent any
    stages {
        stage('SCM Checkout') {
            steps {
                git branch: "\${branch}",
                    url: '${GIT_BASE}/${svc}',
                    credentialsId: '${GIT_CRED}'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker build -t ${ecrRepo} .'
                sh 'docker tag ${ecrRepo}:latest ${registry}/${ecrRepo}:\${environment}\${version}'
            }
        }
        stage('Push Image') {
            steps {
                sh 'aws ecr get-login-password --region ${ECR_REGION} | docker login --username AWS --password-stdin ${registry}'
                sh 'docker push ${registry}/${ecrRepo}:\${environment}\${version}'
            }
        }
        stage('Retag ECR') {
            steps {
                sh 'aws ecr batch-get-image --region ${ECR_REGION} --repository-name ${ecrRepo} --image-ids imageTag=\${environment}\${version} --query "images[].imageManifest" --output text > /tmp/${ecrFile}.json'
                sh 'aws ecr put-image --region ${ECR_REGION} --repository-name ${ecrRepo} --image-tag \${environment}\${releaseTag} --image-manifest file:///tmp/${ecrFile}.json || true'
            }
        }
    }
    post {
        success { echo "SUCCESS: ${svc} pushed and retagged" }
        failure { echo "FAILED:  ${svc}" }
    }
}
"""
                script(pipelineScript)
            }
        }
    }
}
