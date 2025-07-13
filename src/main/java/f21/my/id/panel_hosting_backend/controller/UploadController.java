package f21.my.id.panel_hosting_backend.controller;

import f21.my.id.panel_hosting_backend.model.DeployLog;
import f21.my.id.panel_hosting_backend.repository.DeployLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${deploy.root.path}")
    private String deployRoot;

    private final DeployLogRepository logRepo;

    public UploadController(DeployLogRepository logRepo) {
        this.logRepo = logRepo;
    }

    @PostMapping
    public ResponseEntity<?> handleUpload(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        String id = UUID.randomUUID().toString();

        File uploadDir = new File(deployRoot + File.separator + "uploads");
        File deployDir = new File(deployRoot + File.separator + "deployed" + File.separator + id);

        try {
            // 1. Simpan ZIP
            uploadDir.mkdirs();
            File zipFile = new File(uploadDir, filename);
            file.transferTo(zipFile);

            // 2. Ekstrak
            deployDir.mkdirs();
            unzip(zipFile.getAbsolutePath(), deployDir.getAbsolutePath());

            // 3. Jalankan perintah shell (Windows)
            String deployPath = deployDir.getAbsolutePath();

            String commands = String.join(" && ",
                    "cd /d \"" + deployPath + "\"",
                    "npm install",
                    "npm run build",
                    "pm2 start npm --name panel-hosting -- run start"
            );

            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", commands);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            String output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();

            // 4. Simpan log
            DeployLog log = new DeployLog();
            log.setFilename(filename);
            log.setMessage(output);
            log.setStatus(exitCode == 0 ? "SUCCESS" : "FAILED");
            log.setTimestamp(new Date());
            logRepo.save(log);

            return ResponseEntity.ok(log);

        } catch (Exception e) {
            DeployLog errorLog = new DeployLog();
            errorLog.setFilename(filename);
            errorLog.setMessage(e.getMessage());
            errorLog.setStatus("FAILED");
            errorLog.setTimestamp(new Date());
            logRepo.save(errorLog);

            return ResponseEntity.status(500).body(errorLog);
        }
    }

    // Gunakan Java ZipInputStream untuk ekstrak ZIP secara manual di Windows
    private void unzip(String zipFilePath, String destDir) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(new File(destDir), zipEntry);
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    // Buat parent folder jika belum ada
                    File parent = newFile.getParentFile();
                    if (!parent.exists()) parent.mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
        }
    }

    private File newFile(File destDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destDir, zipEntry.getName());

        // Prevent Zip Slip vulnerability
        String destDirPath = destDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Zip entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }
}