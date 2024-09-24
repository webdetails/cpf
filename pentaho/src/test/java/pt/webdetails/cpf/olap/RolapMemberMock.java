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
