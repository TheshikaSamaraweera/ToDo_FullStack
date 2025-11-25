pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                echo 'STAGE 1: CHECKOUT'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'STAGE 2: BUILD'
                bat 'gradlew.bat clean build -x test'
            }
        }

        stage('Run Tests') {
            steps {
                echo 'STAGE 3: TESTING'
                bat 'gradlew.bat test --info'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                echo 'STAGE 4: PACKAGE'
                bat 'gradlew.bat bootJar'
                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            }
        }

        stage('Verify') {
            steps {
                echo 'STAGE 5: VERIFY'
                bat 'dir build\\libs'
            }
        }
    }

    post {
        success {
            echo ' Pipeline Success!'
        }
        failure {
            echo ' Pipeline Failed!'
        }
    }
}
