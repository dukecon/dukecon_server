#!/usr/bin/env groovy
@Library('jenkins-library@master') _

pipeline {
    agent {
        node {
            label 'docker'
        }
    }

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5', daysToKeepStr: '5'))
    }

    triggers {
        pollSCM('*/3 * * * *')
    }

    stages {
        stage('Build') {
            steps {
                withMaven {
                    script {
                        if (env.BRANCH_NAME == "develop") {
                            sh './mvnw -Pdocker,doc clean install'
                            publishHTML target: [allowMissing         : false,
                                                 alwaysLinkToLastBuild: false,
                                                 keepAll              : true,
                                                 reportDir            : 'impl/target/generated-docs/html/',
                                                 reportFiles          : 'index.html',
                                                 reportName           : 'SwaggerDocumentation']
                        } else if (env.BRANCH_NAME == "feature/102-static-data") {
                            sh 'mvn clean install'
                        } else {
                            sh './mvnw clean verify'
                        }
                    }
                }
                findBuildScans()
            }
        }
        stage('Docker Push and Restart "latest"') {
            steps {
                withMaven {
                    script {
                        if (env.BRANCH_NAME == "develop") {
                            sh './mvnw -Pdocker docker:push'
                            build 'docker_restart_develop_latest'
                            build 'docker_restart_latest-static'
                        } else {
                            echo 'No Docker action required'
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            sendNotification currentBuild.result
        }
        failure {
            // notify users when the Pipeline fails
            mail to: 'gerd@aschemann.net',
                    subject: "Failed DukeCon Server Pipeline: ${currentBuild.fullDisplayName}",
                    body: "Something is wrong with ${env.BUILD_URL}"
        }
    }
}
