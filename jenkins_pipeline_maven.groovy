pipeline {
    agent any
    triggers {
        cron 'H/3 * * * *'
    }
    
    parameters {
      booleanParam defaultValue: false, description: '', name: 'SKIP_TEST'
    }


    tools {
      maven 'Maven'
      jdk 'JDK11'
    }
    stages {
        stage('Checkout') {
            steps {
                cleanWs()
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/JanataPavel/flight-log.git']]])
            }
        }
        stage('Build') {
            steps {
                dir('.') {sh "mvn --batch-mode clean install -DskipTests"}
            }
        }
        stage('Test') {
            when {
                not {
                    expression { params.SKIP_TESTS}
                }
            }
            steps {
                dir('.') {
                    script {try {sh "mvn --batch-mode test -Dmaven.test.failure.ignore=true"} finally {junit '**/target/surefire-reports/*.xml'}
                    }
                }
            }
        }
    }
}
