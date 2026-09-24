@echo off
setlocal enabledelayedexpansion

set "SERVICE_DESC=Validation"
set "LOCAL_PORT=8083"
set "SERVICE_PORT=8080"

title !SERVICE_DESC! - Kubernetes Service Port Forwarding

echo.
echo ========================================================
echo   !SERVICE_DESC! - Kubernetes Service Port Forwarding
echo ========================================================

REM ---[ Set environment variables with defaults ]---------
REM Check if context is passed as parameter
if not "%1"=="" (
    set "SIMPL_VALIDATION_K8S_CONTEXT=%1"
    echo [INFO] Using K8S context from parameter: "!SIMPL_VALIDATION_K8S_CONTEXT!"
) else if "%SIMPL_VALIDATION_K8S_CONTEXT%"=="" (
    set "SIMPL_VALIDATION_K8S_CONTEXT=dev-sandbox-cat-dat"
    echo [INFO] Environment variable SIMPL_VALIDATION_K8S_CONTEXT not set.
    echo [INFO] Using default value: "!SIMPL_VALIDATION_K8S_CONTEXT!"
) else (
    echo [INFO] Using K8S context from environment variable: "!SIMPL_VALIDATION_K8S_CONTEXT!"
)

if "%SIMPL_VALIDATION_K8S_NAMESPACE%"=="" (
    set "SIMPL_VALIDATION_K8S_NAMESPACE=dataprovider01"
    echo [INFO] Environment variable SIMPL_VALIDATION_K8S_NAMESPACE not set.
    echo [INFO] Using default value: "!SIMPL_VALIDATION_K8S_NAMESPACE!"
)

if "%SIMPL_VALIDATION_K8S_SERVICE%"=="" (
    set "SIMPL_VALIDATION_K8S_SERVICE=sd-validation"
    echo [INFO] Environment variable SIMPL_VALIDATION_K8S_SERVICE not set.
    echo [INFO] Using default value: "!SIMPL_VALIDATION_K8S_SERVICE!"
)

REM ---[ Set Kubernetes context ]--------------------------
echo.
echo [INFO] Setting Kubernetes context to "%SIMPL_VALIDATION_K8S_CONTEXT%"...
kubectl config use-context "%SIMPL_VALIDATION_K8S_CONTEXT%" > nul 2>&1

if %errorlevel% neq 0 (
    echo [ERROR] Failed to set context "%SIMPL_VALIDATION_K8S_CONTEXT%".
    echo [INFO] Check available contexts using: kubectl config get-contexts
    echo [INFO] Auto closing in 5 seconds...
    timeout /t 5 /nobreak > nul
    exit
)

REM ---[ Check if port LOCAL_PORT is already in use ]------------
netstat -ano | findstr :!LOCAL_PORT! | findstr LISTENING > nul
if %errorlevel% equ 0 (
    echo.
    echo [INFO] Port forwarding is already running on port !LOCAL_PORT!.
    echo.
    echo [INFO] Auto closing in 5 seconds...
    timeout /t 5 /nobreak > nul
    exit
)

REM ---[ Start port forwarding ]---------------------------
:start
echo.
echo [INFO] Starting port forwarding !SERVICE_DESC! Kubernetes service...
echo [INFO] Context: %SIMPL_VALIDATION_K8S_CONTEXT%
echo [INFO] Namespace: %SIMPL_VALIDATION_K8S_NAMESPACE%
echo [INFO] This script exposes the !SERVICE_DESC! Service Kubernetes service on local port !LOCAL_PORT!
echo [INFO] Access Swagger UI at: http://localhost:!LOCAL_PORT!/swagger-ui/index.html
echo.

kubectl port-forward -n "%SIMPL_VALIDATION_K8S_NAMESPACE%" services/%SIMPL_VALIDATION_K8S_SERVICE% !LOCAL_PORT!:!SERVICE_PORT!

echo.
echo [INFO] Port forwarding terminated.
echo [INFO] Press any key to restart port forwarding or CTRL+C to exit.
echo.
pause > nul
goto start
