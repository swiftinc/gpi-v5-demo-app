package com.swift.developers.sandbox.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.swift.developers.sandbox.exception.ApiSessionException;
import com.swift.sdk.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
public class SwiftSdkUtil extends Util {

    public static JsonObject readConfigurationPropertiesJson(String configPath) throws ApiSessionException {
        JsonObject jsonObject;
        try {
            jsonObject = JsonParser.parseReader(new FileReader(configPath))
                    .getAsJsonObject();
        } catch (FileNotFoundException | JsonIOException | JsonSyntaxException ex) {
            throw new ApiSessionException(ex.getMessage(), ex);
        }

        return jsonObject;
    }

    public static JsonObject readConfigurationPropertiesYaml(String configPath) throws ApiSessionException {
        byte[] fileContent;
        JsonObject jsonObject;

        try {
            fileContent = Files.readAllBytes((new File(configPath)).toPath());
            String tmpStr = new String(fileContent, StandardCharsets.UTF_8);
            ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
            Object obj = yamlReader.readValue(tmpStr, Object.class);
            ObjectMapper jsonWriter = new ObjectMapper();

            jsonObject = JsonParser.parseReader(new StringReader(jsonWriter.writeValueAsString(obj)))
                    .getAsJsonObject();

        } catch (IOException | JsonIOException | JsonSyntaxException ex) {
            throw new ApiSessionException(ex.getMessage(), ex);
        }

        return jsonObject;
    }

    public static String getStackTrace(Throwable ex) {
        return ExceptionUtils.getStackTrace(ex);
    }

    public enum CertType {SOFT, HARD}
}
