/*!
 * Copyright 2024 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
import mondrian.olap.Formula;
import mondrian.olap.Hierarchy;
import mondrian.olap.Id;
import mondrian.olap.Level;
import mondrian.olap.MatchType;
import mondrian.olap.Member;
import mondrian.olap.OlapElement;
import mondrian.olap.OlapElementBase;
import mondrian.olap.SchemaReader;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HierarchyMock extends OlapElementBase implements Hierarchy {
  private final String name;
  private final String qualifiedName;

  public HierarchyMock( String name, String caption, String qualifiedName ) {
    this.name = name;
    this.caption = caption;
    this.qualifiedName = qualifiedName;
  }

  @Override
  public Level[] getLevels() {
    List<LevelMock> levels = new ArrayList<>();

    levels.add( new LevelMock( "Territory", "Territory", "[Markets].[Territory]", 1 ) );

    return levels.toArray( new Level[ 0 ] );
  }

  @Override
  public Member getDefaultMember() {
    return null;
  }

  @Override
  public Member getAllMember() {
    return new RolapMemberMock( "All Markets", "[Markets].[All Markets]", "member with type all",
      Member.MemberType.ALL );
  }

  @Override
  public Member getNullMember() {
    return null;
  }

  @Override
  public boolean hasAll() {
    return false;
  }

  @Override
  public Member createMember( Member member, Level level, String s, Formula formula ) {
    return null;
  }

  @Override
  public String getUniqueNameSsas() {
    return "";
  }

  @Override
  public String getDisplayFolder() {
    return "";
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
