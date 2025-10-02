
# Integrating Tests into Your Dockerfile

Integrating your test suite into your Docker build process is a powerful way to ensure that only code that passes all checks gets packaged into an application image. This creates a high-quality, reliable artifact for deployment.

We can achieve this using a **multi-stage Docker build**. This approach allows us to use one stage for building and testing the application, and a second, much smaller stage for the final, production-ready image. This ensures our final image doesn't contain any build tools, source code, or test dependencies.

## Example Multi-Stage Dockerfile

Below is a `Dockerfile` that demonstrates this process. You can create this file in the root of your project.

```Dockerfile
# --- STAGE 1: Build and Test ---
# Use a base image that includes the JDK and Maven for building
FROM maven:3.9-eclipse-temurin-17-focal AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml to leverage dependency caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies. This layer is cached and only re-run if pom.xml changes.
RUN ./mvnw dependency:go-offline

# Copy the rest of your application source code
COPY src ./src

# Run all tests! The build will fail if any test does not pass.
RUN ./mvnw clean install


# --- STAGE 2: Production Image ---
# Use a minimal base image with just the Java Runtime Environment
FROM eclipse-temurin:17-jre-focal

# Set the working directory
WORKDIR /app

# Copy only the built application JAR from the 'builder' stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

```

## How It Works: The Stages Explained

### Stage 1: The `builder` Stage

1.  **`FROM maven:3.9-eclipse-temurin-17-focal AS builder`**: We start with a full-featured base image that has both JDK 17 and Maven installed. We name this stage `builder` so we can refer to it later.

2.  **`COPY .mvn/ ...` and `COPY src ...`**: We copy our project files into the container. We copy the `pom.xml` and Maven wrapper first, then run `dependency:go-offline` to download all dependencies. Because of Docker's layer caching, this step will be skipped in future builds unless the `pom.xml` file changes, which significantly speeds up the process.

3.  **`RUN ./mvnw clean install`**: This is the crucial step for testing. The `install` command compiles the code, runs all the tests (unit, acceptance, and Cucumber), and packages the application into a `.jar` file. **If any test fails, Maven will exit with a non-zero status code, which will cause the `docker build` command to fail.** This prevents an image from being created from code that doesn't pass its tests.

### Stage 2: The Final Production Stage

1.  **`FROM eclipse-temurin:17-jre-focal`**: We start a new stage from a very lightweight base image. This image only contains the Java Runtime Environment (JRE), which is all that's needed to run our compiled application. It does not contain Maven or the JDK.

2.  **`COPY --from=builder /app/target/*.jar app.jar`**: This is the magic of multi-stage builds. We copy *only* the compiled application `.jar` file from the `builder` stage into our new, clean stage. The source code, test reports, and build dependencies from the first stage are all discarded.

3.  **`EXPOSE 8080` & `ENTRYPOINT [...]`**: We expose the correct port and set the command to run our application when a container is started from the image.

## How to Build the Image

To build the Docker image using this file, you would run the following command from your project's root directory:

```bash
docker build -t ecsdemo:latest .
```

If all your tests pass, the build will succeed, and you will have a lean, production-ready Docker image named `ecsdemo`.
