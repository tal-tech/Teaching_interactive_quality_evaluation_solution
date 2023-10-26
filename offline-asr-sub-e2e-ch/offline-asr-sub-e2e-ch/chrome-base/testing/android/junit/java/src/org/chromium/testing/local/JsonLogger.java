// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.testing.local;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.Description;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 *  Creates json file with junit test information. Format of the json file mirrors the
 *  json generated by build/android/pylib/results/json_results.py.
 */
public class JsonLogger {

    private final JSONObject mBaseJsonObject;
    private final JSONObject mBaseTestInfoJsonObject;
    private final File mOutputFile;

    public JsonLogger(File outputFile) {
        mBaseJsonObject = new JSONObject();
        mBaseTestInfoJsonObject = new JSONObject();
        mOutputFile = outputFile;

        try {
            mBaseJsonObject.put("global_tags", new JSONArray());
            mBaseJsonObject.put("all_tests", new JSONArray());
            mBaseJsonObject.put("disabled_tests", new JSONArray());
            mBaseJsonObject.put("per_iteration_data",
                                new JSONArray().put(mBaseTestInfoJsonObject));
        } catch (JSONException e) {
            System.err.println("Unable to create json output.");
        }
    }

    /**
     *  Add the results of a test run to the json output.
     */
    public void addTestResultInfo(Description test, boolean passed, long elapsedTimeMillis) {
        JSONObject testInfoJsonObject = new JSONObject();

        try {
            testInfoJsonObject.put("status", (passed ? "SUCCESS" : "FAILURE"));
            testInfoJsonObject.put("elapsed_time_ms", elapsedTimeMillis);
            testInfoJsonObject.put("output_snippet", "");
            testInfoJsonObject.put("output_snippet_base64", "");
            testInfoJsonObject.put("losless_snippet", "");

            if (mBaseTestInfoJsonObject.optJSONArray(testName(test)) == null) {
                mBaseTestInfoJsonObject.put(testName(test), new JSONArray());
                mBaseJsonObject.getJSONArray("all_tests").put(testName(test));
            }
            mBaseTestInfoJsonObject.getJSONArray(testName(test)).put(testInfoJsonObject);
        } catch (JSONException e) {
            System.err.println("Unable to log test to json output: " + testName(test));
        }
    }

    /**
     *  Writes the json output to a file.
     */
    public void writeJsonToFile() {
        try {
            PrintStream stream = new PrintStream(new FileOutputStream(mOutputFile));
            try {
                stream.print(mBaseJsonObject);
            } finally {
                try {
                    stream.close();
                } catch (RuntimeException e) {
                    System.err.println("Unable to close output file: " + mOutputFile.getPath());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + mOutputFile.getPath());
        }
    }

    private String testName(Description test) {
        return test.getClassName() + "#" + test.getMethodName();
    }
}