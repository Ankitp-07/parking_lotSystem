@echo off
echo Starting Parking System Web Server...
echo.
echo Server will start on: http://localhost:8080
echo.
java -cp "bin;mysql-connector-j-9.5.0.jar" api.ParkingAPI
pause
