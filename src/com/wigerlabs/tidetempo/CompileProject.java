package com.wigerlabs.tidetempo;

import javax.tools.*;
import java.io.File;
import java.util.*;
import java.nio.file.*;

public class CompileProject {
    public static void main(String[] args) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        
        List<File> files = new ArrayList<>();
        Files.walk(Paths.get("src")).filter(p -> p.toString().endsWith(".java")).forEach(p -> files.add(p.toFile()));
        
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(files);
        
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        
        List<String> options = Arrays.asList("-d", "build/classes", "-cp", "lib/*");
        
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
        
        boolean success = task.call();
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.format("Error on line %d in %s%n%s%n",
                              diagnostic.getLineNumber(),
                              diagnostic.getSource().toUri(),
                              diagnostic.getMessage(null));
        }
        System.out.println("Success: " + success);
        fileManager.close();
    }
}
