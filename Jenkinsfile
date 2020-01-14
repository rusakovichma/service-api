#!groovy

node {

    load "$JENKINS_HOME/jobvars.env"

    stage('JDK') {
        sh 'sudo update-java-alternatives -s java-11-openjdk-amd64'
        sh 'java -version'
        sh 'javac -version'
        sh 'echo $JAVA_HOME'
    }

    stage('Checkout') {
        checkout scm
    }
    stage('Test') {
        sh './gradlew clean test --full-stacktrace'
    }
    stage('Build') {
        sh './gradlew build'
    }
    stage('Docker image') {
        sh "./gradlew buildDocker -P dockerServerUrl=$DOCKER_HOST"
    }
    stage('Deploy container') {
        docker.withServer("$DOCKER_HOST") {
            sh "docker-compose -p reportportal51 -f $COMPOSE_FILE_RP_5_1 up -d --force-recreate api"
        }
    }
}