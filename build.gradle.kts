plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.10.1"
}

group = "org.ck"
version = "0.1.0"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    localPath.set("C:\\Users\\CK\\AppData\\Local\\JetBrains\\Toolbox\\apps\\IDEA-U\\ch-1\\223.8214.52")

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("223")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
}
