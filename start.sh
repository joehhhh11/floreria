#!/bin/bash
# Script de inicio para Railway con DEBUG completo
echo "🚀 Starting Floreria Application..."
echo "📁 Current directory: $(pwd)"
echo "📋 Files in current directory:"
ls -la

echo ""
echo "🔍 DEBUG: Environment Variables"
echo "DATABASE_URL: ${DATABASE_URL:-NOT_SET}"
echo "POSTGRES_USER: ${POSTGRES_USER:-NOT_SET}"
echo "POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-NOT_SET}"
echo "SPRING_DATASOURCE_URL: ${SPRING_DATASOURCE_URL:-NOT_SET}"
echo "PORT: ${PORT:-NOT_SET}"
echo "SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-NOT_SET}"

echo ""
echo "🔍 Looking for JAR file..."
if [ -f "app.jar" ]; then
    echo "✅ Found app.jar"
    ls -la app.jar
    echo "🎯 Starting application with PostgreSQL..."
    echo "⏰ $(date): Starting Java application..."
    exec java -Xmx512m -Dserver.port=${PORT:-8080} -Dspring.profiles.active=prod -jar app.jar
else
    echo "❌ app.jar not found!"
    echo "📁 Current directory contents:"
    ls -la
    echo "❌ ERROR: Application JAR not found!"
    exit 1
fi
