# Cleanup script to resolve Flyway and other environment issues
$migrationDir = "backend/src/main/resources/db/migration"

Write-Host "Cleaning up duplicate Flyway migrations..." -ForegroundColor Yellow

$filesToDelete = @(
    "V2__insert-initial-data.sql",
    "V2_1__insert-initial-data.sql"
)

foreach ($file in $filesToDelete) {
    $path = Join-Path $migrationDir $file
    if (Test-Path $path) {
        Remove-Item $path -Force -ErrorAction SilentlyContinue
        Write-Host "Deleted: $file" -ForegroundColor Green
    }
}

Write-Host "Cleaning target directories..." -ForegroundColor Yellow
if (Test-Path "backend/target") { Remove-Item "backend/target" -Recurse -Force }
if (Test-Path "frontend/dist") { Remove-Item "frontend/dist" -Recurse -Force }

Write-Host "Cleanup complete." -ForegroundColor Cyan