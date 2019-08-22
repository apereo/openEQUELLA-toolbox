/*
 * Licensed to The Apereo Foundation under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * The Apereo Foundation licenses this file to you under the Apache License,
 * Version 2.0, (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apereo.openequella.tools.toolbox;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class FileListerDriver {
    private static Logger LOGGER = LogManager.getLogger(FileListerDriver.class);

    public void execute(String[] args) {
        if(args.length < 2) {
            LOGGER.error("No parameters found!  FileLister parameters - [req] config file, [req] directory(or file) to list, [opt] -useParent flag");
            return;
        }

        boolean checkParentFolder = (args.length > 2) && args[2].equals("-useParent");

        File base = new File(args[1]);
        if(base.exists() && checkParentFolder) {
            base = base.getAbsoluteFile().getParentFile();
            LOGGER.debug("Base's parent is: " + base.getAbsolutePath());
        }

        if(base.exists() && !base.isDirectory()) {
            LOGGER.error("Specific path is a file [" + base.getAbsolutePath() + "]");
            System.exit(2);
        } else if(base.exists()) {
            JSONArray files = new JSONArray();
            list(base, "", Config.get(Config.GENERAL_OS_SLASH), files);
            JSONObject res = new JSONObject();
            res.put("files", files);
            LOGGER.info(res);
        } else {
            LOGGER.error("Unable to find file [" + base.getAbsolutePath() + "]");
            System.exit(1);
        }

    }

    private static void list(File base, String path, String slash, JSONArray results) {
        LOGGER.debug("list() - Using a base of [" + base.getName() + "]");
        File[] files = base.listFiles();
        for(File f : files) {
            String tempPath = path.isEmpty() ? f.getName() : path+slash+f.getName();
            LOGGER.debug("["+tempPath+"]");
            results.put(tempPath);
            if(f.isDirectory()) {
                list(f, tempPath, slash, results);
            }
        }
    }
}