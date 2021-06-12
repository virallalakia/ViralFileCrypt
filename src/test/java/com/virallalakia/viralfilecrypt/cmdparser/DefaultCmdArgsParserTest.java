/*
 ******************************************************************************
 * MIT License                                                                *
 * Copyright (c) (2020 - Present) Viral Lalakia                               *
 ******************************************************************************
 */

package com.virallalakia.viralfilecrypt.cmdparser;

import com.virallalakia.viralfilecrypt.cmdparser.model.CmdArgs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DefaultCmdArgsParserTest {
  @Autowired DefaultCmdArgsParser defaultCmdParser;

  private static Stream<Arguments> parameterProviderParseCmd() {
    CmdArgs blankCmdArgs = CmdArgs.builder().build();
    return Stream.of(
        Arguments.of(blankCmdArgs, null),
        Arguments.of(blankCmdArgs, new String[] {null}),
        Arguments.of(blankCmdArgs, new String[] {""}),
        Arguments.of(blankCmdArgs, new String[] {"    "}),
        Arguments.of(blankCmdArgs, new String[] {null, null}),
        Arguments.of(blankCmdArgs, new String[] {null, ""}),
        Arguments.of(blankCmdArgs, new String[] {"", null}),
        Arguments.of(blankCmdArgs, new String[] {"", ""}),
        Arguments.of(blankCmdArgs, new String[] {"", "    "}),
        Arguments.of(blankCmdArgs, new String[] {"    ", ""}),
        Arguments.of(
            CmdArgs.builder().simpleParams(List.of("param1")).build(), new String[] {"param1"}),
        Arguments.of(
            CmdArgs.builder().simpleParams(List.of("param1", "param2")).build(),
            new String[] {"  param1", "  param2        "}),
        Arguments.of(
            CmdArgs.builder()
                .simpleParams(List.of("param1", "param2"))
                .switchParams(Map.of("-switch1", List.of()))
                .build(),
            new String[] {"  param1", "  param2        ", "-switch1"}),
        Arguments.of(
            CmdArgs.builder()
                .simpleParams(List.of("param1", "param2"))
                .switchParams(Map.of("-switch1", List.of("switch1Param1")))
                .build(),
            new String[] {"  param1", "  param2        ", "  -switch1  ", "  switch1Param1  "}),
        Arguments.of(
            CmdArgs.builder()
                .simpleParams(List.of("param1", "param2", "param3", "param-4"))
                .switchParams(
                    Map.of(
                        "-switch--1",
                        List.of("switch1Param1", "switch1-Param2"),
                        "--switch-2",
                        List.of("switch2-Param1", "switch2-Param--2", "switch2-Param---3"),
                        "-switch-3-",
                        List.of()))
                .build(),
            new String[] {
              "  param1",
              "  param2        ",
              "            param3        ",
              "      param-4        ",
              "  -switch--1  ",
              "  switch1Param1  ",
              "  switch1-Param2",
              "  --switch-2  ",
              "    switch2-Param1",
              "    switch2-Param--2",
              "    switch2-Param---3",
              "  -switch-3-  "
            }));
  }

  @ParameterizedTest
  @MethodSource("parameterProviderParseCmd")
  void testParseCmd(CmdArgs expectedCmdArgs, String... cmdArgs) {
    assertEquals(expectedCmdArgs, defaultCmdParser.parseCmdArgs(cmdArgs));
  }
}
