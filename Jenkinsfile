pipeline {
    agent any

    environment {
        SPRING_PROFILES_ACTIVE = "jenkins"
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
        failure {
            echo ' Pipeline Failed!'
        }
    }
}
