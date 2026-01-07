pipeline {
    agent any

    tools {
        // 如果你的 Jenkins 全局工具配置里有名为 'maven-3' 和 'jdk-21' 的配置，请取消注释
        // maven 'maven-3'
        // jdk 'jdk-21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                // 编译并生成 SOAP 代码
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                script {
                    try {
                        // 1. 后台启动 Quarkus 应用
                        echo "Starting Quarkus application..."
                        // 使用 nohup 运行，并将 PID 写入文件
                        sh 'nohup mvn quarkus:dev > quarkus.log 2>&1 & echo $! > quarkus.pid'

                        // 2. 等待应用启动 (检查端口 8080)
                        echo "Waiting for application to become ready..."
                        // 循环检查，最多等待 30 次，每次 2 秒
                        sh '''
                            for i in {1..30}; do
                                if curl -s http://localhost:8080/q/health > /dev/null; then
                                    echo "Application is up!"
                                    exit 0
                                fi
                                echo "Waiting..."
                                sleep 2
                            done
                            echo "Timeout waiting for app"
                            exit 1
                        '''

                        // 3. 运行 Cucumber 测试
                        echo "Running Cucumber tests..."
                        sh 'mvn test'

                    } finally {
                        // 4. 清理：停止应用并打印日志
                        echo "Stopping application..."
                        sh 'if [ -f quarkus.pid ]; then kill $(cat quarkus.pid) || true; fi'
                        
                        echo "--- Quarkus Logs ---"
                        sh 'cat quarkus.log || true'
                    }
                }
            }
        }
    }
}
