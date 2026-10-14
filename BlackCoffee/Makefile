SHELL := pwsh.exe
.SHELLFLAGS := -NoProfile -Command

SERVICES := BlackCoffeeAuth BlackCoffeeClub BlackCoffeeEvent BlackCoffeeLog BlackCoffeeNotification BlackCoffeeSpace

.PHONY: all clean expand-all $(SERVICES)

all: $(SERVICES)

$(SERVICES):
	@Write-Host "Processing $@ ..." -ForegroundColor Green
	@Push-Location $@
	@Write-Host "Cleaning expand directory..." -ForegroundColor Yellow
	@if (Test-Path expand) { Remove-Item expand\* -Recurse -Force -ErrorAction SilentlyContinue }
	@Pop-Location
	@Write-Host "Copying JAR file..." -ForegroundColor Yellow
	@Copy-Item .\$@\target\$@-0.0.1-SNAPSHOT.jar.original .\$@\expand\$@-0.0.1-SNAPSHOT.jar -Force
	@Write-Host "Extracting JAR..." -ForegroundColor Yellow
	@Push-Location $@\expand
	@jar -xvf .\$@-0.0.1-SNAPSHOT.jar
	@Pop-Location
	@Write-Host "$@ completed!" -ForegroundColor Green

clean:
	@foreach ($$service in @($(SERVICES))) { `
		Write-Host "Cleaning $$service..." -ForegroundColor Yellow; `
		if (Test-Path $$service\expand) { `
			Push-Location $$service\expand; `
			Remove-Item * -Recurse -Force -ErrorAction SilentlyContinue; `
			Pop-Location; `
		} `
	}
	@Write-Host "Cleanup completed!" -ForegroundColor Green

.DEFAULT_GOAL := all
