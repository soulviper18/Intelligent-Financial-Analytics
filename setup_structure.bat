@echo off
echo Setting up Athena Monorepo Structure...

:: 1. Rename existing demo to athena-backend
if exist demo (
    rename demo athena-backend
    echo [OK] Renamed 'demo' to 'athena-backend'
) else (
    if not exist athena-backend (
        echo [ERROR] 'demo' folder not found! Make sure you are running this next to the 'demo' folder.
        pause
        exit
    )
)

:: 2. Create ML Service Structure
if not exist athena-ml mkdir athena-ml
type nul > athena-ml\main.py
type nul > athena-ml\requirements.txt
type nul > athena-ml\Dockerfile
echo [OK] Created 'athena-ml' structure

:: 3. Create Frontend Structure
if not exist athena-frontend\public mkdir athena-frontend\public
if not exist athena-frontend\src mkdir athena-frontend\src
type nul > athena-frontend\package.json
type nul > athena-frontend\Dockerfile
type nul > athena-frontend\public\index.html
type nul > athena-frontend\src\index.js
type nul > athena-frontend\src\App.js
type nul > athena-frontend\src\index.css
echo [OK] Created 'athena-frontend' structure

:: 4. Create Orchestration file
type nul > docker-compose.yml
echo [OK] Created 'docker-compose.yml'

:: 5. Clean up Backend Source
if exist athena-backend\src\main\java\com\example (
    rmdir /s /q athena-backend\src\main\java\com\example
    echo [OK] Removed old 'com.example' package
)

mkdir athena-backend\src\main\java\com\athena\backend\config 2>nul
mkdir athena-backend\src\main\java\com\athena\backend\controller 2>nul
mkdir athena-backend\src\main\java\com\athena\backend\dto 2>nul
mkdir athena-backend\src\main\java\com\athena\backend\entity 2>nul
mkdir athena-backend\src\main\java\com\athena\backend\repository 2>nul
mkdir athena-backend\src\main\java\com\athena\backend\service 2>nul
echo [OK] Created new Backend Java packages

:: 6. Create Backend Files
type nul > athena-backend\Dockerfile
if not exist athena-backend\src\main\resources mkdir athena-backend\src\main\resources 2>nul
type nul > athena-backend\src\main\resources\application-docker.properties

echo.
echo ==============================================
echo   SUCCESS! Athena Structure is Ready.
echo ==============================================
echo.
pause
```

3.  **Save the file:**
    * Click **File** > **Save As**.
    * **Navigate** to your `Athena-Project` folder.
    * **File name:** `setup.bat` (Make sure it ends in `.bat`, not `.txt`).
    * **Save as type:** Select **All Files (*.*)** (This is crucial!).
    * Click **Save**.

4.  **Check your folder:**
    Ensure your folder looks like this **before** you run it:
    ```text
    Athena-Project/
    ├── demo/       <-- This is your existing project folder
    └── setup.bat   <-- The file you just created