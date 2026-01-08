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
                script {
                    try {
                        // 1. 启动 Quarkus (后台)
                        // 使用 -Dquarkus.http.host=0.0.0.0 确保绑定所有网卡
                        sh 'nohup mvn quarkus:dev -Dquarkus.http.host=0.0.0.0 > quarkus.log 2>&1 &'
                        
                        // 2. 循环检查应用是否存活
                        // 我们检查 /q/health 端点，最多等待 120 秒 (因为第一次下载依赖很慢)
                        sh '''
                            echo "Waiting for Quarkus to start..."
                            for i in {1..24}; do
                                sleep 5
                                if curl -s http://localhost:8080/q/health > /dev/null; then
                                    echo "Quarkus is UP!"
                                    exit 0
                                fi
                                echo "Still waiting... ($i/24)"
                            done
                            echo "Timeout waiting for Quarkus!"
                            cat quarkus.log
                            exit 1
                        '''

                        // 3. 运行测试
                        sh 'mvn test -Djava.net.preferIPv4Stack=true'

                    } finally {
                        // 4. 清理 (杀掉所有 java 进程，简单粗暴但有效)
                        sh 'pkill -f quarkus:dev || true'
                    }
                }
            }
        }
    }
    
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            // 保存日志以便排查
            archiveArtifacts artifacts: 'quarkus.log', allowEmptyArchive: true
        }
    }
}
