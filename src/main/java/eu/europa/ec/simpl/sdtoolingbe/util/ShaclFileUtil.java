package eu.europa.ec.simpl.sdtoolingbe.util;

import eu.europa.ec.simpl.data1.common.constant.CommonConstants;
import eu.europa.ec.simpl.data1.common.util.LogUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

/**
 * @deprecated to be removed when SchemaController V1 will be removed
 */
@Deprecated(since = "1.22.0", forRemoval = true)
@Log4j2
public final class ShaclFileUtil {

    private static final Path SHAPES_DIR = Paths.get("data", "shapes");

    public static final String JSON_FILE_ENDING = ".json";
    public static final String TTL_FILE_ENDING = ".ttl";

    // Whitelisting: allowed file extensions
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(JSON_FILE_ENDING, TTL_FILE_ENDING);

    // Whitelisting: pattern for safe file/directory names (alphanumeric, hyphens, underscores, dots)
    // Must start with alphanumeric character to prevent hidden files (starting with dot)
    private static final Pattern SAFE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9._-]*$");

    // Maximum length for path components to prevent DoS attacks
    private static final int MAX_COMPONENT_LENGTH = 255;

    private static final String INVALID_PREFIX = "Invalid ";

    private ShaclFileUtil() {}

    public static File getShapesDir() {
        return SHAPES_DIR.toFile();
    }

    public static File getEcosystemShapesDir() {
        // Validate all input parameters
        validatePathComponent(CommonConstants.ECOSYSTEM, "ecosystem");
        return getShapesDir()
                .toPath()
                .resolve(CommonConstants.ECOSYSTEM)
                .normalize()
                .toFile();
    }

    public static String getTtlFromFilename(String shapeFileName) throws IOException {
        String result = null;
        File shapesDir = getShapesDir();
        for (File file : Objects.requireNonNullElse(shapesDir.listFiles(), new File[0])) {
            if (file.isDirectory()) {
                result = getTtlFromFilename(file, shapeFileName);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    private static String getTtlFromFilename(File shapesDir, String shapeFileName) throws IOException {
        log.info("getTtlFromFilename() for shapeFileName '{}'", shapeFileName);
        File[] files = shapesDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals(shapeFileName)) {
                    log.info("getTtlFromFilename(): file found with name '{}'", shapeFileName);
                    return FileUtils.readFileToString(file, Charset.defaultCharset());
                }
            }
            log.warn("getTtlFromFilename(): no file found with name '{}'", shapeFileName);
        } else {
            log.warn("getTtlFromFilename(): directory '{}' is empty or does not exist", shapesDir);
        }
        return null;
    }

    /**
     * Get a validated file path within the shapes' directory.
     *
     * @param ecosystem  the ecosystem name
     * @param schemaType the schema type
     * @param filename   the filename (must include allowed extension)
     * @return validated Path object
     * @throws SecurityException        if path validation fails
     * @throws IllegalArgumentException if input parameters are invalid
     */
    public static Path getShapesFilePath(String ecosystem, String schemaType, String filename) {
        try {
            // Validate all input parameters
            validatePathComponent(ecosystem, "ecosystem");
            validatePathComponent(schemaType, "schemaType");
            validateFilename(filename, "filename");

            // Get the canonical base directory
            Path baseDir = getCanonicalPath(getShapesDir().toPath());

            // Construct the target path
            Path targetPath = baseDir.resolve(ecosystem).resolve(schemaType).resolve(filename);

            // Convert to canonical path and verify it's within the base directory
            Path canonicalTargetPath = getCanonicalPath(targetPath);

            if (!isWithinDirectory(canonicalTargetPath, baseDir)) {
                log.warn(
                        "Path traversal attempt detected: ecosystem={}, schemaType={}, filename={}",
                        LogUtil.sanitize(ecosystem, schemaType, filename));
                throw new SecurityException("Access denied: path is outside allowed directory");
            }

            log.debug("Validated path: {}", canonicalTargetPath);
            return canonicalTargetPath;

        } catch (SecurityException | IllegalArgumentException e) {
            // Re-throw security and validation exceptions
            throw e;
        }
    }

    /**
     * Validate a path component using whitelisting approach.
     * Throws SecurityException for security violations and IllegalArgumentException for basic validation errors.
     */
    private static void validatePathComponent(String value, String name) {
        // Basic validation: null or empty
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(INVALID_PREFIX + name + ": cannot be null or empty");
        }

        // Basic validation: length check
        if (value.length() > MAX_COMPONENT_LENGTH) {
            throw new IllegalArgumentException(INVALID_PREFIX + name + ": exceeds maximum length");
        }

        // Security check: path traversal sequences
        if (value.contains("..") || value.contains("/") || value.contains("\\")) {
            log.warn("Path traversal attempt detected in {}: {}", name, LogUtil.sanitize(value));
            throw new SecurityException(INVALID_PREFIX + name + ": contains illegal path traversal characters");
        }

        // Security check: whitelist validation - only allow safe alphanumeric characters
        if (!SAFE_NAME_PATTERN.matcher(value).matches()) {
            log.warn("Invalid characters detected in {}: {}", name, LogUtil.sanitize(value));
            throw new SecurityException(INVALID_PREFIX + name
                    + ": must contain only alphanumeric characters, hyphens, underscores, and dots");
        }
    }

    /**
     * Validate filename with extension whitelisting.
     * Throws SecurityException if file extension is not allowed.
     */
    private static void validateFilename(String filename, String name) {
        validatePathComponent(filename, name);

        // Security check: whitelist file extension
        boolean hasAllowedExtension = ALLOWED_EXTENSIONS.stream()
                .anyMatch(ext -> filename.toLowerCase().endsWith(ext));

        if (!hasAllowedExtension) {
            log.warn("Unauthorized file extension detected: {}", LogUtil.sanitize(filename));
            throw new SecurityException(
                    INVALID_PREFIX + name + ": file extension not allowed. Allowed extensions: " + ALLOWED_EXTENSIONS);
        }
    }

    /**
     * Get canonical path with proper error handling.
     */
    private static Path getCanonicalPath(Path path) {
        try {
            return path.toRealPath();
        } catch (IOException e) {
            // If file doesn't exist yet, normalize the path
            try {
                return path.toAbsolutePath().normalize();
            } catch (Exception ex) {
                log.error("Failed to resolve canonical path", ex);
                throw new SecurityException("Unable to resolve file path", ex);
            }
        }
    }

    /**
     * Verify that the target path is within the allowed base directory.
     */
    private static boolean isWithinDirectory(Path targetPath, Path baseDir) {
        try {
            Path normalizedTarget = targetPath.normalize().toAbsolutePath();
            Path normalizedBase = baseDir.normalize().toAbsolutePath();
            return normalizedTarget.startsWith(normalizedBase);
        } catch (Exception e) {
            log.error("Error verifying directory containment", e);
            return false;
        }
    }
}
