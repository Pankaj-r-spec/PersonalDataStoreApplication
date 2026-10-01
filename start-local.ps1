$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendDir = Join-Path $projectRoot "backend"
$frontendDir = Join-Path $projectRoot "frontend"

$requiredCommands = @("docker", "java", "mvn", "node", "npm")
foreach ($command in $requiredCommands) {
    if (-not (Get-Command $command -ErrorAction SilentlyContinue)) {
        Write-Error "Missing required tool: $command. Install Java 21, Maven, Node 20+, npm, and Docker before running this script."
        exit 1
    }
}

Write-Host "Starting Docker infrastructure..."
Set-Location $projectRoot
docker compose up -d

Write-Host "Starting backend..."
Start-Process -FilePath "cmd" -ArgumentList "/c", "cd /d `"$backendDir`" && mvn spring-boot:run" -WorkingDirectory $backendDir

Write-Host "Starting frontend..."
Start-Process -FilePath "cmd" -ArgumentList "/c", "cd /d `"$frontendDir`" && npm install && npm run dev" -WorkingDirectory $frontendDir

Write-Host "Application startup commands launched."
Write-Host "Neo4j: http://localhost:7474"
Write-Host "Kafka UI: http://localhost:8083"
Write-Host "Frontend: http://localhost:5173"
Write-Host "Backend: http://localhost:8081/actuator/health"
