param(
    [string]$Service = "",
    [switch]$Clean
)

$services = @("BlackCoffeeAuth", "BlackCoffeeClub", "BlackCoffeeEvent", "BlackCoffeeLog", "BlackCoffeeNotification", "BlackCoffeeSpace")

function Process-Service {
    param([string]$ServiceName)
    
    Write-Host "Processing $ServiceName..." -ForegroundColor Green
    
    if (-not (Test-Path $ServiceName)) {
        Write-Host "Cartella $ServiceName non trovata!" -ForegroundColor Red
        return
    }
    
    # 1. Preparazione cartella expand
    $expandPath = Join-Path $ServiceName "expand"
    if (Test-Path $expandPath) {
        Remove-Item "$expandPath\*" -Recurse -Force -ErrorAction SilentlyContinue
    } else {
        New-Item -ItemType Directory -Path $expandPath -Force | Out-Null
    }
    
    # 2. Copia del FAT JAR (NON l'original)
    # Rimosso .original perché il Dockerfile vuole BOOT-INF/classes
    Write-Host "  Copying Spring Boot Fat JAR..." -ForegroundColor Yellow
    $jarSource = "$ServiceName\target\$ServiceName-0.0.1-SNAPSHOT.jar"
    $jarDest = "$expandPath\$ServiceName-0.0.1-SNAPSHOT.jar"
    
    if (-not (Test-Path $jarSource)) {
        Write-Host "  ERROR: Fat JAR not found in $jarSource. Hai fatto 'mvn package'?" -ForegroundColor Red
        return
    }
    
    Copy-Item $jarSource $jarDest -Force
    
    # 3. Estrazione
    Write-Host "  Extracting structure for Docker..." -ForegroundColor Yellow
    Push-Location $expandPath
    try {
        # Estraiamo il contenuto
        jar -xf (Split-Path $jarDest -Leaf)
        
        # 4. PULIZIA: Rimuoviamo il file JAR per evitare che il Dockerfile si confonda
        # o copi file inutili. Lasciamo solo BOOT-INF, META-INF, ecc.
        Remove-Item (Split-Path $jarDest -Leaf) -Force
        
        Write-Host "  - $ServiceName expanded successfully!" -ForegroundColor Green
    } catch {
        Write-Host "  - ERROR during extraction" -ForegroundColor Red
    } finally {
        Pop-Location
    }
}

function Clean-All {
    Write-Host "Pulizia di tutti i servizi..." -ForegroundColor Yellow
    foreach ($svc in $services) {
        if (Test-Path "$svc\expand") {
            Remove-Item "$svc\expand\*" -Recurse -Force -ErrorAction SilentlyContinue
        }
    }
    Write-Host "Cleanup completato!" -ForegroundColor Green
}

if ($Clean) {
    Clean-All
} elseif ($Service) {
    Process-Service $Service
} else {
    foreach ($svc in $services) {
        Process-Service $svc
    }
}