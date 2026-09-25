// ============================================
// Mainu-CICD | Dependency Library Template
// ============================================
// For shared libraries that only need Build + Jar
// No Docker/ECR stages needed for library projects

pipeline {
    agent any

    parameters {
        string(name: 'branch', defaultValue: 'main', description: 'Git branch to build')
    }

    stages {

        stage('SCM Checkout') {
            steps {
                git branch: "${params.branch}",
                    url: "<YOUR_LIBRARY_REPO_URL>",
                    credentialsId: "git-credentials-id"
            }
        }

        stage('Build & Install') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

    }

    post {
        success { echo "SUCCESS: Library built and installed to local Maven repo" }
        failure { echo "FAILED:  Library build failed — check Maven logs above" }
    }
}
