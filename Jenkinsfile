pipeline {
    agent any

    environment {
        SPRING_PROFILES_ACTIVE = "jenkins"
    }

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

stages {
        stage('Build & Test') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'POSTGRES_CREDENTIALS',
                    usernameVariable: 'DB_USER',
                    passwordVariable: 'DB_PASS'
                )]) {

                    bat """
                    set SPRING_DATASOURCE_USERNAME=%DB_USER%
                    set SPRING_DATASOURCE_PASSWORD=%DB_PASS%

                    gradlew.bat clean test
                    """
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
