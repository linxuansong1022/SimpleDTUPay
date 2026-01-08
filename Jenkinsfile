pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                // 直接运行测试，Quarkus 会自动处理应用的启动和关闭
                // -Djava.net.preferIPv4Stack=true 防止 IPv6 问题
                sh 'mvn test -Djava.net.preferIPv4Stack=true'
            }
        }
    }
    
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}