# Script to split OpenAPI files by tier based on endpoint configuration
# Reads openapi-v*.json files and creates:
# - openapi-tier1-v*.json (endpoints matching tier1Endpoints list, if defined)
# - openapi-tier2-v*.json (endpoints matching tier2Endpoints list, if defined)
# - openapi-v*.json (all other endpoints not in tier1 or tier2)

# Accept parameters from command line (called from batch file)
param(
    [string]$projectName = "",
    [string]$tier1EndpointsString = "",
    [string]$tier2EndpointsString = ""
)

# Set the correct encoding for special characters
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# Set standard colors for batch-like experience
$InfoColor = "White"      # Standard information
$SuccessColor = "Green"   # Success messages
$WarningColor = "Yellow"  # Warnings and cautions
$ErrorColor = "Red"       # Errors
$HighlightColor = "Cyan"  # Highlights and important information

# Function to extract all schema references from an object recursively
function Get-SchemaReferences {
    param (
        [Parameter(Mandatory=$true)]
        $Object,
        [Parameter(Mandatory=$false)]
        [System.Collections.Generic.HashSet[string]]$References = (New-Object 'System.Collections.Generic.HashSet[string]')
    )

    if ($null -eq $Object) {
        return $References
    }

    # Handle different object types
    if ($Object -is [string]) {
        # Check if it's a reference string
        if ($Object -match '#/components/schemas/(.+)') {
            [void]$References.Add($Matches[1])
        }
    }
    elseif ($Object -is [System.Management.Automation.PSCustomObject]) {
        # Check for $ref property
        if ($Object.PSObject.Properties.Name -contains '$ref') {
            $refValue = $Object.'$ref'
            if ($refValue -match '#/components/schemas/(.+)') {
                [void]$References.Add($Matches[1])
            }
        }

        # Recursively check all properties
        foreach ($prop in $Object.PSObject.Properties) {
            Get-SchemaReferences -Object $prop.Value -References $References | Out-Null
        }
    }
    elseif ($Object -is [System.Collections.IEnumerable] -and $Object -isnot [string]) {
        # Recursively check all items in arrays
        foreach ($item in $Object) {
            Get-SchemaReferences -Object $item -References $References | Out-Null
        }
    }

    return $References
}

# Function to extract schema and all its dependencies recursively
function Get-SchemaWithDependencies {
    param (
        [Parameter(Mandatory=$true)]
        [string]$SchemaName,
        [Parameter(Mandatory=$true)]
        $AllSchemas,
        [Parameter(Mandatory=$false)]
        [System.Collections.Generic.HashSet[string]]$Collected = (New-Object 'System.Collections.Generic.HashSet[string]')
    )

    # If already collected, skip
    if ($Collected.Contains($SchemaName)) {
        return $Collected
    }

    # Add this schema
    [void]$Collected.Add($SchemaName)

    # Get the schema object
    if ($AllSchemas.PSObject.Properties.Name -contains $SchemaName) {
        $schema = $AllSchemas.$SchemaName

        # Find all references in this schema
        $refs = Get-SchemaReferences -Object $schema

        # Recursively get dependencies
        foreach ($ref in $refs) {
            Get-SchemaWithDependencies -SchemaName $ref -AllSchemas $AllSchemas -Collected $Collected | Out-Null
        }
    }

    return $Collected
}

Write-Host ""
Write-Host "----------------------------------------------------------------------" -ForegroundColor $HighlightColor
Write-Host "|  SPLITTING OPENAPI BY TIER                                         |" -ForegroundColor $HighlightColor
Write-Host "----------------------------------------------------------------------" -ForegroundColor $HighlightColor
Write-Host ""

# Convert string parameters to arrays (split by comma or semicolon)
$tier1Endpoints = @()
$tier1All = $false
if ($tier1EndpointsString) {
    if ($tier1EndpointsString.Trim() -eq "ALL") {
        $tier1All = $true
    } else {
        $tier1Endpoints = $tier1EndpointsString -split '[,;]' | ForEach-Object { $_.Trim() } | Where-Object { $_ -ne "" }
    }
}

$tier2Endpoints = @()
$tier2All = $false
if ($tier2EndpointsString) {
    if ($tier2EndpointsString.Trim() -eq "ALL") {
        $tier2All = $true
    } else {
        $tier2Endpoints = $tier2EndpointsString -split '[,;]' | ForEach-Object { $_.Trim() } | Where-Object { $_ -ne "" }
    }
}

Write-Host "Configuration received from caller script:" -ForegroundColor $InfoColor

# Ensure projectName is set, use empty string if not defined
if (-not $projectName) {
    $projectName = ""
    Write-Host "Project name: Not set (using default naming)" -ForegroundColor $InfoColor
} else {
    Write-Host "Project name: $projectName" -ForegroundColor $HighlightColor
}

if ($tier1All) {
    Write-Host "Tier1 endpoints configured: ALL (all endpoints will be in tier1)" -ForegroundColor $HighlightColor
} else {
    Write-Host "Tier1 endpoints configured: $($tier1Endpoints.Count)" -ForegroundColor $InfoColor
    if ($tier1Endpoints.Count -gt 0) {
        Write-Host "  - $($tier1Endpoints -join ', ')" -ForegroundColor $InfoColor
    }
}

if ($tier2All) {
    Write-Host "Tier2 endpoints configured: ALL (all endpoints will be in tier2)" -ForegroundColor $HighlightColor
} else {
    Write-Host "Tier2 endpoints configured: $($tier2Endpoints.Count)" -ForegroundColor $InfoColor
    if ($tier2Endpoints.Count -gt 0) {
        Write-Host "  - $($tier2Endpoints -join ', ')" -ForegroundColor $InfoColor
    }
}
Write-Host ""

# Use current directory (caller script will change to correct directory)
$workingDir = Get-Location
Write-Host "Working directory: $workingDir" -ForegroundColor $InfoColor

# Find OpenAPI files in current directory
$openApiFiles = Get-ChildItem -Filter "openapi-v*.json" -File

if ($openApiFiles.Count -eq 0) {
    Write-Host "No openapi-v*.json files found in the current directory." -ForegroundColor $WarningColor
    exit 1
}
else {
    Write-Host "Found $($openApiFiles.Count) OpenAPI file(s) to process." -ForegroundColor $SuccessColor
    Write-Host ""
}

$openApiFiles | ForEach-Object {
    $originalFile = $_.FullName
    $originalFileName = $_.Name
    Write-Host "Processing: $originalFileName" -ForegroundColor $InfoColor
    Write-Host "from: $originalFile" -ForegroundColor $InfoColor

    try {
        # Load the JSON file
        $json = Get-Content $originalFile -Raw | ConvertFrom-Json
    }
    catch {
        Write-Host "Error parsing JSON file: $originalFileName" -ForegroundColor $ErrorColor
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor $ErrorColor
        exit 1
    }

    # Extract version from filename
    $version = ""
    if ($originalFileName -match "openapi-v(\d+)\.json$") {
        $version = "v$($Matches[1])"
        Write-Host "Version detected: $version" -ForegroundColor $InfoColor
    }
    Write-Host ""

    # Initialize tier1, tier2 and main path collections
    $tier1Paths = @{}
    $tier2Paths = @{}
    $mainPaths = @{}
    $tier1Count = 0
    $tier2Count = 0
    $mainCount = 0

    # Initialize schema reference collections for each tier
    $tier1SchemaRefs = New-Object 'System.Collections.Generic.HashSet[string]'
    $tier2SchemaRefs = New-Object 'System.Collections.Generic.HashSet[string]'
    $mainSchemaRefs = New-Object 'System.Collections.Generic.HashSet[string]'

    # Iterate through all paths and categorize them
    foreach ($pathKey in $json.paths.PSObject.Properties.Name) {
        $isTier1 = $false
        $isTier2 = $false

        $pathObject = $json.paths.$pathKey

        # First, check for specific endpoint matches (higher priority)
        # Check if this path matches any tier2 specific endpoint
        if ($tier2Endpoints.Count -gt 0) {
            foreach ($tier2Endpoint in $tier2Endpoints) {
                if ($pathKey -eq $tier2Endpoint -or $pathKey -like "*$tier2Endpoint*" -or $pathKey -match $tier2Endpoint) {
                    $isTier2 = $true
                    break
                }
            }
        }

        # Check if this path matches any tier1 specific endpoint (only if not already tier2)
        if (-not $isTier2 -and $tier1Endpoints.Count -gt 0) {
            foreach ($tier1Endpoint in $tier1Endpoints) {
                if ($pathKey -eq $tier1Endpoint -or $pathKey -like "*$tier1Endpoint*" -or $pathKey -match $tier1Endpoint) {
                    $isTier1 = $true
                    break
                }
            }
        }

        # Then, apply ALL rules for remaining endpoints
        # If tier2 is set to ALL and endpoint is not already assigned to tier1
        if (-not $isTier1 -and -not $isTier2 -and $tier2All) {
            $isTier2 = $true
        }

        # If tier1 is set to ALL and endpoint is not already assigned
        if (-not $isTier1 -and -not $isTier2 -and $tier1All) {
            $isTier1 = $true
        }

        # Extract schema references from this path
        $pathRefs = Get-SchemaReferences -Object $pathObject

        # Add to appropriate tier collection
        if ($isTier2) {
            $tier2Paths[$pathKey] = $pathObject
            $tier2Count++
            foreach ($ref in $pathRefs) {
                [void]$tier2SchemaRefs.Add($ref)
            }
            Write-Host "[TIER2] $pathKey (refs: $($pathRefs.Count))" -ForegroundColor $HighlightColor
        } elseif ($isTier1) {
            $tier1Paths[$pathKey] = $pathObject
            $tier1Count++
            foreach ($ref in $pathRefs) {
                [void]$tier1SchemaRefs.Add($ref)
            }
            Write-Host "[TIER1] $pathKey (refs: $($pathRefs.Count))" -ForegroundColor $WarningColor
        } else {
            $mainPaths[$pathKey] = $pathObject
            $mainCount++
            foreach ($ref in $pathRefs) {
                [void]$mainSchemaRefs.Add($ref)
            }
            Write-Host "[MAIN ] $pathKey (refs: $($pathRefs.Count))" -ForegroundColor $InfoColor
        }
    }

    Write-Host ""
    Write-Host "Summary: $tier1Count tier1, $tier2Count tier2, $mainCount main endpoints" -ForegroundColor $SuccessColor
    Write-Host "Schema references - Tier1: $($tier1SchemaRefs.Count), Tier2: $($tier2SchemaRefs.Count), Main: $($mainSchemaRefs.Count)" -ForegroundColor $InfoColor
    Write-Host ""

    # Determine output directory (same as original file)
    $outputDir = Split-Path $originalFile -Parent

    # Create TIER2 file if there are tier2 endpoints
    if ($tier2Count -gt 0) {
        $tier2Json = @{
            openapi = $json.openapi
            info = $json.info
            servers = $json.servers
            paths = $tier2Paths
        }

        # Copy only relevant components (schemas with dependencies)
        if ($json.components) {
            $tier2Components = @{}

            # Copy other component types as-is (securitySchemes, parameters, etc.) - always, regardless of schema refs
            foreach ($compType in $json.components.PSObject.Properties.Name) {
                if ($compType -ne 'schemas') {
                    $tier2Components[$compType] = $json.components.$compType
                }
            }

            # Copy only required schemas (only if there are schema refs), otherwise copy all schemas
            if ($tier2SchemaRefs.Count -gt 0) {
                # Collect all required schemas with their dependencies
                $allRequiredSchemas = New-Object 'System.Collections.Generic.HashSet[string]'
                foreach ($schemaRef in $tier2SchemaRefs) {
                    Get-SchemaWithDependencies -SchemaName $schemaRef -AllSchemas $json.components.schemas -Collected $allRequiredSchemas | Out-Null
                }

                Write-Host "Collecting $($allRequiredSchemas.Count) schema(s) for TIER2: $($allRequiredSchemas -join ', ')" -ForegroundColor $InfoColor

                if ($allRequiredSchemas.Count -gt 0) {
                    $tier2Schemas = @{}
                    foreach ($schemaName in $allRequiredSchemas) {
                        if ($json.components.schemas.PSObject.Properties.Name -contains $schemaName) {
                            $tier2Schemas[$schemaName] = $json.components.schemas.$schemaName
                        }
                    }
                    $tier2Components['schemas'] = $tier2Schemas
                }
            } else {
                # No schema refs found - copy all schemas as-is
                if ($json.components.PSObject.Properties.Name -contains 'schemas') {
                    $tier2Components['schemas'] = $json.components.schemas
                    Write-Host "No schema refs found for TIER2 - copying all schemas" -ForegroundColor $InfoColor
                }
            }

            $tier2Json.components = $tier2Components
        }

        # Copy security if it exists
        if ($json.security) {
            $tier2Json.security = $json.security
        }

        # Copy tags if they exist
        if ($json.tags) {
            $tier2Json.tags = $json.tags
        }

        # Generate tier2 output filename
        if ($projectName) {
            $tier2FileName = if ($version) { "openapi-$projectName-tier2-$version.json" } else { "openapi-$projectName-tier2.json" }
        } else {
            $tier2FileName = if ($version) { "openapi-tier2-$version.json" } else { "openapi-tier2.json" }
        }
        $tier2OutFile = Join-Path $outputDir $tier2FileName

        # Save the tier2 file
        try {
            $tier2JsonString = ($tier2Json | ConvertTo-Json -Depth 100)
            [System.IO.File]::WriteAllText($tier2OutFile, $tier2JsonString, [System.Text.UTF8Encoding]::new($false))
            Write-Host "Saved: $tier2FileName ($tier2Count endpoints)" -ForegroundColor $SuccessColor
            Write-Host "to: $tier2OutFile" -ForegroundColor $InfoColor
        }
        catch {
            Write-Host "Error saving tier2 file: $tier2FileName" -ForegroundColor $ErrorColor
            Write-Host "Error: $($_.Exception.Message)" -ForegroundColor $ErrorColor
            exit 1
        }
    } else {
        Write-Host "No tier2 endpoints found - skipping tier2 file" -ForegroundColor $InfoColor
    }

    # Create TIER1 file if there are tier1 endpoints
    if ($tier1Count -gt 0) {
        $tier1Json = @{
            openapi = $json.openapi
            info = $json.info
            servers = $json.servers
            paths = $tier1Paths
        }

        # Copy only relevant components (schemas with dependencies)
        if ($json.components) {
            $tier1Components = @{}

            # Copy other component types as-is (securitySchemes, parameters, etc.) - always, regardless of schema refs
            foreach ($compType in $json.components.PSObject.Properties.Name) {
                if ($compType -ne 'schemas') {
                    $tier1Components[$compType] = $json.components.$compType
                }
            }

            # Copy only required schemas (only if there are schema refs), otherwise copy all schemas
            if ($tier1SchemaRefs.Count -gt 0) {
                # Collect all required schemas with their dependencies
                $allRequiredSchemas = New-Object 'System.Collections.Generic.HashSet[string]'
                foreach ($schemaRef in $tier1SchemaRefs) {
                    Get-SchemaWithDependencies -SchemaName $schemaRef -AllSchemas $json.components.schemas -Collected $allRequiredSchemas | Out-Null
                }

                Write-Host "Collecting $($allRequiredSchemas.Count) schema(s) for TIER1: $($allRequiredSchemas -join ', ')" -ForegroundColor $InfoColor

                # Copy only required schemas
                if ($allRequiredSchemas.Count -gt 0) {
                    $tier1Schemas = @{}
                    foreach ($schemaName in $allRequiredSchemas) {
                        if ($json.components.schemas.PSObject.Properties.Name -contains $schemaName) {
                            $tier1Schemas[$schemaName] = $json.components.schemas.$schemaName
                        }
                    }
                    $tier1Components['schemas'] = $tier1Schemas
                }
            } else {
                # No schema refs found - copy all schemas as-is
                if ($json.components.PSObject.Properties.Name -contains 'schemas') {
                    $tier1Components['schemas'] = $json.components.schemas
                    Write-Host "No schema refs found for TIER1 - copying all schemas" -ForegroundColor $InfoColor
                }
            }

            $tier1Json.components = $tier1Components
        }

        # Copy security if it exists
        if ($json.security) {
            $tier1Json.security = $json.security
        }

        # Copy tags if they exist
        if ($json.tags) {
            $tier1Json.tags = $json.tags
        }

        # Generate tier1 output filename
        if ($projectName) {
            $tier1FileName = if ($version) { "openapi-$projectName-tier1-$version.json" } else { "openapi-$projectName-tier1.json" }
        } else {
            $tier1FileName = if ($version) { "openapi-tier1-$version.json" } else { "openapi-tier1.json" }
        }
        $tier1OutFile = Join-Path $outputDir $tier1FileName

        # Save the tier1 file
        try {
            $tier1JsonString = ($tier1Json | ConvertTo-Json -Depth 100)
            [System.IO.File]::WriteAllText($tier1OutFile, $tier1JsonString, [System.Text.UTF8Encoding]::new($false))
            Write-Host "Saved: $tier1FileName ($tier1Count endpoints)" -ForegroundColor $SuccessColor
            Write-Host "to: $tier1OutFile" -ForegroundColor $InfoColor
        }
        catch {
            Write-Host "Error saving tier1 file: $tier1FileName" -ForegroundColor $ErrorColor
            Write-Host "Error: $($_.Exception.Message)" -ForegroundColor $ErrorColor
            exit 1
        }
    } else {
        Write-Host "No tier1 endpoints found - skipping tier1 file" -ForegroundColor $InfoColor
    }

    # Create MAIN file if there are main endpoints (not tier1 or tier2)
    if ($mainCount -gt 0) {
        $mainJson = @{
            openapi = $json.openapi
            info = $json.info
            servers = $json.servers
            paths = $mainPaths
        }

        # Copy only relevant components (schemas with dependencies)
        if ($json.components) {
            $mainComponents = @{}

            # Copy other component types as-is (securitySchemes, parameters, etc.) - always, regardless of schema refs
            foreach ($compType in $json.components.PSObject.Properties.Name) {
                if ($compType -ne 'schemas') {
                    $mainComponents[$compType] = $json.components.$compType
                }
            }

            # Copy only required schemas (only if there are schema refs), otherwise copy all schemas
            if ($mainSchemaRefs.Count -gt 0) {
                # Collect all required schemas with their dependencies
                $allRequiredSchemas = New-Object 'System.Collections.Generic.HashSet[string]'
                foreach ($schemaRef in $mainSchemaRefs) {
                    Get-SchemaWithDependencies -SchemaName $schemaRef -AllSchemas $json.components.schemas -Collected $allRequiredSchemas | Out-Null
                }

                Write-Host "Collecting $($allRequiredSchemas.Count) schema(s) for MAIN: $($allRequiredSchemas -join ', ')" -ForegroundColor $InfoColor

                # Copy only required schemas
                if ($allRequiredSchemas.Count -gt 0) {
                    $mainSchemas = @{}
                    foreach ($schemaName in $allRequiredSchemas) {
                        if ($json.components.schemas.PSObject.Properties.Name -contains $schemaName) {
                            $mainSchemas[$schemaName] = $json.components.schemas.$schemaName
                        }
                    }
                    $mainComponents['schemas'] = $mainSchemas
                }
            } else {
                # No schema refs found - copy all schemas as-is
                if ($json.components.PSObject.Properties.Name -contains 'schemas') {
                    $mainComponents['schemas'] = $json.components.schemas
                    Write-Host "No schema refs found for MAIN - copying all schemas" -ForegroundColor $InfoColor
                }
            }

            $mainJson.components = $mainComponents
        }

        # Copy security if it exists
        if ($json.security) {
            $mainJson.security = $json.security
        }

        # Copy tags if they exist
        if ($json.tags) {
            $mainJson.tags = $json.tags
        }

        # Generate main output filename
        if ($projectName) {
            $mainFileName = if ($version) { "openapi-$projectName-$version.json" } else { "openapi-$projectName.json" }
        } else {
            $mainFileName = if ($version) { "openapi-$version.json" } else { "openapi.json" }
        }
        $mainOutFile = Join-Path $outputDir $mainFileName

        # Save the main file
        try {
            $mainJsonString = ($mainJson | ConvertTo-Json -Depth 100)
            [System.IO.File]::WriteAllText($mainOutFile, $mainJsonString, [System.Text.UTF8Encoding]::new($false))
            Write-Host "Saved: $mainFileName ($mainCount endpoints)" -ForegroundColor $SuccessColor
            Write-Host "to: $mainOutFile" -ForegroundColor $InfoColor
        }
        catch {
            Write-Host "Error saving main file: $mainFileName" -ForegroundColor $ErrorColor
            Write-Host "Error: $($_.Exception.Message)" -ForegroundColor $ErrorColor
            exit 1
        }
    } else {
        Write-Host "No main endpoints found - skipping main file" -ForegroundColor $InfoColor
    }

    # Delete the original file after successful split
    try {
        Remove-Item -Path $originalFile -Force
        Write-Host ""
        Write-Host "Deleted original JSON file: $originalFile" -ForegroundColor $WarningColor
    }
    catch {
        Write-Host ""
        Write-Host "Error deleting JSON file: $_" -ForegroundColor $ErrorColor
    }

    Write-Host ""
}

Write-Host "----------------------------------------------------------------------" -ForegroundColor $HighlightColor
Write-Host "|  PROCESSING COMPLETE                                               |" -ForegroundColor $HighlightColor
Write-Host "----------------------------------------------------------------------" -ForegroundColor $HighlightColor
Write-Host ""

# exit success code
exit 0

