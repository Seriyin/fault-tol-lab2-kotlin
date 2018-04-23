dependencies {
    implementation(kotlinModule("stdlib-jdk8", project.ext["kotlin_version"] as String))
    compile("pt.haslab","ekit",project.ext["ekit_version"] as String)
    compile(project(":lab2-mes"))
}

