/**
  * Copyright (C) 2016 Stratio (http://stratio.com)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package com.stratio.sparkta.aggregator

import java.io.{Serializable => JSerializable}

import org.apache.spark.streaming.TestSuiteBase
import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import com.stratio.sparkta.plugin.field.default.DefaultField
import com.stratio.sparkta.plugin.operator.count.CountOperator
import com.stratio.sparkta.plugin.operator.sum.SumOperator
import com.stratio.sparkta.sdk._

@RunWith(classOf[JUnitRunner])
class CubeTest extends TestSuiteBase {

  test("aggregate with time") {

    val PreserverOrder = true
    val defaultDimension = new DefaultField
    val checkpointTimeAvailability = 60000
    val checkpointGranularity = "minute"
    val eventGranularity = DateOperations.dateFromGranularity(DateTime.now(), "minute")
    val name = "cubeName"
    val timeConfig = Option(TimeConfig(eventGranularity, checkpointGranularity))

    val expiringDataConfig = ExpiringDataConfig(
      checkpointGranularity, checkpointGranularity, checkpointTimeAvailability
    )

    val cube = Cube(
      name,
      Seq(Dimension("dim1", "foo", "identity", defaultDimension)),
      Seq(new CountOperator("count", Map()),new SumOperator("sum", Map("inputField" -> "n"))),
      Option(expiringDataConfig)
    )

    testOperation(getInput, cube.aggregate, getOutput, PreserverOrder)

    def getInput: Seq[Seq[(DimensionValuesTime, InputFieldsValues)]] = Seq(Seq(
      (DimensionValuesTime("testCube",
        Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "bar")),
        timeConfig
      ),
        InputFieldsValues(Map[String, JSerializable]("n" -> 4))),

      (DimensionValuesTime("testCube",
        Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "bar")),
        timeConfig),
        InputFieldsValues(Map[String, JSerializable]("n" -> 3))),

      (DimensionValuesTime("testCube",
        Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "foo")),
        timeConfig),
        InputFieldsValues(Map[String, JSerializable]("n" -> 3)))),

      Seq(
        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "bar")),
          timeConfig),
          InputFieldsValues(Map[String, JSerializable]("n" -> 4))),

        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "bar")),
          timeConfig),
          InputFieldsValues(Map[String, JSerializable]("n" -> 3))),

        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "foo")),
          timeConfig),
          InputFieldsValues(Map[String, JSerializable]("n" -> 3)))))

    def getOutput: Seq[Seq[(DimensionValuesTime, MeasuresValues)]] = Seq(
      Seq(
        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "bar")),
          timeConfig),
          MeasuresValues(Map("count" -> Some(2L), "sum" -> Some(7L)))),

        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "foo")),
          timeConfig),
          MeasuresValues(Map("count" -> Some(1L), "sum" -> Some(3L))))),

      Seq(
        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "bar")),
          timeConfig),
          MeasuresValues(Map("count" -> Some(4L), "sum" -> Some(14L)))),

        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "foo")),
          timeConfig),
          MeasuresValues(Map("count" -> Some(2L), "sum" -> Some(6L))))))
  }

  test("aggregate without time") {

    val PreserverOrder = true
    val defaultDimension = new DefaultField
    val name = "cubeName"


    val cube = Cube(
      name,
      Seq(Dimension("dim1", "foo", "identity", defaultDimension)),
      Seq(new CountOperator("count", Map()),new SumOperator("sum", Map("inputField" -> "n")))
    )

    testOperation(getInput, cube.aggregate, getOutput, PreserverOrder)

    def getInput: Seq[Seq[(DimensionValuesTime, InputFieldsValues)]] = Seq(Seq(
      (DimensionValuesTime("testCube",
        Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "bar"))
      ),
        InputFieldsValues(Map[String, JSerializable]("n" -> 4))),

      (DimensionValuesTime("testCube",
        Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "bar"))),
        InputFieldsValues(Map[String, JSerializable]("n" -> 3))),

      (DimensionValuesTime("testCube",
        Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "foo"))),
        InputFieldsValues(Map[String, JSerializable]("n" -> 3)))),

      Seq(
        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "bar"))),
          InputFieldsValues(Map[String, JSerializable]("n" -> 4))),

        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "bar"))),
          InputFieldsValues(Map[String, JSerializable]("n" -> 3))),

        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "foo"))),
          InputFieldsValues(Map[String, JSerializable]("n" -> 3)))))

    def getOutput: Seq[Seq[(DimensionValuesTime, MeasuresValues)]] = Seq(
      Seq(
        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "bar"))),
          MeasuresValues(Map("count" -> Some(2L), "sum" -> Some(7L)))),

        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "foo"))),
          MeasuresValues(Map("count" -> Some(1L), "sum" -> Some(3L))))),

      Seq(
        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "bar"))),
          MeasuresValues(Map("count" -> Some(4L), "sum" -> Some(14L)))),

        (DimensionValuesTime("testCube",
          Seq(DimensionValue(Dimension("dim1", "foo", "identity", defaultDimension), "foo"))),
          MeasuresValues(Map("count" -> Some(2L), "sum" -> Some(6L))))))
  }
}
