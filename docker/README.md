# Zookeeper + Kafka + Redpanda

## 📌 목적
kakfa 클러스터를 띄우고 redpanda 로 모니터링을 쉽게 함

## 🚀 실행 방법
### 1. 컨테이너 관리

#### 컨테이너 시작
```bash
  docker-compose up -d
```
#### 컨테이너 중지
```bash
  docker-compose stop
```

#### 컨테이너 실행
```bash
  docker-compose start
```

#### 컨테이너 중지 및 삭제
```bash
  docker-compose down
```

### 2. Redpanda 모니터링
http://localhost:9093

### 3. Kafka 관리

#### 토픽 확인
```bash
  kafka-topics --bootstrap-server localhost:9092 --describe --topic {topic-name}
```

#### 파티션 증가 (기존 파티션 수보다 큰 값을 지정)
```bash
  kafka-topics --bootstrap-server localhost:9092 --alter --topic {topic-name} --partitions {number}
```

#### 파티션 별 메시지 확인
```bash
  kafka-console-consumer --bootstrap-server localhost:9092   --topic {topic-name}  --from-beginning --partition {number}
```

## 📝 참고 자료
- [redpanda](https://blog.voidmainvoid.net/527)
