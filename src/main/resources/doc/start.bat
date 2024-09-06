@ECHO OFF
start javaw -jar --add-opens java.base/java.time=ALL-UNNAMED key-cert-generator-ui.jar
