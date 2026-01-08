pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // 1. 编译打包，跳过测试（因为此时没有服务运行）
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                // 2. 构建 Docker 镜像
                sh 'docker compose build'
            }
        }

        stage('Deploy') {
            steps {
                // 3. 启动容器 (后台运行)
                sh 'docker compose up -d'
                
                // 4. 等待容器就绪 (重要：防止测试跑太快，服务还没起)
                script {
                    sh '''
                        echo "Waiting for Simple DTU Pay to start..."
                        timeout=60
                        while [ $timeout -gt 0 ]; do
                            if curl -s http://localhost:8080/q/health > /dev/null; then
                                echo "Service is UP!"
                                exit 0
                            fi
                            sleep 2
                            timeout=$((timeout-2))
                        done
                        echo "Timeout waiting for service!"
                        docker compose logs
                        exit 1
                    '''
                }
            }
        }

        stage('Test') {
            steps {
                // 5. 运行测试 (连接到 Docker 容器)
                sh 'mvn test'
            }
        }
    }

    post {
        always {
            // 6. 报告测试结果
            junit '**/target/surefire-reports/*.xml'
            
            // 7. 清理环境 (无论成功失败都要做)
            sh 'docker compose down'
        }
    }
}
