/*
 ******************************************************************************
 * MIT License                                                                *
 * Copyright (c) (2020 - Present) Viral Lalakia                               *
 ******************************************************************************
 */

package com.virallalakia.viralfilecrypt.cmdparser;

import com.virallalakia.viralfilecrypt.cmdparser.model.CmdArgs;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("defaultCmdArgsParser")
public class DefaultCmdArgsParser implements CmdArgsParser {
  @Override
  public CmdArgs parseCmdArgs(String... cmdArgs) {
    if (cmdArgs == null || cmdArgs.length == 0) {
      return CmdArgs.builder().build();
    }

    List<String> simpleParams = new ArrayList<>();
    Map<String, List<String>> switchParams = new HashMap<>();
    String curSwitch = null;
    List<String> curSwitchParams = null;
    for (String cmdArg : cmdArgs) {
      if (!StringUtils.hasText(cmdArg)) {
        continue;
      }
      cmdArg = cmdArg.trim();
      if (cmdArg.charAt(0) == '-') {
        if (curSwitch != null) {
          switchParams.put(curSwitch, curSwitchParams);
        }
        curSwitch = cmdArg;
        curSwitchParams = new ArrayList<>();
      } else {
        if (curSwitch != null) {
          curSwitchParams.add(cmdArg);
        } else {
          simpleParams.add(cmdArg);
        }
      }
    }
    if (curSwitch != null) {
      switchParams.put(curSwitch, curSwitchParams);
    }

    CmdArgs.CmdArgsBuilder cmdArgsBuilder = CmdArgs.builder();
    if (!simpleParams.isEmpty()) {
      cmdArgsBuilder.simpleParams(simpleParams);
    }
    if (!switchParams.isEmpty()) {
      cmdArgsBuilder.switchParams(switchParams);
    }
    return cmdArgsBuilder.build();
  }
}
