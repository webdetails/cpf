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

import mondrian.rolap.RolapMemberBase;

public class RolapMemberMock extends RolapMemberBase {


  private String name, qualifiedName, caption;
  private MemberType memberType;

  public RolapMemberMock( String name, String qualifiedName, String caption, MemberType memberType ) {
    this.name = name;
    this.qualifiedName = qualifiedName;
    this.caption = caption;
    this.memberType = memberType;
  }


  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getQualifiedName() {
    return this.qualifiedName;
  }

  @Override
  public String getCaption() {
    return this.caption;
  }

  @Override
  public MemberType getMemberType() {
    return this.memberType;
  }

}
