#!/bin/sh


LOG_FILE="/home/simpluser/entrypoint.log"

log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

log "Starting entrypoint script..."


base_dir="${base_dir:-/home/simpluser/data}"
shapes_folder="${shapes_folder:-shapes}"
schemas_folder="${schemas_folder:-schemas}"
FINAL_SHAPES_DIR="$base_dir/$shapes_folder"
FINAL_SCHEMAS_DIR="$base_dir/$schemas_folder"

#Checking user
log "Current user: $(whoami)"
log "Before mkdir - Ownership and permissions of /home/simpluser:"
ls -ld "/home/simpluser"
log "Checking ownership and permissions of $base_dir before creation of $FINAL_SHAPES_DIR"

if [ -d "$base_dir" ]; then
    log "Before mkdir - Ownership and permissions of $base_dir:"
    ls -ld "$base_dir"
else
    log "$FINAL_SHAPES_DIR does not exist yet."
fi

#Creating Shapes Directory
log "base_dir is set to: $base_dir"
log "shapes_folder is set to: $shapes_folder"
log "Final directory for shapes: $FINAL_SHAPES_DIR"

log "Creating directory: $FINAL_SHAPES_DIR"
mkdir -p "$FINAL_SHAPES_DIR" && log "Directory created successfully: $FINAL_SHAPES_DIR"

if [ -d "/home/simpluser/tmp/shapes" ]; then
    log "Listing contents of /home/simpluser/tmp/shapes:"
    ls -la /home/simpluser/tmp/shapes

    if [ "$(ls -A /home/simpluser/tmp/shapes)" ]; then
        log "Moving /home/simpluser/tmp/shapes to $FINAL_SHAPES_DIR"
        cp -r /home/simpluser/tmp/shapes/* "$FINAL_SHAPES_DIR" && log "Move successful"

        log "Listing contents of $FINAL_SHAPES_DIR after move:"
        ls -la "$FINAL_SHAPES_DIR"
    else
        log "WARNING: /home/simpluser/tmp/shapes exists but is empty!"
    fi
else
    log "WARNING: /home/simpluser/tmp/shapes does not exist!"
fi

log "Checking ownership and permissions of $base_dir before creation of $FINAL_SCHEMAS_DIR"

if [ -d "$base_dir" ]; then
    log "Before mkdir - Ownership and permissions of $base_dir:"
    ls -ld "$base_dir"
else
    log "$FINAL_SCHEMAS_DIR does not exist yet."
fi

#Creating Schemas Directory
log "base_dir is set to: $base_dir"
log "schemas_folder is set to: $schemas_folder"
log "Final directory for schemas: $FINAL_SCHEMAS_DIR"

log "Creating directory: $FINAL_SCHEMAS_DIR"
mkdir -p "$FINAL_SCHEMAS_DIR" && log "Directory created successfully: $FINAL_SCHEMAS_DIR"

if [ -d "/home/simpluser/tmp/schemas" ]; then
    log "Listing contents of /home/simpluser/tmp/schemas:"
    ls -la /home/simpluser/tmp/schemas

    if [ "$(ls -A /home/simpluser/tmp/schemas)" ]; then
        log "Moving /home/simpluser/tmp/schemas to $FINAL_SCHEMAS_DIR"
        cp -r /home/simpluser/tmp/schemas/* "$FINAL_SCHEMAS_DIR" && log "Move successful"

        log "Listing contents of $FINAL_SCHEMAS_DIR after move:"
        ls -la "$FINAL_SCHEMAS_DIR"
    else
        log "WARNING: /home/simpluser/tmp/schemas exists but is empty!"
    fi
else
    log "WARNING: /home/simpluser/tmp/schemas does not exist!"
fi


log "Entrypoint script execution completed."
