plugins {
  java
}

tasks.named<Jar>("jar"){
  manifest {
//    attributes["Main-Class"] = "image_transformation.Menu"
    attributes["Main-Class"] = "image_transformation.ImageTransformation"
    
  }
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
//  testImplementation("org.junit.jupiter:junit-jupiter:4.13.2")

}

tasks.named<Test>("test") {
  jvmArgs("-Xmx4g")   //increase the size of memeory to 4GB, stops stack overflow.
  useJUnitPlatform()
}