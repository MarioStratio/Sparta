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

package com.stratio.sparkta.driver.test.helper

import java.io.{Serializable => JSerializable}

import com.stratio.sparkta.aggregator.Cube
import com.stratio.sparkta.driver.helper.SchemaHelper
import com.stratio.sparkta.sdk._
import com.stratio.sparkta.serving.core.models._
import org.apache.spark.sql.types._
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar

@RunWith(classOf[JUnitRunner])
class SchemaHelperTest extends FlatSpec with ShouldMatchers
with MockitoSugar {

  "SchemaHelperTest" should "return a list of schemas" in new CommonValues {
    val cube = Cube(cubeName, Seq(dim1, dim2), Seq(op1),
      Option(ExpiringDataConfig("minute", checkpointGranularity, 100000)))
    val cubeModel = CubeModel(cubeName, checkpointModel, Seq(dimension1Model, dimension2Model), Seq(operator1Model))
    val cubes = Seq(cube)
    val cubesModel = Seq(cubeModel)
    val tableSchema = TableSchema(
      Seq("outputName"),
      "cubeTest",
      StructType(Array(
        StructField("dim1", StringType, false),
        StructField("dim2", StringType, false),
        StructField(checkpointGranularity, TimestampType, false),
        StructField("op1", LongType, true))),
      Option("minute"))

    val res = SchemaHelper.getSchemasFromCubes(cubes, cubesModel, Seq(output1Model))

    res should be(Seq(tableSchema))
  }

  "SchemaFactorySpec" should "return a list of schemas without time" in new CommonValues {
    val cube = Cube(cubeName, Seq(dim1, dim2), Seq(op1), None)
    val cubeModel = CubeModel(cubeName, noCheckpointModel, Seq(dimension1Model, dimension2Model), Seq(operator1Model))
    val cubes = Seq(cube)
    val cubesModel = Seq(cubeModel)
    val tableSchema = TableSchema(
      Seq("outputName"),
      "cubeTest",
      StructType(Array(
        StructField("dim1", StringType, false),
        StructField("dim2", StringType, false),
        StructField("op1", LongType, true))),
      None)

    val res = SchemaHelper.getSchemasFromCubes(cubes, cubesModel, Seq(output1Model))

    res should be(Seq(tableSchema))
  }

  "SchemaFactorySpec" should "return a list of schemas with id" in new CommonValues {
    val cube = Cube(cubeName, Seq(dim1, dim2), Seq(op1), None)
    val cubeModel =
      CubeModel(cubeName, noCheckpointModel, Seq(dimension1Model, dimension2Model), Seq(operator1Model), writerModelId)
    val cubes = Seq(cube)
    val cubesModel = Seq(cubeModel)
    val tableSchema = TableSchema(
      Seq("outputName"),
      "cubeTest",
      StructType(Array(
        StructField("id", StringType, false),
        StructField("dim1", StringType, false),
        StructField("dim2", StringType, false),
        StructField("op1", LongType, true))),
      None,
      TypeOp.Timestamp,
      true)

    val res = SchemaHelper.getSchemasFromCubes(cubes, cubesModel, Seq(output1Model))

    res should be(Seq(tableSchema))
  }

  "SchemaFactorySpec" should "return a list of schemas with field id" in new CommonValues {
    val cube = Cube(cubeName, Seq(dim1, dimId), Seq(op1), None)
    val cubeModel =
      CubeModel(cubeName, noCheckpointModel, Seq(dimension1Model, dimension2Model), Seq(operator1Model), writerModelId)
    val cubes = Seq(cube)
    val cubesModel = Seq(cubeModel)
    val tableSchema = TableSchema(
      Seq("outputName"),
      "cubeTest",
      StructType(Array(
        StructField("id", StringType, false),
        StructField("dim1", StringType, false),
        StructField("op1", LongType, true))),
      None,
      TypeOp.Timestamp,
      true)

    val res = SchemaHelper.getSchemasFromCubes(cubes, cubesModel, Seq(output1Model))

    res should be(Seq(tableSchema))
  }

  "SchemaFactorySpec" should "return a list of schemas with field id but not in writer" in new CommonValues {
    val cube = Cube(cubeName, Seq(dim1, dimId), Seq(op1), None)
    val cubeModel =
      CubeModel(cubeName, noCheckpointModel, Seq(dimension1Model, dimension2Model), Seq(operator1Model))
    val cubes = Seq(cube)
    val cubesModel = Seq(cubeModel)
    val tableSchema = TableSchema(
      Seq("outputName"),
      "cubeTest",
      StructType(Array(
        StructField("dim1", StringType, false),
        StructField("id", StringType, false),
        StructField("op1", LongType, true))),
      None,
      TypeOp.Timestamp,
      false)

    val res = SchemaHelper.getSchemasFromCubes(cubes, cubesModel, Seq(output1Model))

    res should be(Seq(tableSchema))
  }

  "SchemaFactorySpec" should "return a list of schemas with field id and timeDimension with DateFormat" in
    new CommonValues {
      val cube = Cube(cubeName, Seq(dim1, dim2), Seq(op1),
        Option(ExpiringDataConfig("minute", checkpointGranularity, 100000)))
      val cubeModel = CubeModel(cubeName,
        checkpointModel,
        Seq(dimension1Model, dimension2Model),
        Seq(operator1Model),
        writerModelTimeDate
      )
      val cubes = Seq(cube)
      val cubesModel = Seq(cubeModel)
      val tableSchema = TableSchema(
        Seq("outputName"),
        "cubeTest",
        StructType(Array(
          StructField("id", StringType, false),
          StructField("dim1", StringType, false),
          StructField("dim2", StringType, false),
          StructField(checkpointGranularity, DateType, false),
          StructField("op1", LongType, true))),
        Option("minute"),
        TypeOp.Date,
        true)

      val res = SchemaHelper.getSchemasFromCubes(cubes, cubesModel, Seq(output1Model))

      res should be(Seq(tableSchema))
    }

  "SchemaFactorySpec" should "return a list of schemas with field id and timeDimension with DateFormat and measure" in
    new CommonValues {
      val cube = Cube(cubeName, Seq(dim1, dim2), Seq(op1),
        Option(ExpiringDataConfig("minute", checkpointGranularity, 100000)))
      val cubeModel = CubeModel(cubeName,
        checkpointModel,
        Seq(dimension1Model, dimension2Model),
        Seq(operator1Model),
        writerModelTimeDateAndMeasure
      )
      val cubes = Seq(cube)
      val cubesModel = Seq(cubeModel)
      val tableSchema = TableSchema(
        Seq("outputName"),
        "cubeTest",
        StructType(Array(
          StructField("id", StringType, false),
          StructField("dim1", StringType, false),
          StructField("dim2", StringType, false),
          StructField(checkpointGranularity, DateType, false),
          StructField("measureName", StringType, true),
          StructField("op1", LongType, true))),
        Option("minute"),
        TypeOp.Date,
        true)

      val res = SchemaHelper.getSchemasFromCubes(cubes, cubesModel, Seq(output1Model))

      res should be(Seq(tableSchema))
    }

  class OperatorTest(name: String, properties: Map[String, JSerializable]) extends Operator(name, properties) {

    override val defaultTypeOperation = TypeOp.Long

    override val writeOperation = WriteOp.Inc

    override val defaultCastingFilterType = TypeOp.Number

    override def processMap(inputFields: InputFieldsValues): Option[Any] = {
      None
    }

    override def processReduce(values: Iterable[Option[Any]]): Option[Long] = {
      None
    }
  }

  class DimensionTypeTest extends DimensionType {

    override val operationProps: Map[String, JSerializable] = Map()

    override val properties: Map[String, JSerializable] = Map()

    override val defaultTypeOperation = TypeOp.String

    override def precisionValue(keyName: String, value: JSerializable): (Precision, JSerializable) = {
      val precision = DimensionType.getIdentity(getTypeOperation, defaultTypeOperation)
      (precision, TypeOp.transformValueByTypeOp(precision.typeOp, value))
    }

    override def precision(keyName: String): Precision =
      DimensionType.getIdentity(getTypeOperation, defaultTypeOperation)
  }

  trait CommonValues {

    val dim1: Dimension = Dimension("dim1", "field1", "", new DimensionTypeTest)
    val dim2: Dimension = Dimension("dim2", "field2", "", new DimensionTypeTest)
    val dimId: Dimension = Dimension("id", "field2", "", new DimensionTypeTest)
    val op1: Operator = new OperatorTest("op1", Map())
    val dimension1Model = DimensionModel(
      "dim1",
      "field1",
      DimensionType.IdentityName,
      DimensionType.DefaultDimensionClass,
      Some(Map())
    )
    val dimension2Model = DimensionModel(
      "dim2",
      "field2",
      DimensionType.IdentityName,
      DimensionType.DefaultDimensionClass,
      Some(Map())
    )
    val dimensionId = DimensionModel(
      "id",
      "field2",
      DimensionType.IdentityName,
      DimensionType.DefaultDimensionClass,
      Some(Map())
    )
    val operator1Model = OperatorModel("Count", "op1", Map())
    val output1Model = PolicyElementModel("outputName", "MongoDb", Map())
    val checkpointModel = CheckpointModel("minute", checkpointGranularity, None, 10000)
    val noCheckpointModel = CheckpointModel("none", checkpointGranularity, None, 10000)
    val writerModelId = Option(WriterModel(Seq("outputName"), None, None, Option(true)))
    val writerModelTimeDate = Option(WriterModel(Seq("outputName"), None, Option("date"), Option(true)))
    val writerModelTimeDateAndMeasure =
      Option(WriterModel(Seq("outputName"), Option("measureName:1"), Option("date"), Option(true)))
    val checkpointAvailable = 60000
    val checkpointGranularity = "minute"
    val cubeName = "cubeTest"
  }

}
