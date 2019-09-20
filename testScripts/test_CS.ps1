$CURRENT_PATH = Get-Location
$TEMP_FILENAME = "temp_data.txt"
$TESTDATA_DIR = "../testdata"
$BUILD_SCRIPTS = "../testdata/build_Scripts/build_testdata.py"

python $BUILD_SCRIPTS $TESTDATA_DIR/$TEMP_FILENAME -f

Get-Content $TESTDATA_DIR/$TEMP_FILENAME

##------------------------------------------------------------------##
## dotnet run
##------------------------------------------------------------------##

cd ../Project_CS

Write-Host "Execute ... " -NoNewline
Write-Host "dotnet run $TESTDATA_DIR/$TEMP_FILENAME 4"  -ForegroundColor Yellow
dotnet run $TESTDATA_DIR/$TEMP_FILENAME 4

cd $CURRENT_PATH
