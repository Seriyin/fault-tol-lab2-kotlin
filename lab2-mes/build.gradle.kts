dependencies {
    implementation(kotlinModule("stdlib-jdk8", project.ext["kotlin_version"] as String))
    compile("io.atomix.catalyst", "catalyst-serializer", project.ext["catalyst_version"] as String)
    compile("io.atomix.catalyst", "catalyst-transport", project.ext["catalyst_version"] as String)
    compile("org.spread","spread", project.ext["spread_version"] as String)
}

