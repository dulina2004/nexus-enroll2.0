# =============================================================================
# NexusEnroll 2.0 - Load Seed Data into Docker MySQL
# =============================================================================
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "  NexusEnroll 2.0 - Docker Data Seeder" -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan

# Check if nexus-mysql container is running
$container = docker ps -q -f name=nexus-mysql
if (-not $container) {
    Write-Host "Container 'nexus-mysql' is not running." -ForegroundColor Yellow
    Write-Host "Starting MySQL container via docker compose..." -ForegroundColor Yellow
    docker compose up -d mysql
    Write-Host "Waiting 10 seconds for MySQL to initialize..." -ForegroundColor Yellow
    Start-Sleep -Seconds 10
}

Write-Host "Loading seed data into Docker MySQL..." -ForegroundColor Green
$seedFile = Join-Path $PSScriptRoot "sql\02-seed-all-data.sql"

if (Test-Path $seedFile) {
    Get-Content $seedFile -Raw | docker exec -i nexus-mysql mysql -u root -ppassword
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Seed data successfully loaded into all 8 microservices databases!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Demo Credentials:" -ForegroundColor Cyan
        Write-Host "  Admin:   admin    / Password123" -ForegroundColor White
        Write-Host "  Faculty: faculty1 / Password123 (Sarah Connor)" -ForegroundColor White
        Write-Host "  Faculty: faculty2 / Password123 (Albert Einstein)" -ForegroundColor White
        Write-Host "  Student: student1 / Password123 (John Doe)" -ForegroundColor White
        Write-Host "  Student: student2 / Password123 (James Bond)" -ForegroundColor White
    } else {
        Write-Host "Error occurred while executing seed script in container." -ForegroundColor Red
    }
} else {
    Write-Host "Seed file not found at: $seedFile" -ForegroundColor Red
}
