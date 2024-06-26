# 새로운 이미지를 생성할 때 기반으로 사용할 이미지를 지정
# 베이스 이미지로 OpenJDK 17를 사용
FROM openjdk:17

# 이미지 빌드 시점에서 사용할 변수 지정
# JAR 파일을 ARG 변수로 설정
ARG JAR_FILE=build/libs/app.jar

# 호스트에 있는 파일 or 디렉토리를 Docker 이미지의 파일 시스템으로 복사
# JAR 파일을 이미지에 복사
COPY ${JAR_FILE} ./app.jar

# 컨테이너에서 사용할 환경 변수 지정
# timezome을 한국으로 변경
ENV TZ=Asiz/Seoul

# 컨테이너가 실행되었을 때 항상 실행되어야 하는 커맨드 지정
# 컨테이너 내부에서 jar 파일 실행
ENTRYPOINT ["java", "-jar", "./app.jar"]