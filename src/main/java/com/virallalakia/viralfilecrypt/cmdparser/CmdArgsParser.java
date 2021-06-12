/*
 ******************************************************************************
 * MIT License                                                                *
 * Copyright (c) (2020 - Present) Viral Lalakia                               *
 ******************************************************************************
 */

package com.virallalakia.viralfilecrypt.cmdparser;

import com.virallalakia.viralfilecrypt.cmdparser.model.CmdArgs;

public interface CmdArgsParser {
  CmdArgs parseCmdArgs(String... cmdArgs);
}
