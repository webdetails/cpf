/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package pt.webdetails.cpf.olap;

import mondrian.olap.Annotation;
import mondrian.olap.Dimension;
import mondrian.olap.Hierarchy;
import mondrian.olap.Id;
import mondrian.olap.Level;
import mondrian.olap.LevelType;
import mondrian.olap.MatchType;
import mondrian.olap.OlapElement;
import mondrian.olap.OlapElementBase;
import mondrian.olap.Property;
import mondrian.olap.SchemaReader;
import mondrian.spi.MemberFormatter;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class LevelMock extends OlapElementBase implements Level {
  private final String name;
  private final String qualifiedName;
  private final int depth;

  public LevelMock( String name, String caption, String qualifiedName, int depth ) {
    this.name = name;
    this.caption = caption;
    this.qualifiedName = qualifiedName;
    this.depth = depth;
  }

  @Override
  public int getDepth() {
    return depth;
  }

  @Override
  public Level getChildLevel() {
    return null;
  }

  @Override
  public Level getParentLevel() {
    return null;
  }

  @Override
  public boolean isAll() {
    return false;
  }

  @Override
  public boolean areMembersUnique() {
    return false;
  }

  @Override
  public LevelType getLevelType() {
    return null;
  }

  @Override
  public Property[] getProperties() {
    return new Property[ 0 ];
  }

  @Override
  public Property[] getInheritedProperties() {
    return new Property[ 0 ];
  }

  @Override
  public MemberFormatter getMemberFormatter() {
    return null;
  }

  @Override
  public int getApproxRowCount() {
    return 0;
  }

  @Override
  public Map<String, Annotation> getAnnotationMap() {
    return Map.of();
  }

  @Override
  protected Logger getLogger() {
    return null;
  }

  @Override
  public String getUniqueName() {
    return "";
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return "";
  }

  @Override
  public OlapElement lookupChild( SchemaReader schemaReader, Id.Segment segment, MatchType matchType ) {
    return null;
  }

  @Override
  public String getQualifiedName() {
    return qualifiedName;
  }

  @Override
  public Hierarchy getHierarchy() {
    return null;
  }

  @Override
  public Dimension getDimension() {
    return null;
  }
}
