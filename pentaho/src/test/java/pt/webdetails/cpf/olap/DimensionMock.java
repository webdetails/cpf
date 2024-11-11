/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package pt.webdetails.cpf.olap;

import mondrian.olap.Annotation;
import mondrian.olap.Dimension;
import mondrian.olap.DimensionType;
import mondrian.olap.Hierarchy;
import mondrian.olap.Id;
import mondrian.olap.MatchType;
import mondrian.olap.OlapElement;
import mondrian.olap.Schema;
import mondrian.olap.SchemaReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DimensionMock implements Dimension {
  private String name, caption, description;
  private boolean visible;
  private DimensionType dimType;

  public DimensionMock( String name, String caption, boolean visible, String description,
                        DimensionType dimType ) {
    this.name = name;
    this.caption = caption;
    this.visible = visible;
    this.description = description;
    this.dimType = dimType;
  }

  @Override
  public Hierarchy[] getHierarchies() {
    List<HierarchyMock> hierarchies = new ArrayList<>();

    hierarchies.add( new HierarchyMock( "Markets", "Markets", "hierarchy '[Markets]'" ) );

    return hierarchies.toArray( new Hierarchy[ 0 ] );
  }

  @Override
  public boolean isMeasures() {
    return dimType.equals( DimensionType.MeasuresDimension );
  }

  @Override
  public DimensionType getDimensionType() {
    return dimType;
  }

  @Override
  public Schema getSchema() {
    return null;
  }

  @Override
  public boolean isHighCardinality() {
    return false;
  }

  @Override
  public Map<String, Annotation> getAnnotationMap() {
    return null;
  }

  @Override
  public String getUniqueName() {
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public OlapElement lookupChild( SchemaReader schemaReader, Id.Segment segment, MatchType matchType ) {
    return null;
  }

  @Override
  public String getQualifiedName() {
    return null;
  }

  @Override
  public String getCaption() {
    return caption;
  }

  @Override
  public String getLocalized( LocalizedProperty localizedProperty, Locale locale ) {
    return null;
  }

  @Override
  public Hierarchy getHierarchy() {
    return null;
  }

  @Override
  public Dimension getDimension() {
    return null;
  }

  @Override
  public boolean isVisible() {
    return false;
  }
}
