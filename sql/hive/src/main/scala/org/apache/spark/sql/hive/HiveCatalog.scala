/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.hive

import org.apache.spark.sql.catalyst.catalog._
import org.apache.spark.sql.hive.client.{HiveClient, HiveColumn, HiveTable, TableType}


/**
 * A persistent implementation of the system catalog using Hive.
 *
 * All public methods must be synchronized for thread-safety.
 */
private[spark] class HiveCatalog(client: HiveClient) extends Catalog {
  import Catalog._

  private def toHiveColumn(c: Column): HiveColumn = {
    HiveColumn(c.name, HiveMetastoreTypes.toMetastoreType(c.dataType), c.comment)
  }

  private def toHiveTable(db: String, t: Table): HiveTable = {
    HiveTable(
      specifiedDatabase = Some(db),
      name = t.name,
      schema = t.schema.map(toHiveColumn),
      partitionColumns = t.partitionColumns.map(toHiveColumn),
      properties = t.properties,
      serdeProperties = t.storage.serdeProperties,
      tableType = TableType.withName(t.tableType),
      location = Some(t.storage.locationUri),
      inputFormat = Some(t.storage.inputFormat),
      outputFormat = Some(t.storage.outputFormat),
      serde = Some(t.storage.serde),
      viewText = t.viewText)
  }

  // --------------------------------------------------------------------------
  // Databases
  // --------------------------------------------------------------------------

  override def createDatabase(
      dbDefinition: Database,
      ignoreIfExists: Boolean): Unit = synchronized {
    client.createDatabase(dbDefinition, ignoreIfExists)
  }

  override def dropDatabase(
      db: String,
      ignoreIfNotExists: Boolean,
      cascade: Boolean): Unit = synchronized {
    client.dropDatabase(db, ignoreIfNotExists, cascade)
  }

  /**
   * Alter an existing database. This operation does not support renaming.
   */
  override def alterDatabase(db: String, dbDefinition: Database): Unit = synchronized {
    client.alterDatabase(db, dbDefinition)
  }

  override def getDatabase(db: String): Database = synchronized {
    client.getDatabase(db)
  }

  override def databaseExists(db: String): Boolean = synchronized {
    client.getDatabaseOption(db).isDefined
  }

  override def listDatabases(): Seq[String] = synchronized {
    client.listDatabases("*")
  }

  override def listDatabases(pattern: String): Seq[String] = synchronized {
    client.listDatabases(pattern)
  }

  // --------------------------------------------------------------------------
  // Tables
  // --------------------------------------------------------------------------

  override def createTable(
      db: String,
      tableDefinition: Table,
      ignoreIfExists: Boolean): Unit = synchronized {
    client.createTable(toHiveTable(db, tableDefinition))
  }

  override def dropTable(
      db: String,
      table: String,
      ignoreIfNotExists: Boolean): Unit = synchronized {
    client.dropTable(db, table, ignoreIfNotExists)
  }

  override def renameTable(db: String, oldName: String, newName: String): Unit = synchronized {
    val hiveTable = toHiveTable(db, getTable(db, oldName)).copy(name = newName)
    client.alterTable(oldName, hiveTable)
  }

  /**
   * Alter an existing table. This operation does not support renaming.
   */
  override def alterTable(
      db: String,
      table: String,
      tableDefinition: Table): Unit = synchronized {
    client.alterTable(table, toHiveTable(db, tableDefinition))
  }

  override def getTable(db: String, table: String): Table = synchronized {
    throw new UnsupportedOperationException
  }

  override def listTables(db: String): Seq[String] = synchronized {
    throw new UnsupportedOperationException
  }

  override def listTables(db: String, pattern: String): Seq[String] = synchronized {
    throw new UnsupportedOperationException
  }

  // --------------------------------------------------------------------------
  // Partitions
  // --------------------------------------------------------------------------

  override def createPartitions(
      db: String,
      table: String,
      parts: Seq[TablePartition],
      ignoreIfExists: Boolean): Unit = synchronized {
    throw new UnsupportedOperationException
  }

  override def dropPartitions(
      db: String,
      table: String,
      parts: Seq[PartitionSpec],
      ignoreIfNotExists: Boolean): Unit = synchronized {
    throw new UnsupportedOperationException
  }

  /**
   * Alter an existing table partition and optionally override its spec.
   */
  override def alterPartition(
      db: String,
      table: String,
      spec: PartitionSpec,
      newPart: TablePartition): Unit = synchronized {
    throw new UnsupportedOperationException
  }

  override def getPartition(
      db: String,
      table: String,
      spec: PartitionSpec): TablePartition = synchronized {
    throw new UnsupportedOperationException
  }

  override def listPartitions(db: String, table: String): Seq[TablePartition] = synchronized {
    throw new UnsupportedOperationException
  }

  // --------------------------------------------------------------------------
  // Functions
  // --------------------------------------------------------------------------

  override def createFunction(
      db: String,
      funcDefinition: Function,
      ignoreIfExists: Boolean): Unit = synchronized {
    throw new UnsupportedOperationException
  }

  override def dropFunction(db: String, funcName: String): Unit = synchronized {
    throw new UnsupportedOperationException
  }

  /**
   * Alter an existing function and optionally override its name.
   */
  override def alterFunction(
      db: String,
      funcName: String,
      funcDefinition: Function): Unit = synchronized {
    throw new UnsupportedOperationException
  }

  override def getFunction(db: String, funcName: String): Function = synchronized {
    throw new UnsupportedOperationException
  }

  override def listFunctions(db: String, pattern: String): Seq[String] = synchronized {
    throw new UnsupportedOperationException
  }

}
