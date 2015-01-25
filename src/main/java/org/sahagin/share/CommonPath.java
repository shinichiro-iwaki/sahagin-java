package org.sahagin.share;

import java.io.File;

public class CommonPath {

    public static File srcTreeFile(File reportIntermediateDataDir) {
        return new File(reportIntermediateDataDir, "srcTree");
    }

    public static File runResultRootDir(File reportIntermediateDataDir) {
        return new File(reportIntermediateDataDir, "runResults");
    }

    public static File inputCaptureRootDir(File reportIntermediateDataDir) {
        return new File(reportIntermediateDataDir, "captures");
    }

    public static File htmlReportMainFile(File reportOutputDir) {
        return new File(reportOutputDir, "index.html");
    }

    public static File methodHtmlReportRootDir(File reportOutputDir) {
        return new File(reportOutputDir, "reports");
    }

    // HTML external resource means js, css, and images
    public static File htmlExternalResourceRootDir(File reportOutputDir) {
        return reportOutputDir;
    }

    public static File htmlReportCaptureRootDir(File reportOutputDir) {
        return new File(reportOutputDir, "captures");
    }

    public static String standardAdapdaterLocaleResDirPath() {
        return "/locale/java/adapter";
    }

    public static String standardSystemLocaleResDirPath() {
        return "/locale/java/system";
    }

}
