/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

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
    return new Hierarchy[0];
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
