grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
    repositories {
        grailsCentral()
        mavenCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        runtime 'net.sourceforge.jtds:jtds:1.3.1'
        // runtime 'mysql:mysql-connector-java:5.1.21'
        //jackson JSON parser
        compile 'org.codehaus.jackson:jackson-core-asl:1.9.13'
        compile 'org.codehaus.jackson:jackson-mapper-asl:1.9.13'

        //xls file support
        compile (group:'org.apache.poi', name:'poi', version:'3.9')
        //xlxs file support
        compile (group:'org.apache.poi', name:'poi-ooxml', version:'3.9')
    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:2.2.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }
        compile ":jquery-ui:1.10.3"

        compile ":jprogress:0.2"
        compile ":spring-security-core:1.2.7.3"
        compile ":spring-security-cas:1.0.5"
        compile ":spring-security-eventlog:0.2"
        compile ":webxml:1.4.1"
    }
}
