/*
 ******************************************************************************
 * MIT License                                                                *
 * Copyright (c) (2020 - Present) Viral Lalakia                               *
 ******************************************************************************
 */

package com.virallalakia.viralfilecrypt.cmdparser.model;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class CmdArgs {
  private List<String> simpleParams;
  private Map<String, List<String>> switchParams;

  public int getSwitchParamsCount() {
    if (this.switchParams == null) {
      return 0;
    }
    return switchParams.size();
  }

  public boolean containsSwitchParam(String param) {
    if (this.switchParams == null) {
      return false;
    }
    return switchParams.containsKey(param);
  }

  public List<String> getSwitchParamValues(String param) {
    if (this.switchParams == null) {
      return Collections.emptyList();
    }
    return switchParams.getOrDefault(param, Collections.emptyList());
  }
}
